package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.MerchantEarningsOption;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestPart;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
public class MerchantReceiveEarningsRequest extends BaseRequest{

    @NotNull(message = "Incomplete request parameters. Merchant earning option field not provided")
    private String merchantEarningsOption;

    @NotNull(message = "Incomplete request parameters. Merchant Identification not provided")
    private Long merchantId;
}
