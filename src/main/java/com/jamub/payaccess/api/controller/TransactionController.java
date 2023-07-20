package com.jamub.payaccess.api.controller;


import com.jamub.payaccess.api.models.request.CustomerSignUpRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.TransactionService;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;

@RestController
@RequestMapping("/api/v1/customer")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value="/get-transactions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse getTransactions()
    {
        PayAccessResponse payAccessResponse = transactionService.getTransactions();

        return payAccessResponse;
    }
}
