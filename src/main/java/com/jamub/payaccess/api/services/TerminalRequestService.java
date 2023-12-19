package com.jamub.payaccess.api.services;

import com.jamub.payaccess.api.dao.TerminalRequestDao;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.TerminalRequest;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TerminalRequestService {

    private TerminalRequestDao terminalRequestDao;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public TerminalRequestService(TerminalRequestDao terminalRequestDao){

        this.terminalRequestDao = terminalRequestDao;
    }


    public ResponseEntity createTerminalRequest(TerminalOrderRequest terminalOrderRequest, User authenticatedUser) {
        TerminalRequest terminalRequest = terminalRequestDao.saveTerminalRequest(terminalOrderRequest, authenticatedUser);
        if(terminalRequest!=null) {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Terminal request created successfully");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Terminal request could not be created successfully");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
    }


    public TerminalRequest getTerminalRequest(Long terminalRequestId, Long merchantId) {

        Optional<TerminalRequest> optionalTerminalRequest = terminalRequestDao.get(terminalRequestId, merchantId);
        TerminalRequest terminalRequest = null;
        if(optionalTerminalRequest.isPresent())
            terminalRequest = optionalTerminalRequest.get();

        return terminalRequest;

    }


    public TerminalRequest getTerminalRequest(Long terminalRequestId) {

        Optional<TerminalRequest> optionalTerminalRequest = terminalRequestDao.get(terminalRequestId);
        TerminalRequest terminalRequest = null;
        if(optionalTerminalRequest.isPresent())
            terminalRequest = optionalTerminalRequest.get();

        return terminalRequest;

    }

    public TerminalRequest updateTerminalRequest(TerminalRequest terminalRequest)
    {
        return terminalRequestDao.update(terminalRequest);
    }

    public ResponseEntity getTerminalRequests(Integer pageNumber, Integer pageSize) {
        if(pageNumber==null)
            pageNumber = 0;

        Map queryResponse = terminalRequestDao.getAll(pageNumber, pageSize);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setResponseObject(queryResponse);
        if(queryResponse!=null)
        {
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Terminals fetched successfully");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("Terminal listing fetch failed");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);

    }

    public List<TerminalRequest> getTerminalRequestsByFilter(TerminalRequestSearchFilterRequest terminalRequestSearchFilterRequest, User authenticatedUser) {
        List<TerminalRequest> queryResponse = terminalRequestDao.getTerminalRequestsByFilter(
                terminalRequestSearchFilterRequest.getTerminalRequestStatus(),
                terminalRequestSearchFilterRequest.getTerminalBrand(),
                terminalRequestSearchFilterRequest.getTerminalType(),
                terminalRequestSearchFilterRequest.getStartDate(),
                terminalRequestSearchFilterRequest.getEndDate(),
                authenticatedUser.getId()
        );
        return queryResponse;
    }
}
