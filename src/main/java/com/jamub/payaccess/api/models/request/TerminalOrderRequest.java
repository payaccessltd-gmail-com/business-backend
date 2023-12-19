package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class TerminalOrderRequest extends BaseRequest{

    @NotNull(message = "Incomplete request parameters. Quantity key not provided")
    private Integer quantity;

    @NotBlank(message = "Incomplete request parameters. Terminal brand key not provided")
    private String terminalBrand;

    @NotNull(message = "Incomplete request parameters. Merchant identification not provided")
    private Long merchantId;

    @NotBlank(message = "Incomplete request parameters. Terminal type key not provided")
    private String terminalType;
}
