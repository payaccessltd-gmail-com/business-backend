package com.jamub.payaccess.api.models.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthOTPResponse {
    private String transactionRef;
    private String message;
    private String token;
    private String tokenExpiryDate;
    private String panLast4Digits;
    private String transactionIdentifier;
    private String amount;
    private String responseCode;
    private String cardType;
}
