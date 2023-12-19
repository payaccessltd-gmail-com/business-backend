package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.DiscountType;
import com.jamub.payaccess.api.enums.InvoiceStatus;
import com.jamub.payaccess.api.models.InvoiceBreakdown;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestPart;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class StandardInvoiceRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. Customer name key not provided")
    private String customerName;

    @NotBlank(message = "Incomplete request parameters. Customer email key not provided")
    private String customerEmail;

    private String additionalCustomerEmailAddress;

    @NotBlank(message = "Incomplete request parameters. Due date key not provided")
    private String dueDate;

    @NotNull(message = "Incomplete request parameters. Amount key not provided")
    @DecimalMin(value = "0.10", message = "Please Enter a valid Amount. Minimum amount acceptable is 0.10")
    private BigDecimal amount;


    private String invoiceNote;

    @NotNull(message = "Incomplete request parameters. Invoice breakdown list of the items key is missing")
    private List<InvoiceBreakdown> invoiceBreakdownList;

    @NotNull(message = "Incomplete request parameters. Tax percentage key not provided")
    private BigDecimal taxPercent;

    @NotBlank(message = "Incomplete request parameters. Discount type key not provided")
    @Pattern(regexp = "PERCENTAGE|VALUE|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: PERCENTAGE, VALUE")
    private String discountType;

    @NotNull(message = "Incomplete request parameters. Discount amount key not provided")
    @DecimalMin(value = "0.00", message = "Please Enter a valid Discount Amount")
    private BigDecimal discountAmount;

    @NotNull(message = "Incomplete request parameters. Shipping fee key not provided")
    @DecimalMin(value = "0.00", message = "Please Enter a valid Shipping Fee")
    private BigDecimal shippingFee;

    @NotNull(message = "Incomplete request parameters. Merchant identification not provided")
    private Long merchantId;

    @NotNull(message = "Incomplete request parameters. Invoice status key not provided")
    @Pattern(regexp = "DRAFT|PENDING|PAID|DELETED|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: DRAFT, PENDING, PAID, DELETED")
    private String invoiceStatus;
}
