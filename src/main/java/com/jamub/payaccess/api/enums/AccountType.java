package com.jamub.payaccess.api.enums;

public enum AccountType {
    CUSTOMER("CUSTOMER"), MERCHANT("MERCHANT");


    public final String value;

    private AccountType(String value) {
        this.value = value;
    }

    public static AccountType valueOfLabel(String label) {
        for (AccountType e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
