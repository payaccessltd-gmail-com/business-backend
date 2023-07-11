package com.jamub.payaccess.api.enums;

public enum MerchantStatus {
    IN_PROGRESS("IN PROGRESS"), ACTIVE("ACTIVE"), VERIFIED("VERIFIED"), SUSPENDED("SUSPENDED"), CLOSED("CLOSED"), DELETED("DELETED");



    public final String value;

    private MerchantStatus(String value) {
        this.value = value;
    }

    public MerchantStatus valueOfLabel(String label) {
        for (MerchantStatus e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }



}
