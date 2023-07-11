package com.jamub.payaccess.api.enums;

public enum IdentificationDocument {
    INTL_PASSPORT("INTERNATIONAL PASSPORT"), DRIVERS_LICENCE("DRIVERS LICENCE"), NATIONAL_ID("NATIONAL ID CARD");



    public final String value;

    private IdentificationDocument(String value) {
        this.value = value;
    }

    public IdentificationDocument valueOfLabel(String label) {
        for (IdentificationDocument e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
