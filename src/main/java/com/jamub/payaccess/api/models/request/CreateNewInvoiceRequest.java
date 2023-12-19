package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.InvoiceStatus;
import com.jamub.payaccess.api.enums.InvoiceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
public class CreateNewInvoiceRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. Customers name not provided")
    private String customerName;

    @NotBlank(message = "Incomplete request parameters. Customers email address not provided")
    private String customerEmail;


    private String additionalCustomerEmailAddress;

    @NotBlank(message = "Incomplete request parameters. Invoice due date not provided")
    private LocalDate dueDate;

    @NotBlank(message = "Incomplete request parameters. Invoice amount not provided")
    private BigDecimal amount;

    @NotBlank(message = "Incomplete request parameters. Tax not provided")
    private BigDecimal taxAmount;

    @NotBlank(message = "Incomplete request parameters. Shipping Fee not provided")
    private BigDecimal shippingFee;


    private String invoiceNote;

    @NotBlank(message = "Incomplete request parameters. Specify the invoice type")
    private String invoiceType;

    private String businessLogo;

    @NotBlank(message = "Incomplete request parameters. Merchant Id not provided")
    private Long merchantId;

    @NotBlank(message = "Incomplete request parameters. Invoice status not provided")
    private String invoiceStatus;
}
