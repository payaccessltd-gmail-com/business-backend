package com.jamub.payaccess.api.services;

import com.jamub.payaccess.api.dao.SettlementBreakdownDao;
import com.jamub.payaccess.api.dao.SettlementDao;
import com.jamub.payaccess.api.dao.TerminalDao;
import com.jamub.payaccess.api.dao.TransactionDao;
import com.jamub.payaccess.api.dto.SettlementTransactionDTO;
import com.jamub.payaccess.api.enums.PayAccessCurrency;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.enums.SettlementStatus;
import com.jamub.payaccess.api.enums.TransactionStatus;
import com.jamub.payaccess.api.models.*;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SettlementService {

    @Autowired
    private SettlementDao settlementDao;
    @Autowired
    private SettlementBreakdownDao settlementBreakdownDao;

    @Autowired
    private TransactionDao transactionDao;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    public SettlementService(SettlementDao settlementDao){

        this.settlementDao = settlementDao;
    }


    public Settlement getSettlement(Long settlementId) {

        Optional<Settlement> optionalSettlement = settlementDao.get(settlementId);
        Settlement settlement = null;
        if(optionalSettlement.isPresent())
            settlement = optionalSettlement.get();

        return settlement;

    }

    public Settlement updateSettlement(Settlement settlement)
    {
        return settlementDao.update(settlement);
    }

    public ResponseEntity getSettlementList(SettlementFilterRequest settlementFilterRequest, Integer pageSize, Integer pageNumber) {
        if(pageNumber==null)
            pageNumber = 0;

        Map queryResponse = null;

        if(settlementFilterRequest!=null && settlementFilterRequest.getSettlementStartDate()!=null)
        {
            logger.info("req is not null {}", queryResponse);
            queryResponse = settlementDao.getSettlementByFilter(settlementFilterRequest, pageNumber, pageSize);
        }
        else
        {
            logger.info("req is null {}", queryResponse);
            queryResponse = settlementDao.getAll(pageNumber, pageSize);
        }


        logger.info("{}", queryResponse);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        if(queryResponse!=null)
        {
            payAccessResponse.setResponseObject(queryResponse);
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Settlements fetched successfully");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("Settlement listing fetch failed");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);

    }


    public ResponseEntity getSettlementBreakdownList(SettlementBreakdownFilterRequest settlementBreakdownFilterRequest, Integer rowCount, Integer pageNumber) {
        if(pageNumber==null)
            pageNumber = 0;

        Map queryResponse = null;

        logger.info("req is null {}", queryResponse);
        queryResponse = settlementBreakdownDao.getSettlementBreakdownByFilter(settlementBreakdownFilterRequest, pageNumber, rowCount);


        logger.info("{}", queryResponse);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        if(queryResponse!=null)
        {
            payAccessResponse.setResponseObject(queryResponse);
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Settlement breakdown fetched successfully");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("Settlement listing fetch failed");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);

    }

    public ResponseEntity runSettlement(RunSettlementRequest runSettlementRequest, User authenticatedUser, String ipAddress) {
        TransactionFilterRequest transactionFilterRequest = new TransactionFilterRequest();
        transactionFilterRequest.setEndDate(runSettlementRequest.getSettlementDate());
        transactionFilterRequest.setStartDate(runSettlementRequest.getSettlementDate());
        List<SettlementTransactionDTO> transactionListResp = transactionDao.getAllTransactionsForSettlement(transactionFilterRequest, TransactionStatus.SUCCESS);

        logger.info("transactionListResp....{}", transactionListResp.size());

        BigDecimal totalAmount = transactionListResp.stream().map(t -> {
            logger.info("1 -> {}", t.getSettlementAmount());
            logger.info("2 -> {}", t.getMerchantId());
            logger.info("3 -> {}", t.getMerchantCode());
            logger.info("3 -> {}", t.getBusinessName());
            return t.getSettlementAmount();
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
        logger.info("totalAmount....{}", totalAmount);


        CreateSettlementRequest createSettlementRequest = new CreateSettlementRequest();
        createSettlementRequest.setCreatedByUserId(authenticatedUser.getId());
        createSettlementRequest.setSettlementAmount(totalAmount);
        createSettlementRequest.setSettlementDate((LocalDate.parse(runSettlementRequest.getSettlementDate())));
        createSettlementRequest.setPayAccessCurrency(PayAccessCurrency.valueOf(runSettlementRequest.getPayAccessCurrency()));


        final Settlement settlement = settlementDao.saveSettlement(createSettlementRequest, authenticatedUser, ipAddress);
        List<SettlementBreakdown> breakdownList = transactionListResp.stream().map(t -> {
            CreateSettlementBreakdownRequest createSettlementBreakdownRequest = new CreateSettlementBreakdownRequest();
            createSettlementBreakdownRequest.setSettlement(settlement);
            createSettlementBreakdownRequest.setSettlementAmount(t.getSettlementAmount());
            createSettlementBreakdownRequest.setMerchantId(t.getMerchantId());
            createSettlementBreakdownRequest.setMerchantCode(t.getMerchantCode());
            createSettlementBreakdownRequest.setMerchantName(t.getBusinessName());

            return settlementBreakdownDao.saveSettlementBreakdown(createSettlementBreakdownRequest, authenticatedUser, ipAddress);
        }).collect(Collectors.toList());


        Settlement settlementUpdated = settlementDao.update(settlement);

        if(settlementUpdated==null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
            payAccessResponse.setMessage("Settlement was not run successful");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }

        Map responseMap = new HashMap();
        responseMap.put("settlement", settlementUpdated);
        responseMap.put("settlementBreakdown", breakdownList);


        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setResponseObject(responseMap);
        payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
        payAccessResponse.setMessage("Settlement ran successfully");
        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
    }
}
