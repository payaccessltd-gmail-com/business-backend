package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.models.Settlement;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class CreateSettlementBreakdownRequest extends BaseRequest{

    private Settlement settlement;
    private BigDecimal settlementAmount;
    private Long merchantId;
    private String merchantCode;
    private String merchantName;
}
