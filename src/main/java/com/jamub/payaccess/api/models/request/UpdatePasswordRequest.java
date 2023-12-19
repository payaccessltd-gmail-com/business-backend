package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class UpdatePasswordRequest {

    @NotBlank(message = "Incomplete request parameters. Current password not provided")
    private String password;

    @NotBlank(message = "Incomplete request parameters. New password not provided")
    private String newPassword;
}
