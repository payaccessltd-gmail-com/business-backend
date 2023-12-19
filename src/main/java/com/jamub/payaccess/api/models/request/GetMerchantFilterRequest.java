package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.MerchantStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMerchantFilterRequest {

    private MerchantStatus merchantStatus;
    private String startDate;
    private String endDate;
}
