package com.jamub.payaccess.api.enums;

public enum BusinessCategory {
    TRANSPORTATION("TRANSPORTATION"), FARMING("FARMING"), FISHING("FISHING");


    public final String value;

    private BusinessCategory(String value) {
        this.value = value;
    }

    public static BusinessCategory valueOfLabel(String label) {
        for (BusinessCategory e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
