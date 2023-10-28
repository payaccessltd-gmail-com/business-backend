package com.jamub.payaccess.api.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.models.Merchant;
import com.jamub.payaccess.api.models.Terminal;
import com.jamub.payaccess.api.models.Transaction;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.ISWAuthTokenResponse;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.*;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ISWService iswService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private TerminalService terminalService;
    @Autowired
    private TokenService tokenService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value="/get-transactions", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse getTransactions(@RequestBody TransactionFilterRequest transactionFilterRequest,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws JsonProcessingException {
        User authenticatedUser = tokenService.getUserFromToken(request);
        PayAccessResponse payAccessResponse = transactionService.getTransactions(transactionFilterRequest);

        return payAccessResponse;
    }


    @RequestMapping(value="/debit-card", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse debitCard(@RequestBody InitiateTransactionRequest initiateTransactionRequest,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response)
    {
        String deviceAuthorizationToken = null;
        String authorizationToken = transactionService.getAuthorization(request, "Authorization");
//        String deviceAuthorizationToken = transactionService.getAuthorization(request, "X-Device-Auth");
        String merchantCode = initiateTransactionRequest.getMerchantCode();

        List<?> merchantDetails = merchantService.getMerchantDetails(merchantCode);
        Merchant merchant = (Merchant) merchantDetails.get(0);

        Terminal terminal = null;
        if(!initiateTransactionRequest.getTerminalCode().isEmpty())
            terminal = terminalService.getTerminalByTerminalCode(initiateTransactionRequest.getTerminalCode());

        try {
            ISWAuthTokenResponse iswAuthTokenResponse = iswService.getToken();
            logger.info("iswAuthTokenResponse .... {}", iswAuthTokenResponse);



            PayAccessResponse payAccessResponse = transactionService.debitCard(iswService, merchant, terminal,
                    initiateTransactionRequest, authorizationToken, deviceAuthorizationToken, iswAuthTokenResponse);
            return payAccessResponse;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


        PayAccessResponse payAccessResponse = null;
        return payAccessResponse;
    }


    @RequestMapping(value="/authenticate-card-payment-otp", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse authenticateCardPaymentOtp(@RequestBody AuthenticateCardPaymentOtpRequest authenticateCardPaymentOtpRequest,
                                       HttpServletRequest request,
                                       HttpServletResponse response)
    {
        String deviceAuthorizationToken = null;
        String authorizationToken = transactionService.getAuthorization(request, "Authorization");
//        String deviceAuthorizationToken = transactionService.getAuthorization(request, "X-Device-Auth");
        String merchantCode = authenticateCardPaymentOtpRequest.getMerchantCode();

        List<?> merchantDetails = merchantService.getMerchantDetails(merchantCode);
        Merchant merchant = (Merchant) merchantDetails.get(0);

        Transaction transaction = transactionService.getTransactionByOrderRef(authenticateCardPaymentOtpRequest.getOrderRef(), merchantCode);

        try {
            ISWAuthTokenResponse iswAuthTokenResponse = iswService.getToken();
            logger.info("iswAuthTokenResponse .... {}", iswAuthTokenResponse);
            PayAccessResponse payAccessResponse = transactionService.authenticateCardPaymentOtp(iswService, merchant, authenticateCardPaymentOtpRequest,
                    authorizationToken, deviceAuthorizationToken, iswAuthTokenResponse, transaction);
            return payAccessResponse;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


        PayAccessResponse payAccessResponse = null;
        return payAccessResponse;
    }
}
