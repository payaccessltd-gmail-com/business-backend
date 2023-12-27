package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.Bank;
import com.jamub.payaccess.api.models.ErrorMessage;
import com.jamub.payaccess.api.models.PaymentRequest;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.CreateBankRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.BankService;
import com.jamub.payaccess.api.services.PaymentRequestService;
import com.jamub.payaccess.api.services.TokenService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/payment-request")
@Api(produces = "application/json", value = "Operations pertaining to Requests From Payments")
public class PaymentRequestController {


    @Autowired
    TokenService tokenService;

    @Autowired
    PaymentRequestService paymentRequestService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @CrossOrigin
    //VIEW_PAYMENT_REQUEST
    @PreAuthorize("hasRole('ROLE_VIEW_PAYMENT_REQUEST')")
    @RequestMapping(value = "/get-payment-requests/{pageNumber}/{pageSize}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "List Requests For Payments", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getPaymentRequests(
            @PathVariable(required = true) Integer pageNumber,
            @PathVariable(required = false) Integer pageSize,
            HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);

        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. Token expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }


        Map paymentRequestsResp = paymentRequestService.getPaymentRequestByPagination(pageNumber, pageSize);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
        payAccessResponse.setMessage("Payment requests listing");
        payAccessResponse.setResponseObject(paymentRequestsResp);
        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);


    }



    @CrossOrigin
    //VIEW_PAYMENT_REQUEST
    @PreAuthorize("hasRole('ROLE_VIEW_PAYMENT_REQUEST')")
    @RequestMapping(value = "/get-payment-request-by-id/{paymentRequestId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Get Payment Request By Id", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getPaymentRequestById(
            @PathVariable(required = true) Long paymentRequestId,
            HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);

        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. Token expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }


        PaymentRequest paymentRequestsResp = paymentRequestService.getPaymentRequestById(paymentRequestId);

        if(paymentRequestsResp==null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
            payAccessResponse.setMessage("Payment requests details not found");
            payAccessResponse.setResponseObject(paymentRequestsResp);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
        }
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
        payAccessResponse.setMessage("Payment requests details");
        payAccessResponse.setResponseObject(paymentRequestsResp);
        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);


    }


}
