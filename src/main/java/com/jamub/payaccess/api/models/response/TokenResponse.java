package com.jamub.payaccess.api.models.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponse {
    private String message;
    private String responseCode;
}
