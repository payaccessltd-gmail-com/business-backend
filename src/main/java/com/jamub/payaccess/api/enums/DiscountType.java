package com.jamub.payaccess.api.enums;

public enum DiscountType {
    PERCENTAGE("PERCENTAGE"), VALUE("VALUE");


    public final String value;

    private DiscountType(String value) {
        this.value = value;
    }

    public static DiscountType valueOfLabel(String label) {
        for (DiscountType e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
