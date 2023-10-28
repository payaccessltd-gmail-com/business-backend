package com.jamub.payaccess.api.models.response;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ISWAuthTokenResponse implements Serializable {
    private String access_token;
    private String token_type;
    private String expires_in;
    private String scope;
    private String merchant_code;
    private String client_name;
    private String payable_id;
    private String jti;
}
