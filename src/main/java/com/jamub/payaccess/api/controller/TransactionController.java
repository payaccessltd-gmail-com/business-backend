package com.jamub.payaccess.api.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.enums.PaymentRequestType;
import com.jamub.payaccess.api.models.*;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.ISWAuthTokenResponse;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.*;
import io.swagger.annotations.*;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Entity;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/transactions")
@Api(produces = "application/json", value = "Operations pertaining to Transaction Management")
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
    @Autowired
    private PaymentRequestService paymentRequestService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @CrossOrigin
    @RequestMapping(value = {"/get-transactions/{rowCount}", "/get-transactions/{rowCount}/{pageNumber}"}, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "List transactions", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getTransactions(
            @PathVariable(required = true) Integer rowCount,
            @PathVariable(required = false) Integer pageNumber,
            @RequestBody @Valid TransactionFilterRequest transactionFilterRequest,
            BindingResult bindingResult,
            HttpServletRequest request,
            HttpServletResponse response) throws JsonProcessingException {

        if (bindingResult.hasErrors()) {
            List errorMessageList =  bindingResult.getFieldErrors().stream().map(fe -> {
                return new ErrorMessage(fe.getField(), fe.getDefaultMessage());
            }).collect(Collectors.toList());

            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setResponseObject(errorMessageList);
            payAccessResponse.setStatusCode(PayAccessStatusCode.VALIDATION_FAILED.label);
            payAccessResponse.setMessage("Request validation failed");
            return ResponseEntity.badRequest().body(payAccessResponse);
        }

        User authenticatedUser = tokenService.getUserFromToken(request);


        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. Token expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }
        return transactionService.getTransactions(transactionFilterRequest, pageNumber, rowCount);
    }



    @CrossOrigin
    @RequestMapping(value = {"/get-transactions-by-merchant-id/{merchantId}/{rowCount}", "/get-transactions-by-merchant-id/{merchantId}/{rowCount}/{pageNumber}"}, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "List merchant transactions", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getTransactionsByMerchantId(
            @PathVariable(required = true) Long merchantId,
            @PathVariable(required = true) Integer rowCount,
            @PathVariable(required = false) Integer pageNumber,
            @RequestBody(required = false) TransactionFilterRequest transactionFilterRequest,
            HttpServletRequest request,
            HttpServletResponse response) throws JsonProcessingException {
        User authenticatedUser = tokenService.getUserFromToken(request);


        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. Token expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }

        Merchant merchant = (Merchant)merchantService.getMerchantById(merchantId);
        if(!(merchant!=null && merchant.getUserId().equals(authenticatedUser.getId())))
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. You can only view your merchant transactions");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }

        return transactionService.getTransactionsByMerchantId(transactionFilterRequest, pageNumber, rowCount, merchantId);

    }


    @CrossOrigin
    @RequestMapping(value="/debit-card", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Debit Card", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity debitCard(@RequestBody @Valid InitiateTransactionRequest initiateTransactionRequest,
                                    BindingResult bindingResult,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws JsonProcessingException {

        if (bindingResult.hasErrors()) {
            List errorMessageList =  bindingResult.getFieldErrors().stream().map(fe -> {
                return new ErrorMessage(fe.getField(), fe.getDefaultMessage());
            }).collect(Collectors.toList());

            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setResponseObject(errorMessageList);
            payAccessResponse.setStatusCode(PayAccessStatusCode.VALIDATION_FAILED.label);
            payAccessResponse.setMessage("Request validation failed");
            return ResponseEntity.badRequest().body(payAccessResponse);
        }


        PaymentRequest paymentRequest = paymentRequestService.createPaymentRequest(initiateTransactionRequest.getMerchantCode(),
                initiateTransactionRequest.getTerminalCode(),
                initiateTransactionRequest.getOrderRef(),
                PaymentRequestType.INITIALIZE_CARD_DEBIT,
                (new ObjectMapper()).writeValueAsString(initiateTransactionRequest)
        );


        String deviceAuthorizationToken = null;
        String authorizationToken = transactionService.getAuthorization(request, "Authorization");
//        String deviceAuthorizationToken = transactionService.getAuthorization(request, "X-Device-Auth");
        String merchantCode = initiateTransactionRequest.getMerchantCode();

        List<?> merchantDetails = merchantService.getMerchantDetails(merchantCode);
        if(merchantDetails.get(0)==null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.MERCHANT_NOT_FOUND.label);
            payAccessResponse.setMessage("Merchant details not found matching the merchant code");


            paymentRequest.setResponseBody(new ObjectMapper().writeValueAsString(payAccessResponse));
            paymentRequest = paymentRequestService.updatePaymentRequest(paymentRequest);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
        }
        Merchant merchant = (Merchant) merchantDetails.get(0);

        Terminal terminal = null;
        if(!initiateTransactionRequest.getTerminalCode().isEmpty())
            terminal = terminalService.getTerminalByTerminalCode(initiateTransactionRequest.getTerminalCode());

        try {
            ISWAuthTokenResponse iswAuthTokenResponse = iswService.getToken();
            logger.info("iswAuthTokenResponse .... {}", iswAuthTokenResponse);



            return transactionService.debitCard(iswService, merchant, terminal,
                    initiateTransactionRequest, authorizationToken, deviceAuthorizationToken, iswAuthTokenResponse, paymentRequest, paymentRequestService);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Your payment card could not be debited at this moment. Please try again later");
            payAccessResponse.setResponseObject(e.getMessage());


            paymentRequest.setResponseBody(new ObjectMapper().writeValueAsString(payAccessResponse));
            paymentRequest = paymentRequestService.updatePaymentRequest(paymentRequest);


            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }


    }




    @CrossOrigin
    @RequestMapping(value="/get-transaction-details/{merchantCode}/{orderRef}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Get details of a transaction using the Order ref", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getTransactionDetailsByOrderRef(
                                                    @PathVariable(required = true) String merchantCode,
                                                    @PathVariable(required = true) String orderRef,
                                                     HttpServletRequest request,
                                                     HttpServletResponse response) throws JsonProcessingException {


        User authenticatedUser = tokenService.getUserFromToken(request);


        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. Token expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }

        List<?> merchantList = merchantService.getMerchantDetails(merchantCode);
        Merchant merchant = merchantList!=null ? (Merchant)merchantList.get(0) : null;

        if(!(merchant!=null && merchant.getUserId().equals(authenticatedUser.getId())))
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. You can not view this transaction as this transaction belongs to another Merchant");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }
        Transaction transaction = transactionService.getTransactionByOrderRef(orderRef, merchantCode);

        if(transaction!=null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Transaction details found");
            payAccessResponse.setResponseObject(transaction);
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }
        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.ENTITY_INSTANCE_NOT_FOUND.label);
        payAccessResponse.setMessage("Transaction not found. Please check Order Ref ensuring the order reference belongs to the merchant");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);


    }

    @CrossOrigin
    @RequestMapping(value="/authorize-card-payment-otp", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Authorize Card Payment Using OTP", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity authorizeCardPaymentOtp(@RequestBody @Valid AuthenticateCardPaymentOtpRequest authenticateCardPaymentOtpRequest,
                                                     BindingResult bindingResult,
                                       HttpServletRequest request,
                                       HttpServletResponse response) throws JsonProcessingException {

        if (bindingResult.hasErrors()) {
            List errorMessageList =  bindingResult.getFieldErrors().stream().map(fe -> {
                return new ErrorMessage(fe.getField(), fe.getDefaultMessage());
            }).collect(Collectors.toList());

            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setResponseObject(errorMessageList);
            payAccessResponse.setStatusCode(PayAccessStatusCode.VALIDATION_FAILED.label);
            payAccessResponse.setMessage("Request validation failed");
            return ResponseEntity.badRequest().body(payAccessResponse);
        }


        PaymentRequest paymentRequest = paymentRequestService.createPaymentRequest(authenticateCardPaymentOtpRequest.getMerchantCode(),
                authenticateCardPaymentOtpRequest.getTerminalCode(),
                authenticateCardPaymentOtpRequest.getOrderRef(),
                PaymentRequestType.AUTHORIZE_CARD_PAYMENT_OTP,
                (new ObjectMapper()).writeValueAsString(authenticateCardPaymentOtpRequest)
        );

        String deviceAuthorizationToken = null;
        String authorizationToken = transactionService.getAuthorization(request, "Authorization");
//        String deviceAuthorizationToken = transactionService.getAuthorization(request, "X-Device-Auth");
        String merchantCode = authenticateCardPaymentOtpRequest.getMerchantCode();

        List<?> merchantDetails = merchantService.getMerchantDetails(merchantCode);
        if(merchantDetails.get(0)==null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.MERCHANT_NOT_FOUND.label);
            payAccessResponse.setMessage("Merchant details not found matching the merchant code");


            paymentRequest.setResponseBody(new ObjectMapper().writeValueAsString(payAccessResponse));
            paymentRequest = paymentRequestService.updatePaymentRequest(paymentRequest);


            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
        }
        Merchant merchant = (Merchant) merchantDetails.get(0);

        Transaction transaction = transactionService.getTransactionByOrderRef(authenticateCardPaymentOtpRequest.getOrderRef(), merchantCode);

        try {
            ISWAuthTokenResponse iswAuthTokenResponse = iswService.getToken();
            logger.info("iswAuthTokenResponse .... {}", iswAuthTokenResponse);
            return transactionService.authenticateCardPaymentOtp(iswService, merchant, authenticateCardPaymentOtpRequest,
                    authorizationToken, deviceAuthorizationToken, iswAuthTokenResponse, transaction, paymentRequest, paymentRequestService);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Validation of your One-Time password was not successful at the moment. Please try again later");
            payAccessResponse.setResponseObject(e.getMessage());


            paymentRequest.setResponseBody(new ObjectMapper().writeValueAsString(payAccessResponse));
            paymentRequest = paymentRequestService.updatePaymentRequest(paymentRequest);


            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }
    }
}
