package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class LoginRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. Username not provided")
    private String username;
    @NotBlank(message = "Incomplete request parameters. Password not provided")
    private String password;
}
