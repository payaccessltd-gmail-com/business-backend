package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.constraints.ValidPassword;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class UserSignUpRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. First name not provided")
    private String firstName;

    @NotBlank(message = "Incomplete request parameters. Last name not provided")
    private String lastName;

    @NotBlank(message = "Incomplete request parameters. Email address not provided")
    private String emailAddress;

    @NotBlank(message = "Incomplete request parameters. Business name not provided")
    private String businessName;

    @NotBlank(message = "Incomplete request parameters. Password not provided")
    @ValidPassword
    private String password;
}
