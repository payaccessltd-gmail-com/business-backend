package com.jamub.payaccess.api.models.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jamub.payaccess.api.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CreateTransactionTicketRequest {

    @NotBlank(message = "Incomplete request parameters. Specify the transaction this ticket is for")
    private String orderRef;

    @NotNull(message = "Incomplete request parameters. Merchant identification not found in the request")
    private Long merchantId;


    @NotBlank(message = "Incomplete request parameters. Provide details about your ticket")
    private String ticketMessage;

    @Valid
    MultipartFile attachmentImage;

    @NotBlank(message = "Incomplete request parameters. Product category not provided")
    @Pattern(regexp = "BVN|PAYMENT|SETTLEMENT|AUDIT_LOG|TRANSACTIONS|DISPUTES|INVOICE|REFUNDS|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: BVN, PAYMENT, SETTLEMENT, AUDIT_LOG, TRANSACTIONS, DISPUTES, INVOICE, REFUNDS")
    @Schema(name = "Category of the issue", example = "SETTLEMENT", allowableValues = {"BVN", "PAYMENT", "SETTLEMENT", "AUDIT_LOG", "TRANSACTIONS", "DISPUTES", "INVOICE", "REFUNDS"})
    private String issueCategory;
}
