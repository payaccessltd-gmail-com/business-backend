package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class UpdateAdminForgotPasswordRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. Email address not provided")
    private String emailAddress;

    @NotBlank(message = "Incomplete request parameters. Forgot password link not provided")
    private String forgotPasswordLink;

    @NotBlank(message = "Incomplete request parameters. Password not provided")
    private String password;
}
