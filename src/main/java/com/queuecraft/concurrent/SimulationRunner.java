package com.queuecraft.concurrent;

import com.queuecraft.engine.SimulationEngine;
import java.util.concurrent.*;
import javax.swing.SwingUtilities;

public final class SimulationRunner implements AutoCloseable {
  private final ScheduledExecutorService executor =
      Executors.newSingleThreadScheduledExecutor(
          r -> {
            Thread t = new Thread(r, "queuecraft-simulation");
            t.setDaemon(true);
            return t;
          });
  private ScheduledFuture<?> task;
  private final SimulationEngine engine;
  private final Runnable onTick;

  public SimulationRunner(SimulationEngine e, Runnable cb) {
    engine = e;
    onTick = cb;
  }

  public synchronized void start() {
    if (task != null && !task.isDone()) return;
    task =
        executor.scheduleAtFixedRate(
            () -> {
              engine.step();
              SwingUtilities.invokeLater(onTick);
            },
            0,
            400,
            TimeUnit.MILLISECONDS);
  }

  public synchronized void pause() {
    if (task != null) {
      task.cancel(false);
      task = null;
    }
    engine.setPaused(true);
  }

  public synchronized void resume() {
    engine.setPaused(false);
    start();
  }

  public synchronized void reset() {
    pause();
    engine.reset();
    SwingUtilities.invokeLater(onTick);
  }

  public synchronized void close() {
    pause();
    executor.shutdownNow();
  }
}
