package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.constraints.ValidPassword;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class CustomerSignUpRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. Customers mobile number not provided")
    private String mobileNumber;

    @NotBlank(message = "Incomplete request parameters. Customers email address not provided")
    private String emailAddress;

    @NotBlank(message = "Incomplete request parameters. Password not provided")
    @ValidPassword
    private String password;
}
