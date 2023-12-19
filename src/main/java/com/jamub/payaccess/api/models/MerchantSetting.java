package com.jamub.payaccess.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jamub.payaccess.api.enums.APIMode;
import com.jamub.payaccess.api.enums.BusinessCategory;
import com.jamub.payaccess.api.enums.BusinessType;
import com.jamub.payaccess.api.enums.MerchantStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "merchant_settings")
public class MerchantSetting implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable= true)
    private String merchantTransactionFeePayerOption;

    @Column(nullable= true)
    private String merchantReceiveEarningsOption;

    @Column(nullable= true)
    private Boolean transactionNotificationByEmail;

    @Column(nullable= true)
    private Boolean customerNotificationByEmail;

    @Column(nullable= true)
    private Boolean transferNotificationByEmailForCredit;

    @Column(nullable= true)
    private Boolean transferNotificationByEmailForDebit;

    @Column(nullable= true)
    private Boolean enableNotificationServicesForTransfer;

    @Column(nullable= true)
    private Boolean enableNotificationForInvoicing;

    @Column(nullable= true)
    private Boolean enableNotificationForPaymentLink;

    @Column(nullable= true)
    private Boolean enableNotificationForSettlement;

    @Column(nullable= true)
    private Boolean twoFactorAuthForPaymentAndTransfer;

    @Column(nullable= true)
    private String defaultCurrency;


    @Column(nullable= false)
    private Long merchantId;

    @Column(nullable= true)
    private Boolean enableAcceptPosChannel;

    @Column(nullable= true)
    private Boolean enableAcceptBankTransfers;

    @Column(nullable= true)
    private Boolean enableAcceptCardPayment;

    @Column(nullable= true)
    private Boolean enableAcceptMobileMoneyTransfer;

    @Column(nullable= true)
    private Boolean enableUssdTransfer;



}
