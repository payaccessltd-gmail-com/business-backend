package com.jamub.payaccess.api.models.request;


import com.jamub.payaccess.api.enums.TransactionStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TransactionFilterRequest {
    private TransactionStatus transactionStatus;
    private String merchantCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private String orderRef;
    private String switchTransactionRef;
    private String terminalCode;
}
