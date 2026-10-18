package com.queuevanta.model;

/** Campus services a counter can provide and a customer can request. */
public enum ServiceType {
    CANTEEN("Canteen"),
    STUDENT_OFFICE("Student Office"),
    LIBRARY("Library"),
    IT_HELP("IT Help Desk");
    private final String label;

    ServiceType(String label) {
        this.label = label;
    }

    /** Returns the readable label used in the dashboard and reports. */
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
