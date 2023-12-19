package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class ForgotPasswordRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. Email address not provided")
    private String emailAddress;
}
