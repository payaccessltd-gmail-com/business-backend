package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
public class TerminalRequestSearchFilterRequest {

    @NotBlank(message = "Incomplete request parameters. Terminal request status key not provided")
    private String terminalRequestStatus;

    @NotBlank(message = "Incomplete request parameters. Terminal brand key not provided")
    private String terminalBrand;

    @NotBlank(message = "Incomplete request parameters. Terminal type key not provided")
    private String terminalType;

    @NotBlank(message = "Incomplete request parameters. Start date key not provided")
    private LocalDate startDate;

    @NotBlank(message = "Incomplete request parameters. End date key not provided")
    private LocalDate endDate;

}
