package com.jamub.payaccess.api.models.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class PayAccessResponse implements Serializable {

    private String statusCode;
    private String message;
    private Object responseObject;

    public PayAccessResponse(String statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
