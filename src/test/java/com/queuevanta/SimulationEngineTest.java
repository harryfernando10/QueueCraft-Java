package com.queuevanta;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.queuevanta.engine.SimulationEngine;
import com.queuevanta.model.CounterSnapshot;
import com.queuevanta.model.Customer;
import com.queuevanta.model.CustomerStatus;
import com.queuevanta.model.ServiceCounter;
import com.queuevanta.model.ServiceType;
import com.queuevanta.service.CounterValidator;
import com.queuevanta.service.SimulationException;
import com.queuevanta.service.SimulationStats;
import com.queuevanta.strategy.ShortestQueueStrategy;
import java.util.List;
import org.junit.jupiter.api.Test;

class SimulationEngineTest {
    @Test
    void preservesShortValidCounterNamesAndShowsCustomerServiceInItsQueue() throws Exception {
        SimulationEngine engine = new SimulationEngine();
        engine.addCounter(new ServiceCounter("yt", ServiceType.CANTEEN, 3));
        engine.generate(ServiceType.CANTEEN);

        CounterSnapshot snapshot = engine.getCounterSnapshots().get(0);

        assertEquals("yt", snapshot.name());
        assertEquals(ServiceType.CANTEEN, snapshot.serviceType());
        assertEquals(List.of("Guest 1 · Canteen"), snapshot.waitingCustomers());
        assertThrows(
                IllegalArgumentException.class,
                () -> new ServiceCounter("x", ServiceType.CANTEEN, 3));
    }

    @Test
    void customerTransitionsCalculateWaitingAndServiceTicks() {
        Customer customer = new Customer(1, "Guest 1", ServiceType.CANTEEN, 2);
        customer.startService(5);
        customer.complete(9);

        assertEquals(CustomerStatus.COMPLETED, customer.getStatus());
        assertEquals(3, customer.getWaitingTime());
        assertEquals(4, customer.getServiceDuration());
        assertThrows(IllegalStateException.class, () -> customer.complete(10));
    }

    @Test
    void shortestQueueSelectsCompatibleCounterAndBreaksTiesByName() {
        ServiceCounter westDesk = new ServiceCounter("West Desk", ServiceType.LIBRARY, 2);
        ServiceCounter eastDesk = new ServiceCounter("East Desk", ServiceType.LIBRARY, 2);
        Customer customer = new Customer(1, "Guest", ServiceType.LIBRARY, 0);
        ShortestQueueStrategy strategy = new ShortestQueueStrategy();

        assertEquals(eastDesk, strategy.chooseCounter(customer, List.of(westDesk, eastDesk)));
        eastDesk.enqueue(new Customer(2, "Guest 2", ServiceType.LIBRARY, 0));
        assertEquals(westDesk, strategy.chooseCounter(customer, List.of(westDesk, eastDesk)));
        assertNull(
                strategy.chooseCounter(
                        new Customer(3, "Guest 3", ServiceType.IT_HELP, 0),
                        List.of(westDesk, eastDesk)));
    }

    @Test
    void simulationCompletesCustomersAndCalculatesStatistics() throws Exception {
        SimulationEngine engine = new SimulationEngine();
        engine.addCounter(new ServiceCounter("Desk", ServiceType.CANTEEN, 2));
        engine.generateBatch(ServiceType.CANTEEN, 2);
        engine.setPaused(false);
        for (int index = 0; index < 5; index++) {
            engine.step();
        }

        SimulationStats stats = engine.stats();
        assertEquals(2, stats.generated());
        assertEquals(2, stats.completed());
        assertEquals(0, stats.waiting());
        assertEquals(0, stats.serving());
        assertEquals(2.0, stats.averageWait());
        assertEquals(2.0, stats.averageService());
    }

    @Test
    void emptyStatisticsAreZeroAndEmptyQueueDoesNothing() {
        SimulationEngine engine = new SimulationEngine();
        engine.setPaused(false);
        engine.step();

        SimulationStats stats = engine.stats();
        assertEquals(1, engine.getTick());
        assertEquals(0, stats.averageWait());
        assertEquals(0, stats.averageService());
        assertEquals(0, stats.maxQueue());
    }

    @Test
    void pauseResetAndCounterAvailabilityWork() throws Exception {
        SimulationEngine engine = new SimulationEngine();
        engine.addCounter(new ServiceCounter("Desk", ServiceType.LIBRARY, 1));
        assertThrows(SimulationException.class, () -> engine.generate(ServiceType.CANTEEN));

        engine.setCounterEnabled("Desk", false);
        assertThrows(SimulationException.class, () -> engine.generate(ServiceType.LIBRARY));
        engine.setCounterEnabled("Desk", true);
        engine.generate(ServiceType.LIBRARY);
        engine.step();
        assertEquals(0, engine.getTick(), "A paused simulation must not advance.");

        engine.setPaused(false);
        engine.step();
        engine.step();
        assertEquals(1, engine.stats().completed());

        engine.reset();
        assertEquals(0, engine.getTick());
        assertEquals(0, engine.stats().generated());
        assertEquals(0, engine.getCounters().get(0).getCompletedCount());
        assertTrue(engine.isPaused());
    }

    @Test
    void rejectsDuplicateCountersAndInvalidNames() throws Exception {
        SimulationEngine engine = new SimulationEngine();
        engine.addCounter(new ServiceCounter("Desk", ServiceType.IT_HELP, 2));
        assertThrows(
                SimulationException.class,
                () -> engine.addCounter(new ServiceCounter("desk", ServiceType.CANTEEN, 2)));
        assertThrows(
                SimulationException.class,
                () -> CounterValidator.create(" ", ServiceType.IT_HELP, 3, List.of()));
        assertThrows(
                SimulationException.class,
                () -> CounterValidator.create("Desk", ServiceType.IT_HELP, 3, engine.getCounters()));
        assertDoesNotThrow(
                () -> CounterValidator.create("North-1", ServiceType.IT_HELP, 3, List.of()));
    }

    @Test
    void disabledCounterKeepsQueueAndSnapshotsAreDetached() throws Exception {
        SimulationEngine engine = new SimulationEngine();
        engine.addCounter(new ServiceCounter("Desk", ServiceType.LIBRARY, 3));
        engine.generateBatch(ServiceType.LIBRARY, 3);
        engine.setPaused(false);
        engine.step();
        engine.setCounterEnabled("Desk", false);

        CounterSnapshot snapshot = engine.getCounterSnapshots().get(0);
        assertEquals(2, snapshot.waitingCustomers().size());
        assertEquals(9, snapshot.estimatedWaitTicks());
        assertEquals(3, snapshot.load());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.waitingCustomers().clear());

        engine.step();
        assertEquals(2, engine.getCounterSnapshots().get(0).waitingCustomers().size());
        engine.setCounterEnabled("Desk", true);
        for (int index = 0; index < 10; index++) {
            engine.step();
        }
        assertEquals(3, engine.stats().completed());
    }
}
