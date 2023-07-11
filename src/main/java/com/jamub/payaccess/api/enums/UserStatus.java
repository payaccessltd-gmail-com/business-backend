package com.jamub.payaccess.api.enums;

public enum UserStatus {
    NOT_ACTIVATED("NOT ACTIVATED"), ACTIVE("ACTIVE"), SUSPENDED("SUSPENDED"), DELETED("DELETED");



    public final String value;

    private UserStatus(String value) {
        this.value = value;
    }

    public UserStatus valueOfLabel(String label) {
        for (UserStatus e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }



}
