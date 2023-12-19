package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.InvoiceStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.math.BigDecimal;


@Getter
@Setter
public class GetInvoiceFilterRequest {


//    @Column(nullable= true)
    private String invoiceStatus;
    private String creationStartDate;
    private String creationEndDate;
    private String dueDateStartDate;
    private String dueDateEndDate;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
}
