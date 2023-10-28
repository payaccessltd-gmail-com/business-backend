package com.jamub.payaccess.api.enums;

public enum InvoiceType {
    SIMPLE("SIMPLE"), STANDARD("STANDARD");



    public final String value;

    private InvoiceType(String value) {
        this.value = value;
    }

    public InvoiceType valueOfLabel(String label) {
        for (InvoiceType e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }



}
