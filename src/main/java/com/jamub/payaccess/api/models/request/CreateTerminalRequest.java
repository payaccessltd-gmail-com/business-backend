package com.jamub.payaccess.api.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateTerminalRequest extends BaseRequest{

    private String terminalCode;
    private String terminalSerialNo;
    private String terminalBrand;
    private Long merchantId;
    private String terminalType;
    private Long acquirerId;
    private Long terminalRequestId;

}
