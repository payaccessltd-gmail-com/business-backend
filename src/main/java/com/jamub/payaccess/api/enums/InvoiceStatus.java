package com.jamub.payaccess.api.enums;

public enum InvoiceStatus {
    DRAFT("DRAFT"), PENDING("PENDING"), PAID("PAID"), DELETED("DELETED");



    public final String value;

    private InvoiceStatus(String value) {
        this.value = value;
    }

    public InvoiceStatus valueOfLabel(String label) {
        for (InvoiceStatus e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }



}
