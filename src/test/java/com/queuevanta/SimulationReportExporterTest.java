package com.queuevanta;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.queuevanta.model.CounterSnapshot;
import com.queuevanta.model.ServiceType;
import com.queuevanta.service.SimulationReportExporter;
import com.queuevanta.service.SimulationStats;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SimulationReportExporterTest {
    @TempDir Path temporaryDirectory;

    @Test
    void writesSummaryAndEscapesCounterNames() throws Exception {
        Path output = temporaryDirectory.resolve("summary.csv");
        var counter =
                new CounterSnapshot("North, Desk", ServiceType.LIBRARY, 4, true, null, 0, List.of(), 2);

        SimulationReportExporter.export(
                output, new SimulationStats(4, 0, 0, 4, 1.5, 3, 2), List.of(counter));

        String csv = Files.readString(output);
        assertTrue(csv.startsWith("Section,Name,Value,Unit\r\n"));
        assertTrue(csv.contains("Summary,Average wait,1.5,ticks per completed customer"));
        assertTrue(csv.contains("\"Counter North, Desk\",Service,Library,"));
        assertTrue(csv.contains("Counter North, Desk"));
    }

    @Test
    void refusesToWriteToADirectory() {
        assertThrows(
                IOException.class,
                () ->
                        SimulationReportExporter.export(
                                temporaryDirectory,
                                new SimulationStats(0, 0, 0, 0, 0, 0, 0),
                                List.of()));
    }
}
