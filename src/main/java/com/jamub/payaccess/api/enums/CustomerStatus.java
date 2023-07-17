package com.jamub.payaccess.api.enums;

public enum CustomerStatus {
    IN_PROGRESS("IN PROGRESS"), ACTIVE("ACTIVE"), VERIFIED("VERIFIED"), SUSPENDED("SUSPENDED"), CLOSED("CLOSED"), DELETED("DELETED");



    public final String value;

    private CustomerStatus(String value) {
        this.value = value;
    }

    public CustomerStatus valueOfLabel(String label) {
        for (CustomerStatus e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }



}
