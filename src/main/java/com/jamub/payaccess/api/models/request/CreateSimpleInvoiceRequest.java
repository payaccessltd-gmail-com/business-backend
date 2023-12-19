package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.InvoiceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class CreateSimpleInvoiceRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. Customer name not provided")
    private String customerName;

    @NotBlank(message = "Incomplete request parameters. Customer email not provided")
    private String customerEmail;


    private String additionalCustomerEmailAddress;

    @NotBlank(message = "Incomplete request parameters. Due date not provided")
    private String dueDate;

    @NotNull(message = "Incomplete request parameters. Amount not provided")
    private BigDecimal amount;

//    @NotNull(message = "Incomplete request parameters. Tax amount not provided")
//    private BigDecimal taxAmount;

//    @NotNull(message = "Incomplete request parameters. Shipping Fee not provided")
//    private BigDecimal shippingFee;

    private String invoiceNote;

//    @NotBlank(message = "Incomplete request parameters. Invoice Type not provided")
//    private String invoiceType;

    @Valid
    private MultipartFile businessLogo;

    @NotNull(message = "Incomplete request parameters. Merchant Id not provided")
    private Long merchantId;

    @NotBlank(message = "Incomplete request parameters. Invoice status not provided")
    @Pattern(regexp = "DRAFT|PENDING|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: DRAFT, PENDING")
    @Schema(name = "Status of the Invoice", example = "PENDING", allowableValues = {"DRAFT", "PENDING"})
    private String invoiceStatus;
}
