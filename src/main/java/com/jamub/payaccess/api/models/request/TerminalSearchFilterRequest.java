package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
public class TerminalSearchFilterRequest {

    @NotBlank(message = "Incomplete request parameters. Terminal status not provided")
    private String terminalStatus;

    @NotBlank(message = "Incomplete request parameters. Merchant identification not provided")
    private String merchantCode;

    @NotBlank(message = "Incomplete request parameters. Start date key not provided")
    private String startDate;

    @NotBlank(message = "Incomplete request parameters. End date key not provided")
    private String endDate;

    @NotBlank(message = "Incomplete request parameters. Terminal brand key not provided")
    private String terminalBrand;

    @NotBlank(message = "Incomplete request parameters. Terminal type key not provided")
    private String terminalType;

    @NotBlank(message = "Incomplete request parameters. Page number not provided")
    private Integer pageNumber;

    @NotBlank(message = "Incomplete request parameters. Page size not provided")
    private Integer pageSize;

}
