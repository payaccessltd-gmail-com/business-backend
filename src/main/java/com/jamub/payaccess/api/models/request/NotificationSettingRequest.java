package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.MerchantEarningsOption;
import com.jamub.payaccess.api.models.InvoiceBreakdown;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class NotificationSettingRequest extends BaseRequest{

    @NotNull(message = "Incomplete request parameters. Email notification on transactions key not provided")
    private Boolean transactionNotificationByEmail;

    @NotNull(message = "Incomplete request parameters. Email notification  of customers key not provided")
    private Boolean customerNotificationByEmail;

    @NotNull(message = "Incomplete request parameters. Email notification on transfer credit key not provided")
    private Boolean transferNotificationByEmailForCredit;

    @NotNull(message = "Incomplete request parameters. Email Notification on transfer debit key not provided")
    private Boolean transferNotificationByEmailForDebit;

    @NotBlank(message = "Incomplete request parameters. The key indicating if the merchant receives earnings has not provided")
    @Pattern(regexp = "BANK_ACCOUNT|PAYACCESS_WALLET|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: BANK_ACCOUNT, PAYACCESS_WALLET")
    private String merchantReceiveEarningsOption;

    @NotNull(message = "Incomplete request parameters. Notification for transfer key not provided")
    private Boolean enableNotificationForTransfer;

    @NotNull(message = "Incomplete request parameters. Notification for invoicing key not provided")
    private Boolean enableNotificationForInvoicing;

    @NotNull(message = "Incomplete request parameters. Notification for payment link key not provided")
    private Boolean enableNotificationForPaymentLink;

    @NotNull(message = "Incomplete request parameters. Notification for settlement key not provided")
    private Boolean enableNotificationForSettlement;

    @NotNull(message = "Incomplete request parameters. Merchant identification not provided")
    private Long merchantId;
}
