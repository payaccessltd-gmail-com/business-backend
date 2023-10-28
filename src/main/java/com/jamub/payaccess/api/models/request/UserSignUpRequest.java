package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserSignUpRequest extends BaseRequest{
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String businessName;
    private String password;
}
