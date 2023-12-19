package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class SetPasswordRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. New password not provided")
    private String newPassword;

    @NotBlank(message = "Incomplete request parameters. Email address key not provided")
    private String emailAddress;

    @NotBlank(message = "Incomplete request parameters. Link to set new password not provided")
    private String forgotPasswordLink;
}
