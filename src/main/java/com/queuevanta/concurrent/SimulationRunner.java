package com.queuevanta.concurrent;

import com.queuevanta.engine.SimulationEngine;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;

/** Runs simulation ticks away from Swing's Event Dispatch Thread. */
public final class SimulationRunner implements AutoCloseable {
    private static final long TICK_INTERVAL_MILLIS = 400;

    private final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor(
                    task -> {
                        Thread worker = new Thread(task, "queuevanta-simulation");
                        worker.setDaemon(true);
                        return worker;
                    });
    private final SimulationEngine engine;
    private final Runnable onTick;
    private ScheduledFuture<?> task;

    /** Creates a runner for the engine and its EDT refresh callback. */
    public SimulationRunner(SimulationEngine engine, Runnable onTick) {
        this.engine = Objects.requireNonNull(engine);
        this.onTick = Objects.requireNonNull(onTick);
    }

    /** Starts one scheduled worker unless a task is already active. */
    public synchronized void start() {
        if (task != null && !task.isDone()) {
            return;
        }
        task =
                executor.scheduleAtFixedRate(
                        () -> {
                            engine.step();
                            SwingUtilities.invokeLater(onTick);
                        },
                        0,
                        TICK_INTERVAL_MILLIS,
                        TimeUnit.MILLISECONDS);
    }

    /** Cancels the scheduled task and pauses the engine without clearing its state. */
    public synchronized void pause() {
        if (task != null) {
            task.cancel(false);
            task = null;
        }
        engine.setPaused(true);
    }

    /** Resumes the engine and starts a worker if one is not already active. */
    public synchronized void resume() {
        engine.setPaused(false);
        start();
    }

    /** Pauses, clears simulation state, and requests an EDT refresh. */
    public synchronized void reset() {
        pause();
        engine.reset();
        SwingUtilities.invokeLater(onTick);
    }

    /** Stops scheduled work and shuts down the worker when the application closes. */
    @Override
    public synchronized void close() {
        pause();
        executor.shutdownNow();
    }
}
