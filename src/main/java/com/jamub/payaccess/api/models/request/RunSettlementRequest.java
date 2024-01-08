package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RunSettlementRequest extends BaseRequest{

    private String settlementDate;
    private String settlementStatus;
    private String payAccessCurrency;
}
