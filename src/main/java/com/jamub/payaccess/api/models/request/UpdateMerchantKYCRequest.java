package com.jamub.payaccess.api.models.request;

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
public class UpdateMerchantKYCRequest {

    @NotNull(message = "Incomplete request parameters. Merchant identification not provided")
    private Long merchantId;

    @Valid
    @NotNull(message = "Incomplete request parameters. Government recognized identification document for your registered business must be provided")
    private MultipartFile governmentApprovedDocument;

    @Valid
    @NotNull(message = "Incomplete request parameters. Directors proof of identity document must be provided")
    private MultipartFile directorsProofOfIdentity;

    @Valid
    @NotNull(message = "Incomplete request parameters. Business owners document must be provided")
    private MultipartFile businessOwnersDocument;

    @Valid
    @NotNull(message = "Incomplete request parameters. Shareholders document must be provided")
    private MultipartFile shareholdersDocument;
}
