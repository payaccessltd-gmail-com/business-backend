package com.jamub.payaccess.api.enums;

public enum TerminalType {
    POS("POS"), ATM("ATM"), MOBILE("MOBILE"), WEB("WEB");


    public final String value;

    private TerminalType(String value) {
        this.value = value;
    }

    public static TerminalType valueOfLabel(String label) {
        for (TerminalType e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
