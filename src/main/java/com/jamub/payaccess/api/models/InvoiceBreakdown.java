package com.jamub.payaccess.api.models;

import com.jamub.payaccess.api.enums.InvoiceStatus;
import com.jamub.payaccess.api.enums.InvoiceType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "invoice_breakdowns")
public class InvoiceBreakdown implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable= false)
    private Long invoiceId;

    @Column(nullable= false)
    private String invoiceItem;

    @Column(nullable= false)
    private Integer quantity;

    @Column(nullable= false)
    private BigDecimal costPerUnit;




}
