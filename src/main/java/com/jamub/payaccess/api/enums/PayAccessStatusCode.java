package com.jamub.payaccess.api.enums;

public enum PayAccessStatusCode {
    GENERAL_ERROR("1"), SUCCESS("0"), AUTHORIZATION_FAILED("401"), VALIDATION_FAILED("701"), FAIL("2"), EMAIL_EXISTS("702"),
    OTP_EXPIRED("703"), OTP_MISMATCH("704"), FILE_ATTACHMENT_FAIL("705"), INVALID_PARAMETER("706"), LIST_EMPTY("707"),
    MERCHANT_NOT_FOUND("708"), MERCHANT_REVIEW_EXISTS("709"), INVALID_FILE_TYPE("710"), SWITCH_AUTHORIZATION_GRANT_DENIED("711"),
    INCOMPLETE_REQUEST("712"), ACCESS_LEVELS_INSUFFICIENT("713"), ENTITY_INSTANCE_NOT_FOUND("714"), AUTH_TOKEN_EXPIRED("715");

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
