package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class CreateSimpleInvoiceRequest extends BaseRequest{

    private String customerName;
    private String customerEmail;
    private String additionalCustomerEmailAddress;
    private String dueDate;
    private BigDecimal amount;
    private BigDecimal taxAmount;
    private BigDecimal shippingFee;
    private String invoiceNote;
    private String invoiceType;
    private MultipartFile businessLogo;
    private Long merchantId;
}
