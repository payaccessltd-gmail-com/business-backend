package com.jamub.payaccess.api.models.request;

import com.jamub.payaccess.api.enums.MerchantEarningsOption;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestPart;

@Getter @Setter
public class MerchantReceiveEarningsRequest extends BaseRequest{
    private MerchantEarningsOption merchantEarningsOption;
    private Long merchantId;
}
