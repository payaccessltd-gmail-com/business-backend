package com.jamub.payaccess.api.enums;

public enum TerminalBrand {
    TELPO("TELPO"), INDECO("INDECO");


    public final String value;

    private TerminalBrand(String value) {
        this.value = value;
    }

    public static TerminalBrand valueOfLabel(String label) {
        for (TerminalBrand e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
