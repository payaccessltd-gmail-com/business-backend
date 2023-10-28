package com.jamub.payaccess.api.enums;

public enum TerminalStatus {
    ACTIVE("ACTIVE"), DEACTIVATED("DEACTIVATED");



    public final String value;

    private TerminalStatus(String value) {
        this.value = value;
    }

    public TerminalStatus valueOfLabel(String label) {
        for (TerminalStatus e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }



}
