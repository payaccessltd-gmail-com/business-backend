package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.enums.APIMode;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.exception.PayAccessAuthException;
import com.jamub.payaccess.api.models.ErrorMessage;
import com.jamub.payaccess.api.models.Merchant;
import com.jamub.payaccess.api.models.MerchantCredential;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.MerchantService;
import com.jamub.payaccess.api.services.TokenService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/developer")
@Api(produces = "application/json", description = "Operations pertaining to Developer Section.")
public class DeveloperController {


    @Autowired
    TokenService tokenService;

    @Autowired
    MerchantService merchantService;

    @Value("${path.uploads.identification_documents}")
    private String fileDestinationPath;


    @CrossOrigin
    //GENERATE_MERCHANT_KEYS
    @PreAuthorize("hasRole('ROLE_GENERATE_MERCHANT_KEYS')")
    @RequestMapping(value = "/generate-new-merchant-keys", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Generate Merchant Keys", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity updateMerchantBusinessData(@RequestBody @Valid GenerateNewMerchantKeyRequest generateNewMerchantKeyRequest,
                                                     BindingResult bindingResult,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws JsonProcessingException, PayAccessAuthException {



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
        if(authenticatedUser!=null)
        {
            Merchant merchant = merchantService.getMerchantById(generateNewMerchantKeyRequest.getMerchantId());

            if(!merchant.getUserId().equals(authenticatedUser.getId()))
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization to carry out this action denied");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
            }


            if(generateNewMerchantKeyRequest.getApiMode().toUpperCase().equals(APIMode.LIVE.name()))
                merchant = merchantService.generateNewMerchantKeys(APIMode.LIVE, generateNewMerchantKeyRequest.getMerchantId(), authenticatedUser);
            else if(generateNewMerchantKeyRequest.getApiMode().toUpperCase().equals(APIMode.TEST.name()))
                merchant = merchantService.generateNewMerchantKeys(APIMode.TEST, generateNewMerchantKeyRequest.getMerchantId(), authenticatedUser);

            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            if(merchant!=null)
                payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);

            payAccessResponse.setResponseObject(merchant);
            payAccessResponse.setMessage("Merchant keys updated");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);

    }


    @CrossOrigin
    //VIEW_MERCHANT_KEYS
    @PreAuthorize("hasRole('ROLE_VIEW_MERCHANT_KEYS')")
    @RequestMapping(value = "/get-merchant-keys/{merchantId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Get List of Merchant Keys", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getMerchantKeys(@PathVariable Long merchantId,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws JsonProcessingException, PayAccessAuthException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            Merchant merchant = merchantService.getMerchantById(merchantId);


            if(!merchant.getUserId().equals(authenticatedUser.getId()))
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization to carry out this action denied");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
            }

            MerchantCredential merchantCredential = merchantService.getMerchantKeys(merchantId, authenticatedUser);
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setResponseObject(merchantCredential);
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Merchant keys fetched");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);

    }



    @CrossOrigin
    //UPDATE_MERCHANT_CALLBACK_WEBHOOK
    @PreAuthorize("hasRole('ROLE_UPDATE_MERCHANT_CALLBACK_WEBHOOK')")
    @RequestMapping(value = "/update-merchant-callback-webhook", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Update Merchant Callback Webhook", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity updateMerchantCallbackWebhook(@RequestBody @Valid UpdateMerchantCallbackRequest updateMerchantCallbackRequest,
                                                        BindingResult bindingResult,
                                                        HttpServletRequest request,
                                                               HttpServletResponse response) throws JsonProcessingException, PayAccessAuthException {



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
        if(authenticatedUser!=null)
        {
            return merchantService.updateMerchantCallbackWebhook(updateMerchantCallbackRequest, authenticatedUser);
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
    }


}
