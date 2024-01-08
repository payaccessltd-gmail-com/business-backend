package com.jamub.payaccess.api.models.response;


import com.jamub.payaccess.api.models.AuthMerchantData;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AuthenticateResponse {

    private String subject;
    private List<AuthMerchantData> merchantList;
    private String token;
    private String message;




}
