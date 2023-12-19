package com.jamub.payaccess.api.models;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorMessage {
    private String fieldName;
    private String fieldErrorMessage;

    public ErrorMessage(String fieldName, String fieldErrorMessage)
    {
        this.fieldName = fieldName;
        this.fieldErrorMessage = fieldErrorMessage;
    }
}
