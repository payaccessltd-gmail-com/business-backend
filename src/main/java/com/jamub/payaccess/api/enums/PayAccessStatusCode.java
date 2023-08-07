package com.jamub.payaccess.api.enums;

public enum PayAccessStatusCode {
    GENERAL_ERROR("1"), SUCCESS("0"), AUTHORIZATION_FAILED("403"), VALIDATION_FAILED("701");

    public final String label;

    private PayAccessStatusCode(String label) {
        this.label = label;
    }

    public static PayAccessStatusCode valueOfLabel(String label) {
        for (PayAccessStatusCode e : values()) {
            if (e.label.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
