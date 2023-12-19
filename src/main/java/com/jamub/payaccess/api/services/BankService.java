package com.jamub.payaccess.api.services;

import com.jamub.payaccess.api.dao.BankDao;
import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.Bank;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class BankService {


    @Autowired
    private BankDao bankDao;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${is.live}")
    private int isLive;




    @Autowired
    public BankService(BankDao bankDao){
        this.bankDao = bankDao;
    }


    public ResponseEntity createNewBank(String bankCode, String bankName, String bankOtherName, Long actorId, String ipAddress, String description,
                                        ApplicationAction userAction, String carriedOutByUserFullName, String objectClassReference,
                                        Long objectIdReference) {

        Bank existingBank = this.bankDao.getBankByBankCode(bankCode);
        if(existingBank!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Bank matching the bank code already exists");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }

        existingBank = this.bankDao.getBankByBankName(bankName);
        if(existingBank!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Bank matching the bank name already exists");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }

        Bank bank = this.bankDao.createNewBank(bankCode, bankName, bankOtherName, actorId, ipAddress, description,
                userAction, carriedOutByUserFullName, objectClassReference,
                objectIdReference);

        if(bank!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setResponseObject(bank);
            payAccessResponse.setMessage("Bank creation was successful");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("Bank creation was not successful");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
    }


    public Bank getBankByBankCode(String bankCode) {
        return this.bankDao.getBankByBankCode(bankCode);
    }


    public Map getBanksByPagination(Integer pageNumber, Integer pageSize) {
        return this.bankDao.getBanksByPagination(pageNumber, pageSize);
    }
}
