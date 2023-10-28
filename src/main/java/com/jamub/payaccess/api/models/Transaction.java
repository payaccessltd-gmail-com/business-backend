package com.jamub.payaccess.api.models;

import com.jamub.payaccess.api.enums.Channel;
import com.jamub.payaccess.api.enums.PayAccessCurrency;
import com.jamub.payaccess.api.enums.ServiceType;
import com.jamub.payaccess.api.enums.TransactionStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="transactions")
public class Transaction implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;
    @Column(nullable = true)
    String transactionRef;
    Long customerId;
    Long recipientCustomerId;

    @Column(nullable = false, unique=true)
    String orderRef;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    Channel channel;
    @Column(nullable = false)
    LocalDateTime createdAt;
    @Column(nullable = false)
    LocalDateTime updatedAt;
    LocalDateTime deletedAt;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    ServiceType serviceType;
    @Column(nullable = true)
    String payerName;
    @Column(nullable = true)
    String payerEmail;
    @Column(nullable = true)
    String payerMobile;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    TransactionStatus transactionStatus;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    PayAccessCurrency payAccessCurrency;
    @Column(nullable = true)
    Long acquirerId;
    @Column(nullable = true)
    Long terminalId;
    @Column(nullable = true)
    String terminalCode;
    String messageRequest;
    String messageResponse;
    @Column(nullable = true, precision=10, scale=2)
    Double fixedCharge;
    @Column(nullable = true, precision=10, scale=2)
    Double transactionCharge;
    Double preDebitSourceBalance;
    Double postDebitSourceBalance;
    Double preCreditSourceBalance;
    Double postCreditSourceBalance;
    Long poolId;

    @Column(nullable = true, precision=10, scale=2)
    Double amount;
    String otp;
    Long merchantId;
    String merchantName;
    String merchantCode;
    String transactionDetail;
    String transactionRemark;
    String transactionSourceDetails;
    String transactionReceipientDetails;
    String customData;
    @Column(nullable = true)
    String summary;
    String sourceWalletNumber;
    Long sourceWalletId;
    String destinationWalletNumber;
    Long destinationWalletId;
    @Column(nullable = true)
    String redirectUrl;
    @Column(nullable = true)
    String switchTransactionRef;



}