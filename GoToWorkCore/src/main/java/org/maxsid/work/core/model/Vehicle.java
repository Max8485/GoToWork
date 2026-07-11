package org.maxsid.work.core.model;

public enum Vehicle {
    CAR("car"),
    FOOT("foot");

    private final String value;

    Vehicle(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
