package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ValidateOtpRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. OTP not provided")
    private String otp;

    @NotBlank(message = "Incomplete request parameters. Validation Key not provided")
    private String key;

    @NotBlank(message = "Incomplete request parameters. Username not provided")
    private String username;
}
