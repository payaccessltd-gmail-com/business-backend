package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class UpdateForgotPasswordRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. Email address not provided")
    private String emailAddress;

    @NotBlank(message = "Incomplete request parameters. Forgot password link not provided")
    private String forgotPasswordLink;

    @NotBlank(message = "Incomplete request parameters. One-Time password not provided")
    private String otp;
}
