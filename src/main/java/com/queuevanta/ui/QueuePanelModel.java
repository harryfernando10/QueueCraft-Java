package com.queuevanta.ui;

import com.queuevanta.model.CounterSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Builds readable, ordered rows for the queue list from detached engine snapshots. */
public final class QueuePanelModel {
    private QueuePanelModel() {}

    /** Converts snapshots into a counter heading, serving row, then FIFO waiting rows. */
    public static List<Row> fromSnapshots(List<CounterSnapshot> snapshots) {
        Objects.requireNonNull(snapshots, "Counter snapshots are required.");
        List<Row> rows = new ArrayList<>();
        for (CounterSnapshot counter : snapshots) {
            rows.add(new Row(
                    Kind.HEADER,
                    counter.name() + "  ·  " + counter.serviceType().getLabel()
                            + "  ·  " + (counter.enabled() ? "Enabled" : "Disabled")));
            if (counter.activeCustomer() == null) {
                rows.add(new Row(Kind.CUSTOMER, "Serving: nobody"));
            } else {
                rows.add(new Row(
                        Kind.CUSTOMER,
                        "Serving: " + counter.activeCustomer() + "  ·  "
                                + counter.remainingTicks() + " ticks left"));
            }
            if (counter.waitingCustomers().isEmpty()) {
                rows.add(new Row(Kind.CUSTOMER, "Waiting line: empty"));
            } else {
                for (int index = 0; index < counter.waitingCustomers().size(); index++) {
                    rows.add(new Row(
                            Kind.CUSTOMER,
                            "Waiting " + (index + 1) + ": " + counter.waitingCustomers().get(index)));
                }
            }
        }
        return List.copyOf(rows);
    }

    /** Creates a placeholder row when no counters exist. */
    public static Row empty(String text) {
        return new Row(Kind.CUSTOMER, Objects.requireNonNull(text, "Text is required."));
    }

    /** Display role used to style counter headings separately from customer rows. */
    public enum Kind {
        HEADER,
        CUSTOMER
    }

    /** A single display row with its semantic role. */
    public record Row(Kind kind, String text) {
        public Row {
            Objects.requireNonNull(kind, "Row kind is required.");
            Objects.requireNonNull(text, "Row text is required.");
        }
    }
}
