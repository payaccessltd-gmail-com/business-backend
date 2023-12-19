package com.jamub.payaccess.api.enums;

public enum MerchantStatus {
    PROCESSING("PROCESSING"), COMPLETED("COMPLETED"), APPROVED("APPROVED"), DEACTIVATED("DEACTIVATED"),
    REJECTED("REJECTED"), SUSPENDED("SUSPENDED"), CLOSED("CLOSED"), UNDER_REVIEW("UNDER_REVIEW"),
    DELETED("DELETED"), REQUEST_UPDATE("REQUEST_UPDATE"), FORWARDED_FOR_REVIEW("FORWARDED_FOR_REVIEW");



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
