package com.jamub.payaccess.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jamub.payaccess.api.enums.APIMode;
import com.jamub.payaccess.api.enums.BusinessCategory;
import com.jamub.payaccess.api.enums.BusinessType;
import com.jamub.payaccess.api.enums.MerchantStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
public class MerchantCredential implements Serializable {

    private String secretKey;
    private String publicKey;
    private String secretKeyLive;
    private String publicKeyLive;




}
