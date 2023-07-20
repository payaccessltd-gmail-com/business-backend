package com.jamub.payaccess.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamub.payaccess.api.dao.MerchantDao;
import com.jamub.payaccess.api.dao.TransactionDao;
import com.jamub.payaccess.api.dao.UserDao;
import com.jamub.payaccess.api.dto.MerchantDTO;
import com.jamub.payaccess.api.dto.UserDTO;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.Merchant;
import com.jamub.payaccess.api.models.Transaction;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.MerchantBusinessBankAccountDataUpdateRequest;
import com.jamub.payaccess.api.models.request.MerchantBusinessDataUpdateRequest;
import com.jamub.payaccess.api.models.request.MerchantSignUpRequest;
import com.jamub.payaccess.api.models.request.MerchantUserBioDataUpdateRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    private TransactionDao transactionDao;
    private UserDao userDao;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public TransactionService(TransactionDao transactionDao, UserDao userDao){

        this.transactionDao = transactionDao;
        this.userDao = userDao;
    }

    public PayAccessResponse getTransactions(){
        List<Transaction> allTransactions = transactionDao.getAll();
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setMessage("Transactions");
        payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);

        return payAccessResponse;
    }

}
