package com.jamub.payaccess.api.enums;

public enum MerchantStatus {
    PROCESSING("PROCESSING"), COMPLETED("COMPLETED"), ACTIVE("ACTIVE"), SUSPENDED("SUSPENDED"), CLOSED("CLOSED"), DELETED("DELETED");



    public final String value;

    private MerchantStatus(String value) {
        this.value = value;
    }

    public static MerchantStatus valueOfLabel(String label) {
        for (MerchantStatus e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }



}
