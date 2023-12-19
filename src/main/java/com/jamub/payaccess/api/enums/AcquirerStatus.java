package com.jamub.payaccess.api.enums;

public enum AcquirerStatus {
    ACTIVE("ACTIVE"), DEACTIVATED("DEACTIVATED");



    public final String value;

    private AcquirerStatus(String value) {
        this.value = value;
    }

    public AcquirerStatus valueOfLabel(String label) {
        for (AcquirerStatus e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }



}
