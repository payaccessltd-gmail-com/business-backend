package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.Bank;
import com.jamub.payaccess.api.models.ErrorMessage;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.CreateAcquirerRequest;
import com.jamub.payaccess.api.models.request.CreateBankRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.AcquirerService;
import com.jamub.payaccess.api.services.BankService;
import com.jamub.payaccess.api.services.TokenService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/banks")
@Api(produces = "application/json", value = "Operations pertaining to Banks")
public class BankController {


    @Autowired
    TokenService tokenService;

    @Autowired
    BankService bankService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());



    @CrossOrigin
    //CREATE_NEW_BANK
    @RequestMapping(value = "/create-new-bank", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Create New Bank", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity createNewBank(@RequestBody @Valid CreateBankRequest createBankRequest,
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

        logger.info("{}", createBankRequest.getBankCode());
        User authenticatedUser = tokenService.getUserFromToken(request);

        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. Token expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }

        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }

        String description = "Create New Bank - " + createBankRequest.getBankName();

        return this.bankService.createNewBank(createBankRequest.getBankCode(), createBankRequest.getBankName(), createBankRequest.getBankOtherName(),
                authenticatedUser.getId(), ipAddress, description,
                ApplicationAction.CREATE_NEW_BANK, authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                Bank.class.getCanonicalName(), authenticatedUser.getId());

//        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);


    }



    @CrossOrigin
    //VIEW_BANKS
    @RequestMapping(value = "/get-banks/{pageNumber}/{pageSize}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Get List of Banks", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getBanks(
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


        Map banksResp = bankService.getBanksByPagination(pageNumber, pageSize);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
        payAccessResponse.setMessage("Bank listing");
        payAccessResponse.setResponseObject(banksResp);
        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);


    }



}
