package com.jamub.payaccess.api.models.response;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ConfirmTransactionStatusErrorResponse {

    private String code;
    private String description;
    private String logId;
    private String errors;
}
