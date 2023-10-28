package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ForgotPasswordRequest extends BaseRequest{
    private String emailAddress;
    private String forgotPasswordEndpoint;
}
