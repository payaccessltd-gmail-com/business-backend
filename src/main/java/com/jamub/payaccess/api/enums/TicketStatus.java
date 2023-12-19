package com.jamub.payaccess.api.enums;

public enum TicketStatus {
    OPEN("OPEN"), CLOSED("CLOSED"), ASSIGNED("ASSIGNED");



    public final String value;

    private TicketStatus(String value) {
        this.value = value;
    }

    public TicketStatus valueOfLabel(String label) {
        for (TicketStatus e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }



}
