package com.jamub.payaccess.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jamub.payaccess.api.enums.*;
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
@Table(name = "invoices")
public class Invoice implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable= false)
    private String customerName;

    @Column(nullable= false)
    private Long createdByUserId;

    @Column(nullable= false)
    private Long createdByMerchantId;


    @Column(nullable= false)
    private String customerEmail;
    @Column(nullable= true)
    private String additionalCustomerEmailAddress;

    @Column(nullable= false)
    private LocalDate dueDate;
    @Column(nullable= true)
    private BigDecimal amount;
    @Column(nullable= true)
    private BigDecimal taxAmount;
    @Column(nullable= true)
    private BigDecimal shippingFee;
    @Column(nullable= true)
    private String invoiceNote;
    @Column(nullable= true)
    private BigDecimal discount;
    @Column(nullable= true)
    private String invoiceNumber;

    @Column(nullable= true)
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @Column(nullable= false)
    @Enumerated(EnumType.STRING)
    private InvoiceType invoiceType;
    @Column(nullable= true)
    private String businessLogo;
    @Column(nullable= false)
    private LocalDateTime createdAt;
    @Column(nullable= true)
    private LocalDateTime deletedAt;
    @Column(nullable= true)
    private LocalDateTime updatedAt;

    @Column(nullable= false)
    @Enumerated(EnumType.STRING)
    private InvoiceStatus invoiceStatus;

    @Column(nullable= true)
    private Long paymentTransactionId;

    private String referenceNumber;



    @Column(nullable= true)
    private String qrFileName;


}
