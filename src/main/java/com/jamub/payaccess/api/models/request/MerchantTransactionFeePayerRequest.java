package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestAttribute;

@Getter
@Setter
public class MerchantTransactionFeePayerRequest {
    Boolean merchantMustPayTransactionFee;
    Long merchantId;
}
