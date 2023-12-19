package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
public class MerchantBusinessBankAccountDataUpdateRequest {
//    @NotBlank(message = "Incomplete request parameters. Email address not provided")
//    private String emailAddress;
    @NotBlank(message = "Incomplete request parameters. BVN of the business account not provided")
    private String businessBvn;
    @NotBlank(message = "Incomplete request parameters. Please specify the bank the business uses")
    private String businessBankName;
    @NotBlank(message = "Incomplete request parameters. Please provide the bank account number of the business")
    private String businessAccountNumber;
    @NotBlank(message = "Incomplete request parameters. Please provide the bank account name of the business")
    private String businessAccountName;
    @NotNull(message = "Incomplete request parameters. Merchant identification not provided")
    private Long merchantId;
}
