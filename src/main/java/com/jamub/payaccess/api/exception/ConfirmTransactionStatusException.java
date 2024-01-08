package com.jamub.payaccess.api.exception;

import com.jamub.payaccess.api.models.Transaction;

import java.text.DecimalFormat;

public class ConfirmTransactionStatusException extends Exception{

    public ConfirmTransactionStatusException(String errorMessage)
    {
        super(errorMessage);
    }

}
