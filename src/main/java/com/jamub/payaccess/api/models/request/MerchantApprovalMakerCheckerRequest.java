package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
public class MerchantApprovalMakerCheckerRequest extends BaseRequest{

    @NotBlank(message = "Incomplete request parameters. Specify the user email address")
    private String approverEmailAddress;
    @NotNull(message = "Incomplete request parameters. Specify the level of the approver")
    private Integer checkerLevel;
    @NotBlank(message = "Incomplete request parameters. Specify the level of the approver")
    private String makerCheckerType;
}
