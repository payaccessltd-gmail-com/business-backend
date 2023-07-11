package com.jamub.payaccess.api.enums;

public enum BusinessType {
    INDIVIDUAL("INDIVIDUAL"), REGISTERED_BUSINESS("REGISTERED BUSINESS");


    public final String value;

    private BusinessType(String value) {
        this.value = value;
    }

    public static BusinessType valueOfLabel(String label) {
        for (BusinessType e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
