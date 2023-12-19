package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class CreateBankRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. Bank name not provided")
    private String bankName;
    @NotBlank(message = "Incomplete request parameters. Bank other name not provided")
    private String bankOtherName;
    @NotBlank(message = "Incomplete request parameters. Bank code not provided")
    private String bankCode;
}
