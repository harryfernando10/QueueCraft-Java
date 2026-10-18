package com.queuecraft;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.queuecraft.engine.SimulationEngine;
import com.queuecraft.model.Customer;
import com.queuecraft.model.CustomerStatus;
import com.queuecraft.model.ServiceCounter;
import com.queuecraft.model.ServiceType;
import com.queuecraft.service.CounterValidator;
import com.queuecraft.service.SimulationException;
import com.queuecraft.service.SimulationStats;
import com.queuecraft.strategy.ShortestQueueStrategy;
import java.util.List;
import org.junit.jupiter.api.Test;

class SimulationEngineTest {
  @Test
  void customerTransitionsCalculateWaitingAndServiceTicks() {
    Customer c = new Customer(1, "Guest 1", ServiceType.CANTEEN, 2);
    c.startService(5);
    c.complete(9);
    assertEquals(CustomerStatus.COMPLETED, c.getStatus());
    assertEquals(3, c.getWaitingTime());
    assertEquals(4, c.getServiceDuration());
    assertThrows(IllegalStateException.class, () -> c.complete(10));
  }

  @Test
  void shortestQueueSelectsCompatibleLeastLoadedCounterAndBreaksTiesByName() {
    ServiceCounter z = new ServiceCounter("Z Desk", ServiceType.LIBRARY, 2);
    ServiceCounter a = new ServiceCounter("A Desk", ServiceType.LIBRARY, 2);
    Customer c = new Customer(1, "Guest", ServiceType.LIBRARY, 0);
    ShortestQueueStrategy strategy = new ShortestQueueStrategy();
    assertEquals(a, strategy.chooseCounter(c, List.of(z, a)));
    a.enqueue(new Customer(2, "Guest 2", ServiceType.LIBRARY, 0));
    assertEquals(z, strategy.chooseCounter(c, List.of(z, a)));
    assertNull(
        strategy.chooseCounter(new Customer(3, "Guest 3", ServiceType.IT_HELP, 0), List.of(z, a)));
  }

  @Test
  void simulationCompletesCustomersAndCalculatesStatistics() throws Exception {
    SimulationEngine e = new SimulationEngine();
    e.addCounter(new ServiceCounter("Desk", ServiceType.CANTEEN, 2));
    e.generateBatch(ServiceType.CANTEEN, 2);
    e.setPaused(false);
    for (int i = 0; i < 5; i++) e.step();
    SimulationStats s = e.stats();
    assertEquals(2, s.generated());
    assertEquals(2, s.completed());
    assertEquals(0, s.waiting());
    assertEquals(0, s.serving());
    assertEquals(2.0, s.averageWait());
    assertEquals(2.0, s.averageService());
  }

  @Test
  void emptyStatisticsAreZeroAndEmptyQueueDoesNothing() {
    SimulationEngine e = new SimulationEngine();
    e.setPaused(false);
    e.step();
    SimulationStats s = e.stats();
    assertEquals(1, e.getTick());
    assertEquals(0, s.averageWait());
    assertEquals(0, s.averageService());
    assertEquals(0, s.maxQueue());
  }

  @Test
  void pauseResetAndCounterAvailabilityWork() throws Exception {
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

  @Test
  void rejectsDuplicateCountersAndInvalidNames() throws Exception {
    SimulationEngine e = new SimulationEngine();
    e.addCounter(new ServiceCounter("Desk", ServiceType.IT_HELP, 2));
    assertThrows(
        SimulationException.class,
        () -> e.addCounter(new ServiceCounter("desk", ServiceType.CANTEEN, 2)));
    assertThrows(
        SimulationException.class,
        () -> CounterValidator.create(" ", ServiceType.IT_HELP, 3, List.of()));
    assertThrows(
        SimulationException.class,
        () -> CounterValidator.create("Desk", ServiceType.IT_HELP, 3, e.getCounters()));
    assertDoesNotThrow(() -> CounterValidator.create("North-1", ServiceType.IT_HELP, 3, List.of()));
  }

  @Test
  void disabledCounterKeepsItsQueueAndSnapshotWaitEstimateIsDetached() throws Exception {
    SimulationEngine engine = new SimulationEngine();
    engine.addCounter(new ServiceCounter("Desk", ServiceType.LIBRARY, 3));
    engine.generateBatch(ServiceType.LIBRARY, 3);
    engine.setPaused(false);
    engine.step();
    engine.setCounterEnabled("Desk", false);

    var snapshot = engine.getCounterSnapshots().get(0);
    assertEquals(2, snapshot.waitingCustomers().size());
    assertEquals(9, snapshot.estimatedWaitTicks());
    assertEquals(3, snapshot.load());
    assertThrows(UnsupportedOperationException.class, () -> snapshot.waitingCustomers().clear());

    engine.step();
    assertEquals(2, engine.getCounterSnapshots().get(0).waitingCustomers().size());
    engine.setCounterEnabled("Desk", true);
    for (int i = 0; i < 10; i++) engine.step();
    assertEquals(3, engine.stats().completed());
  }
}
