package com.jamub.payaccess.api.models.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PendingRequestResponse extends PayAccessResponse {

    public PendingRequestResponse(String statusCode, String message) {
        super(statusCode, message);
    }
}
