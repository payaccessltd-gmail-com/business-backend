package com.jamub.payaccess.api.models.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateAccountRequest extends BaseRequest{
    private String bankCode;
    private String accountNumber;
    private String terminalId;
}