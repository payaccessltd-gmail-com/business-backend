package com.jamub.payaccess.api.models.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayAccessResponse {

    private String statusCode;
    private String message;
    private Object responseObject;
}
