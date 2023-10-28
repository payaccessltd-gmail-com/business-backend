package com.jamub.payaccess.api.enums;

public enum TerminalRequestStatus {
    PENDING("PENDING"), APPROVED("APPROVED"), DECLINED("DECLINED"), DELETED("DELETED");



    public final String value;

    private TerminalRequestStatus(String value) {
        this.value = value;
    }

    public TerminalRequestStatus valueOfLabel(String label) {
        for (TerminalRequestStatus e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }



}
