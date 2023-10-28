package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdatePasswordRequest {
    private String password;
    private String newPassword;
}
