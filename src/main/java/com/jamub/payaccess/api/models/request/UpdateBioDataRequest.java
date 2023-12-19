package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class UpdateBioDataRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. First name not provided")
    private String firstName;

    @NotBlank(message = "Incomplete request parameters. Last name not provided")
    private String lastName;

    @NotBlank(message = "Incomplete request parameters. Email address not provided")
    private String emailAddress;

    @NotBlank(message = "Incomplete request parameters. Gender not provided")
    private String gender;

    @NotBlank(message = "Incomplete request parameters. Phone number not provided")
    private String phoneNumer;
}
