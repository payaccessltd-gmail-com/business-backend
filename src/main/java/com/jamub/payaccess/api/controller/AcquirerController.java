package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.dao.util.UtilityHelper;
import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.Acquirer;
import com.jamub.payaccess.api.models.Bank;
import com.jamub.payaccess.api.models.ErrorMessage;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.CreateAcquirerRequest;
import com.jamub.payaccess.api.models.request.CreateContactUsRequest;
import com.jamub.payaccess.api.models.request.CreateFeedbackRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.AcquirerService;
import com.jamub.payaccess.api.services.BankService;
import com.jamub.payaccess.api.services.MerchantService;
import com.jamub.payaccess.api.services.TokenService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/acquirers")
@Api(produces = "application/json", value = "Operations pertaining to management of Acquirers")
public class AcquirerController {


    @Autowired
    TokenService tokenService;

    @Autowired
    AcquirerService acquirerService;

    @Autowired
    BankService bankService;


    @CrossOrigin
    //CREATE_NEW_ACQUIRER
    @RequestMapping(value = "/create-new-acquirer", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Create a new Acquirer", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity createNewAcquirer(@RequestBody @Valid CreateAcquirerRequest createAcquirerRequest,
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


        Bank bank = this.bankService.getBankByBankCode(createAcquirerRequest.getBankCode());

        if(bank==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Invalid bank specified");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }

        Long actorId = authenticatedUser.getId();
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        String description = "Create New Acquirer - " + createAcquirerRequest.getAcquirerName() + " ("+createAcquirerRequest.getAcquirerCode().toUpperCase()+")";

        return acquirerService.createNewAcquirer(createAcquirerRequest.getAcquirerCode().toUpperCase(),
                createAcquirerRequest.getAcquirerName(), bank, authenticatedUser, createAcquirerRequest.getIsBank(),
                actorId, ipAddress, description,
                ApplicationAction.CREATE_NEW_ACQUIRER, authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                Acquirer.class.getCanonicalName(), null);

//        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);


    }



    @CrossOrigin
    //VIEW_ACQUIRERS
    @RequestMapping(value = "/get-acquirers/{pageNumber}/{pageSize}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Get List of Acquirer", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getAcquirers(
            @PathVariable(required = true) Integer pageNumber,
            @PathVariable(required = false) Integer pageSize,
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


        return acquirerService.getAcquirers(authenticatedUser, pageNumber, pageSize);


    }



}
