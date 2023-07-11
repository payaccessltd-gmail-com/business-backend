package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MerchantBusinessBankAccountDataUpdateRequest {
    private Long merchantId;
    private String businessBvn;
    private String businessBankName;
    private String businessAccountNumber;
    private String businessAccountName;
}
