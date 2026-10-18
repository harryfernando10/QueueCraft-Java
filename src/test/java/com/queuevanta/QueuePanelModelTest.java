package com.queuevanta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.queuevanta.engine.SimulationEngine;
import com.queuevanta.model.CounterSnapshot;
import com.queuevanta.model.ServiceCounter;
import com.queuevanta.model.ServiceType;
import com.queuevanta.ui.QueuePanelModel;
import java.util.List;
import org.junit.jupiter.api.Test;

class QueuePanelModelTest {
    @Test
    void createsCounterHeadingAndServingThenFifoWaitingRows() throws Exception {
        SimulationEngine engine = new SimulationEngine();
        engine.addCounter(new ServiceCounter("Canteen", ServiceType.CANTEEN, 4));
        engine.generateBatch(ServiceType.CANTEEN, 3);
        engine.setPaused(false);
        engine.step();

        List<QueuePanelModel.Row> rows =
                QueuePanelModel.fromSnapshots(engine.getCounterSnapshots());

        assertEquals(
                List.of(
                        new QueuePanelModel.Row(
                                QueuePanelModel.Kind.HEADER, "Canteen  ·  Canteen  ·  Enabled"),
                        new QueuePanelModel.Row(
                                QueuePanelModel.Kind.CUSTOMER,
                                "Serving: Guest 1 · Canteen  ·  4 ticks left"),
                        new QueuePanelModel.Row(
                                QueuePanelModel.Kind.CUSTOMER, "Waiting 1: Guest 2 · Canteen"),
                        new QueuePanelModel.Row(
                                QueuePanelModel.Kind.CUSTOMER, "Waiting 2: Guest 3 · Canteen")),
                rows);
    }

    @Test
    void disabledAndEmptyCountersRemainExplicitInQueueView() throws Exception {
        SimulationEngine engine = new SimulationEngine();
        engine.addCounter(new ServiceCounter("Library", ServiceType.LIBRARY, 2));
        engine.setCounterEnabled("Library", false);
        List<CounterSnapshot> snapshots = engine.getCounterSnapshots();

        assertEquals(
                List.of(
                        new QueuePanelModel.Row(
                                QueuePanelModel.Kind.HEADER, "Library  ·  Library  ·  Disabled"),
                        new QueuePanelModel.Row(QueuePanelModel.Kind.CUSTOMER, "Serving: nobody"),
                        new QueuePanelModel.Row(QueuePanelModel.Kind.CUSTOMER, "Waiting line: empty")),
                QueuePanelModel.fromSnapshots(snapshots));
        assertThrows(
                NullPointerException.class,
                () -> QueuePanelModel.fromSnapshots(null));
    }
}
