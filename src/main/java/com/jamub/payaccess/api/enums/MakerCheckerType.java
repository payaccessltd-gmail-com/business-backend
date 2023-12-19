package com.jamub.payaccess.api.enums;

public enum MakerCheckerType {
    MERCHANT_APPROVAL("MERCHANT_APPROVAL");


    public final String value;

    private MakerCheckerType(String value) {
        this.value = value;
    }

    public static MakerCheckerType valueOfLabel(String label) {
        for (MakerCheckerType e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
