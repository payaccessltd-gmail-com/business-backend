package com.jamub.payaccess.api.enums;

public enum EmailDocumentStatus {
    PENDING("PENDING"), SENT("SENT");



    public final String value;

    private EmailDocumentStatus(String value) {
        this.value = value;
    }

    public EmailDocumentStatus valueOfLabel(String label) {
        for (EmailDocumentStatus e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }



}
