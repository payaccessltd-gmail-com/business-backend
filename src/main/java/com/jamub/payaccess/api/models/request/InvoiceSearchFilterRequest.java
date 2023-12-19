package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
public class InvoiceSearchFilterRequest {

    private String invoiceStatus;
    private String emailAddress;
    private String startDate;
    private String endDate;

    @NotBlank(message = "Incomplete request parameters. Merchant identification not provided")
    private Long merchantId;

}
