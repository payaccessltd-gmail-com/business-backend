package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.MerchantReviewStatus;
import com.jamub.payaccess.api.enums.MerchantStage;
import com.jamub.payaccess.api.enums.MerchantStatus;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter @Setter
public class MerchantStatusUpdateRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. Merchant identification not provided")
    private String merchantCode;

    @NotNull(message = "Incomplete request parameters. Merchant status not provided")
    @Pattern(regexp = "PROCESSING|COMPLETED|APPROVED|DEACTIVATED|REJECTED|SUSPENDED|CLOSED|UNDER_REVIEW|DELETED|REQUEST_UPDATE|FORWARDED_FOR_REVIEW|^\\s$",
            flags = Pattern.Flag.UNICODE_CASE, message = "Must be one of the following: PROCESSING, COMPLETED, APPROVED, DEACTIVATED, REJECTED, SUSPENDED, CLOSED, UNDER_REVIEW, DELETED, REQUEST_UPDATE, FORWARDED_FOR_REVIEW")
    private String merchantStatus;

}
