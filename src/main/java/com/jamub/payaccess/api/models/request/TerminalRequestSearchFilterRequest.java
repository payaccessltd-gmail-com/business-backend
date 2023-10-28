package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TerminalRequestSearchFilterRequest {

    private String terminalRequestStatus;
    private String terminalBrand;
    private String terminalType;
    private LocalDate startDate;
    private LocalDate endDate;

}
