package com.queuecraft;

import com.queuecraft.engine.SimulationEngine;
import com.queuecraft.model.*;
import com.queuecraft.service.*;
import com.queuecraft.strategy.ShortestQueueStrategy;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SimulationEngineTest {
    @Test void customerTransitionsCalculateWaitingAndServiceTicks() {
        Customer c = new Customer(1, "Guest 1", ServiceType.CANTEEN, 2);
        c.startService(5);
        c.complete(9);
        assertEquals(CustomerStatus.COMPLETED, c.getStatus());
        assertEquals(3, c.getWaitingTime());
        assertEquals(4, c.getServiceDuration());
        assertThrows(IllegalStateException.class, () -> c.complete(10));
    }

    @Test void shortestQueueSelectsCompatibleLeastLoadedCounterAndBreaksTiesByName() {
        ServiceCounter z = new ServiceCounter("Z Desk", ServiceType.LIBRARY, 2);
        ServiceCounter a = new ServiceCounter("A Desk", ServiceType.LIBRARY, 2);
        Customer c = new Customer(1, "Guest", ServiceType.LIBRARY, 0);
        ShortestQueueStrategy strategy = new ShortestQueueStrategy();
        assertEquals(a, strategy.chooseCounter(c, List.of(z, a)));
        a.enqueue(new Customer(2, "Guest 2", ServiceType.LIBRARY, 0));
        assertEquals(z, strategy.chooseCounter(c, List.of(z, a)));
        assertNull(strategy.chooseCounter(new Customer(3, "Guest 3", ServiceType.IT_HELP, 0), List.of(z, a)));
    }

    @Test void simulationCompletesCustomersAndCalculatesStatistics() throws Exception {
        SimulationEngine e = new SimulationEngine();
        e.addCounter(new ServiceCounter("Desk", ServiceType.CANTEEN, 2));
        e.generateBatch(ServiceType.CANTEEN, 2);
        e.setPaused(false);
        for (int i=0; i<5; i++) e.step();
        SimulationStats s = e.stats();
        assertEquals(2, s.generated());
        assertEquals(2, s.completed());
        assertEquals(0, s.waiting());
        assertEquals(0, s.serving());
        assertEquals(2.0, s.averageWait());
        assertEquals(2.0, s.averageService());
    }

    @Test void emptyStatisticsAreZeroAndEmptyQueueDoesNothing() {
        SimulationEngine e = new SimulationEngine();
        e.setPaused(false);
        e.step();
        SimulationStats s = e.stats();
        assertEquals(1, e.getTick());
        assertEquals(0, s.averageWait());
        assertEquals(0, s.averageService());
        assertEquals(0, s.maxQueue());
    }

    @Test void pauseResetAndCounterAvailabilityWork() throws Exception {
        SimulationEngine e = new SimulationEngine();
        e.addCounter(new ServiceCounter("Desk", ServiceType.LIBRARY, 1));
        assertThrows(SimulationException.class, () -> e.generate(ServiceType.CANTEEN));
        e.setCounterEnabled("Desk", false);
        assertThrows(SimulationException.class, () -> e.generate(ServiceType.LIBRARY));
        e.setCounterEnabled("Desk", true);
        e.generate(ServiceType.LIBRARY);
        e.step();
        assertEquals(0, e.getTick(), "paused simulation does not advance");
        e.setPaused(false);
        e.step();
        e.step();
        assertEquals(1, e.stats().completed());
        e.reset();
        assertEquals(0, e.getTick());
        assertEquals(0, e.stats().generated());
        assertEquals(0, e.getCounters().get(0).getCompletedCount());
        assertTrue(e.isPaused());
    }

    @Test void rejectsDuplicateCountersAndInvalidNames() throws Exception {
        SimulationEngine e = new SimulationEngine();
        e.addCounter(new ServiceCounter("Desk", ServiceType.IT_HELP, 2));
        assertThrows(SimulationException.class, () -> e.addCounter(new ServiceCounter("desk", ServiceType.CANTEEN, 2)));
        assertThrows(SimulationException.class, () -> CounterValidator.create(" ", ServiceType.IT_HELP, 3, List.of()));
        assertThrows(SimulationException.class, () -> CounterValidator.create("Desk", ServiceType.IT_HELP, 3, e.getCounters()));
        assertDoesNotThrow(() -> CounterValidator.create("North-1", ServiceType.IT_HELP, 3, List.of()));
    }
}
