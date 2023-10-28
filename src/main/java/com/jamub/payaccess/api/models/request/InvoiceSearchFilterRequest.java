package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class InvoiceSearchFilterRequest {

    private String invoiceStatus;
    private String emailAddress;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long merchantId;

}
