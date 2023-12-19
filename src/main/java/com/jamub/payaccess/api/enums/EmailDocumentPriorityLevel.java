package com.jamub.payaccess.api.enums;

public enum EmailDocumentPriorityLevel {
    PRIORITY("PRIORITY"), NORMAL("NORMAL"), LOW("LOW");


    public final String value;

    private EmailDocumentPriorityLevel(String value) {
        this.value = value;
    }

    public static EmailDocumentPriorityLevel valueOfLabel(String label) {
        for (EmailDocumentPriorityLevel e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
