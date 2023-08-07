package com.jamub.payaccess.api.models.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ISWAuthTokenResponse {
    private String access_token;
    private String token_type;
    private String expires_in;
    private String scope;
    private String merchant_code;
    private String production_payment_code;
    private String requestor_id;
    private String payable_id;
    private String jti;
}
