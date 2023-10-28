package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MerchantBusinessBankAccountDataUpdateRequest {
    private String emailAddress;
    private String businessBvn;
    private String businessBankName;
    private String businessAccountNumber;
    private String businessAccountName;
    private Long merchantId;
}
