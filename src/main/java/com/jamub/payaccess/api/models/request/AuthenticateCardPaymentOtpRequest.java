package com.jamub.payaccess.api.models.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticateCardPaymentOtpRequest {
    private String orderRef;
    private String otp;
    private String merchantCode;

}
