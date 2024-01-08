package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class SettlementBreakdownFilterRequest extends BaseRequest{

    private String settlementStatus;
    private String startDate;
    private String endDate;
    private String merchantCode;
    private Long settlementId;
}
