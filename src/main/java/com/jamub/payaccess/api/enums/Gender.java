package com.jamub.payaccess.api.enums;

public enum Gender {
    FEMALE("FEMALE"), MALE("MALE");



    public final String value;

    private Gender(String value) {
        this.value = value;
    }

    public Gender valueOfLabel(String label) {
        for (Gender e : values()) {
            if (e.value.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
