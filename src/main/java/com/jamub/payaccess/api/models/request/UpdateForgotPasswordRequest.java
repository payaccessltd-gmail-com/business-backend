package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateForgotPasswordRequest extends BaseRequest{
    private String newPassword;
    private String confirmNewPassword;
    private String emailAddress;
    private String forgotPasswordLink;
    private String otp;
}
