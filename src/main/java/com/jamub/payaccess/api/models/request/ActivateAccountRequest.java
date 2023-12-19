package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ActivateAccountRequest {

    @NotBlank(message = "Please provide your profile email address")
    private String emailAddress;
    @NotBlank(message = "Incomplete request. Please provide the One-Time password")
    private String otp;
    @NotBlank(message = "Request is invalid. Verification code not provided in the request")
    private String verificationLink;
}
