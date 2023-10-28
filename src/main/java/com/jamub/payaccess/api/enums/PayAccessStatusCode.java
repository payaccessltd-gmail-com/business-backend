package com.jamub.payaccess.api.enums;

public enum PayAccessStatusCode {
    GENERAL_ERROR("1"), SUCCESS("0"), AUTHORIZATION_FAILED("403"), VALIDATION_FAILED("701"), FAIL("2"), EMAIL_EXISTS("702"),
    OTP_EXPIRED("703"), OTP_MISMATCH("704"), FILE_ATTACHMENT_FAIL("705");

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
