package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CustomerSignUpRequest extends BaseRequest{
    private String mobileNumber;
    private String emailAddress;
    private String password;
}
