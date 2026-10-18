package com.queuecraft.model;

public enum ServiceType {
  CANTEEN("Canteen"),
  STUDENT_OFFICE("Student Office"),
  LIBRARY("Library"),
  IT_HELP("IT Help Desk");
  private final String label;

  ServiceType(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public String toString() {
    return label;
  }
}
