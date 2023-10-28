package com.jamub.payaccess.api.models.request;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.PathVariable;

@Getter
@Setter
public class GenerateNewMerchantKeyRequest {
    private String apiMode;
    private Long merchantId;
}
