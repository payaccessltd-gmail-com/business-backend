package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.InvoiceStatus;
import com.jamub.payaccess.api.enums.InvoiceType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
public class CreateNewInvoiceRequest extends BaseRequest{

    private String customerName;
    private String customerEmail;
    private String additionalCustomerEmailAddress;
    private LocalDate dueDate;
    private BigDecimal amount;
    private BigDecimal taxAmount;
    private BigDecimal shippingFee;
    private String invoiceNote;
    private String invoiceType;
    private String businessLogo;
    private Long merchantId;
}
