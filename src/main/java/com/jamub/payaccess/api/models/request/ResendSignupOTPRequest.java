package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.constraints.ValidPassword;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class ResendSignupOTPRequest extends BaseRequest{


    @NotBlank(message = "Incomplete request parameters. Email address not provided")
    private String emailAddress;


}
