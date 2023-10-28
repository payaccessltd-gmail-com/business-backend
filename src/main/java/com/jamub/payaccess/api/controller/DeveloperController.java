package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.dao.util.UtilityHelper;
import com.jamub.payaccess.api.enums.APIMode;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.Merchant;
import com.jamub.payaccess.api.models.MerchantCredential;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.MerchantService;
import com.jamub.payaccess.api.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/developer")
public class DeveloperController {


    @Autowired
    TokenService tokenService;

    @Autowired
    MerchantService merchantService;

    @Value("${path.uploads.identification_documents}")
    private String fileDestinationPath;


    @CrossOrigin
    @RequestMapping(value = "/generate-new-merchant-keys", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse updateMerchantBusinessData(@RequestBody GenerateNewMerchantKeyRequest generateNewMerchantKeyRequest,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            Merchant merchant = merchantService.getMerchantById(generateNewMerchantKeyRequest.getMerchantId());

            if(!merchant.getUserId().equals(authenticatedUser.getId()))
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization to carry out this action denied");
                return payAccessResponse;
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
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization failed");
        return payAccessResponse;

    }


    @CrossOrigin
    @RequestMapping(value = "/get-merchant-keys/{merchantId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse getMerchantKeys(@PathVariable Long merchantId,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            Merchant merchant = merchantService.getMerchantById(merchantId);


            if(!merchant.getUserId().equals(authenticatedUser.getId()))
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization to carry out this action denied");
                return payAccessResponse;
            }

            MerchantCredential merchantCredential = merchantService.getMerchantKeys(merchantId, authenticatedUser);
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setResponseObject(merchantCredential);
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Merchant keys fetched");
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization failed");
        return payAccessResponse;

    }



    @CrossOrigin
    @RequestMapping(value = "/update-merchant-callback-webhook", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse updateMerchantCallbackWebhook(@RequestBody UpdateMerchantCallbackRequest updateMerchantCallbackRequest,
                                                        HttpServletRequest request,
                                                               HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            PayAccessResponse payAccessResponse = merchantService.updateMerchantCallbackWebhook(updateMerchantCallbackRequest, authenticatedUser);

            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization failed");
        return payAccessResponse;
    }


}
