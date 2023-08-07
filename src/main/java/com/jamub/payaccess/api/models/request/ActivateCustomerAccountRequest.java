package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivateCustomerAccountRequest {

    private String emailAddress;
    private String otp;
    private String verificationLink;
}
