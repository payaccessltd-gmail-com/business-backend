package com.jamub.payaccess.api.models.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PayAccessResponse implements Serializable {

    private String statusCode;
    private String message;
    private Object responseObject;
}
