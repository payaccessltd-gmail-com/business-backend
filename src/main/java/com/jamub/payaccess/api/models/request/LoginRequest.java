package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequest extends BaseRequest{
    private String username;
    private String password;
}
