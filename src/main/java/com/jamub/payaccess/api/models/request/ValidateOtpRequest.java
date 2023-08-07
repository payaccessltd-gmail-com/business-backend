package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateOtpRequest extends BaseRequest{
    private String otp;
    private String key;
    private String username;
}
