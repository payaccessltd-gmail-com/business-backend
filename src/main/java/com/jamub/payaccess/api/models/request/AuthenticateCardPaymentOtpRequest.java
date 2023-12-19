package com.jamub.payaccess.api.models.request;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class AuthenticateCardPaymentOtpRequest {

    @NotBlank(message = "Incomplete request parameters. Order Ref not provided")
    private String orderRef;
    @NotBlank(message = "Incomplete request parameters. OTP not provided")
    private String otp;
    @NotBlank(message = "Incomplete request parameters. Merchant code not provided")
    private String merchantCode;
    @NotBlank(message = "Incomplete request parameters. Terminal code not provided")
    private String terminalCode;

}
