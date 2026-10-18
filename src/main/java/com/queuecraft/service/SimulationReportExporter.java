package com.queuecraft.service;

import com.queuecraft.model.CounterSnapshot;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Writes a compact, human-readable simulation summary in CSV format. */
public final class SimulationReportExporter {
  private SimulationReportExporter() {}

  public static void export(Path destination, SimulationStats stats, List<CounterSnapshot> counters)
      throws IOException {
    if (destination == null || Files.isDirectory(destination)) {
      throw new IOException("Choose a file path for the CSV summary.");
    }
    StringBuilder csv = new StringBuilder("Section,Name,Value,Unit\r\n");
    addMetric(csv, "Summary", "Generated", stats.generated(), "customers");
    addMetric(csv, "Summary", "Waiting", stats.waiting(), "customers");
    addMetric(csv, "Summary", "Serving", stats.serving(), "customers");
    addMetric(csv, "Summary", "Completed", stats.completed(), "customers");
    addMetric(csv, "Summary", "Average wait", stats.averageWait(), "ticks per completed customer");
    addMetric(csv, "Summary", "Average service", stats.averageService(), "ticks per completed customer");
    addMetric(csv, "Summary", "Maximum counter load", stats.maxQueue(), "customers");
    for (CounterSnapshot counter : counters) {
      addMetric(csv, "Counter " + counter.name(), "Service", counter.serviceType().getLabel(), "");
      addMetric(csv, "Counter " + counter.name(), "Enabled", counter.enabled(), "");
      addMetric(csv, "Counter " + counter.name(), "Current load", counter.load(), "customers");
      addMetric(csv, "Counter " + counter.name(), "Estimated wait", counter.estimatedWaitTicks(), "ticks");
      addMetric(csv, "Counter " + counter.name(), "Completed", counter.completed(), "customers");
    }
    Path absolute = destination.toAbsolutePath();
    Path parent = absolute.getParent();
    if (parent != null && !Files.isDirectory(parent)) {
      throw new IOException("The selected folder does not exist.");
    }
    Files.writeString(absolute, csv, StandardCharsets.UTF_8);
  }

  private static void addMetric(StringBuilder csv, String section, String name, Object value, String unit) {
    csv.append(escape(section)).append(',')
        .append(escape(name)).append(',')
        .append(escape(String.valueOf(value))).append(',')
        .append(escape(unit)).append("\r\n");
  }

  private static String escape(String value) {
    if (value.contains(",") || value.contains("\"") || value.contains("\r") || value.contains("\n")) {
      return "\"" + value.replace("\"", "\"\"") + "\"";
    }
    return value;
  }
}
