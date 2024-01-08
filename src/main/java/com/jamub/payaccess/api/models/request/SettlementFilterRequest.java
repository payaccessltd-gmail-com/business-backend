package com.jamub.payaccess.api.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class SettlementFilterRequest extends BaseRequest{

    private String settlementStartDate;
    private String settlementEndDate;
    private String settlementStatus;
    private String payAccessCurrency;
}
