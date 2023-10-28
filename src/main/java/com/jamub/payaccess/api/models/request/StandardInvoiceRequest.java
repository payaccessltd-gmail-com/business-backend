package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.models.InvoiceBreakdown;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestPart;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class StandardInvoiceRequest extends BaseRequest{
    private String customerName;
    private String customerEmail;
    private String additionalCustomerEmailAddress;
    private String dueDate;
    private Double amount;
    private String invoiceNote;
    private List<InvoiceBreakdown> invoiceBreakdownList;
    private BigDecimal taxPercent;
    private String discountType;
    private BigDecimal discountAmount;
    private Long merchantId;
}
