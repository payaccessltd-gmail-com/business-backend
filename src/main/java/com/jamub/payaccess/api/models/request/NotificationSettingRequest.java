package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.MerchantEarningsOption;
import com.jamub.payaccess.api.models.InvoiceBreakdown;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class NotificationSettingRequest extends BaseRequest{
    private Boolean transactionNotificationByEmail;
    private Boolean customerNotificationByEmail;
    private Boolean transferNotificationByEmailForCredit;
    private Boolean transferNotificationByEmailForDebit;
    private MerchantEarningsOption merchantReceiveEarningsOption;
    private Boolean enableNotificationForTransfer;
    private Boolean enableNotificationForInvoicing;
    private Boolean enableNotificationForPaymentLink;
    private Boolean enableNotificationForSettlement;
    private Long merchantId;
}
