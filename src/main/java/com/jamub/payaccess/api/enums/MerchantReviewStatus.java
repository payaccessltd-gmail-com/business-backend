package com.jamub.payaccess.api.enums;

public enum MerchantReviewStatus {
    APPROVED("APPROVED"),  REJECTED("REJECTED"), REQUEST_UPDATE("REQUEST_UPDATE");



    public final String value;

    private MerchantReviewStatus(String value) {
        this.value = value;
    }

    public static MerchantReviewStatus valueOfLabel(String label) {
        for (MerchantReviewStatus e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }



}
