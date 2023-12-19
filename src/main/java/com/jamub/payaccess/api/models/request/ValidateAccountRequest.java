package com.jamub.payaccess.api.models.request;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ValidateAccountRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. Bank code not provided")
    private String bankCode;

    @NotBlank(message = "Incomplete request parameters. Account number not provided")
    private String accountNumber;

    @NotBlank(message = "Incomplete request parameters. Terminal identification not provided")
    private String terminalId;
}