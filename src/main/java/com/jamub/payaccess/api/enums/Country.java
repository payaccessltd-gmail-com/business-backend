package com.jamub.payaccess.api.enums;

public enum Country {
    AFGHANISTAN("AFGHANISTAN"), BELARUS("BELARUS"), CHINA("CHINA"), DR_CONGO("DR CONGO"), SPAIN("SPAIN"), NIGERIA("NIGERIA");



    public final String value;

    private Country(String value) {
        this.value = value;
    }

    public static Country valueOfLabel(String label) {
        for (Country e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
