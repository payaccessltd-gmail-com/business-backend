package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class TerminalOrderRequest extends BaseRequest{

    private Integer quantity;
    private String terminalBrand;
    private Long merchantId;
    private String terminalType;
}
