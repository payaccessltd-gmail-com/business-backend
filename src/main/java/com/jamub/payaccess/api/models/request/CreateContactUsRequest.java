package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.ProductCategory;
import com.jamub.payaccess.api.enums.Urgency;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class CreateContactUsRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. Email address not provided")
    private String emailAddress;

    @NotBlank(message = "Incomplete request parameters. Subject not provided")
    private String subject;

    @NotBlank(message = "Incomplete request parameters. Product category not provided")
    @Pattern(regexp = "BVN|PAYMENT|SETTLEMENT|AUDIT_LOG|TRANSACTIONS|DISPUTES|INVOICE|REFUNDS|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: BVN, PAYMENT, SETTLEMENT, AUDIT_LOG, TRANSACTIONS, DISPUTES, INVOICE, REFUNDS")
    @Schema(name = "Category of Issues", example = "SETTLEMENT", allowableValues = {"BVN", "PAYMENT", "SETTLEMENT", "AUDIT_LOG", "TRANSACTIONS", "DISPUTES", "INVOICE", "REFUNDS"})
    private String productCategory;

    @NotBlank(message = "Incomplete request parameters. Description not provided")
    private String description;

    @NotBlank(message = "Incomplete request parameters. Urgency not provided")
    @Pattern(regexp = "IMPORTANT|NOT_SO_URGENT|CRITICAL|^\\s$", flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: IMPORTANT, NOT_SO_URGENT, CRITICAL")
    @Schema(name = "Category of Issues", example = "SETTLEMENT", allowableValues = {"IMPORTANT", "NOT_SO_URGENT", "CRITICAL"})
    private String urgency;

    @Valid
    private MultipartFile businessLogoFile;
}
