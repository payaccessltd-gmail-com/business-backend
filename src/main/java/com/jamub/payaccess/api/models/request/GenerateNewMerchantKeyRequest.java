package com.jamub.payaccess.api.models.request;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class GenerateNewMerchantKeyRequest {

    @NotBlank(message = "Incomplete request parameters. API Mode not provided")
    private String apiMode;

    @NotBlank(message = "Incomplete request parameters. Merchant identification not provided")
    private Long merchantId;
}
