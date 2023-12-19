package com.jamub.payaccess.api.services;

import com.jamub.payaccess.api.dao.TerminalDao;
import com.jamub.payaccess.api.dao.TerminalRequestDao;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.Terminal;
import com.jamub.payaccess.api.models.TerminalRequest;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.CreateTerminalRequest;
import com.jamub.payaccess.api.models.request.TerminalOrderRequest;
import com.jamub.payaccess.api.models.request.TerminalRequestSearchFilterRequest;
import com.jamub.payaccess.api.models.request.TerminalSearchFilterRequest;
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
public class TerminalService {

    private TerminalDao terminalDao;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public TerminalService(TerminalDao terminalDao){

        this.terminalDao = terminalDao;
    }


    public Terminal createTerminal(CreateTerminalRequest createTerminalRequest, User authenticatedUser) {
        Terminal terminal = terminalDao.saveTerminal(createTerminalRequest, authenticatedUser);
        return terminal;
    }


    public Terminal getTerminal(Long terminalId, Long merchantId) {

        Optional<Terminal> optionalTerminal = terminalDao.get(terminalId, merchantId);
        Terminal terminal = null;
        if(optionalTerminal.isPresent())
            terminal = optionalTerminal.get();

        return terminal;

    }

    public Terminal updateTerminal(Terminal terminal)
    {
        return terminalDao.update(terminal);
    }

    public ResponseEntity getTerminals(Integer pageNumber, Integer pageSize) {
        if(pageNumber==null)
            pageNumber = 0;

        Map queryResponse = terminalDao.getAll(pageNumber, pageSize);
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

    public List<Terminal> getTerminalsByFilter(TerminalSearchFilterRequest terminalSearchFilterRequest, User authenticatedUser) {
        List<Terminal> queryResponse = terminalDao.getTerminalsByFilter(
                terminalSearchFilterRequest.getTerminalStatus(),
                terminalSearchFilterRequest.getTerminalBrand(),
                terminalSearchFilterRequest.getTerminalType(),
                terminalSearchFilterRequest.getStartDate(),
                terminalSearchFilterRequest.getEndDate(),
                terminalSearchFilterRequest.getMerchantCode(),
                terminalSearchFilterRequest.getPageNumber(),
                terminalSearchFilterRequest.getPageSize()
        );
        return queryResponse;
    }

    public Terminal getTerminalByTerminalCode(String terminalCode) {
        List<Terminal> terminalList = terminalDao.getTerminalByTerminalCode(
                terminalCode
        );

        if(terminalList!=null && !terminalList.isEmpty())
        {
            return terminalList.get(0);
        }
        return null;
    }
}
