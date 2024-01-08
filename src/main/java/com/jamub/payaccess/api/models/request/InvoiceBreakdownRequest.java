package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class InvoiceBreakdownRequest implements Serializable {

    @Column(nullable= false)
    private String invoiceItem;

    @Column(nullable= false)
    private Integer quantity;

    @Column(nullable= false)
    private BigDecimal costPerUnit;




}
