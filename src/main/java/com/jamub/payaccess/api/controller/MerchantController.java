package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.MerchantSignUpRequest;
import com.jamub.payaccess.api.models.request.MerchantBusinessBankAccountDataUpdateRequest;
import com.jamub.payaccess.api.models.request.MerchantBusinessDataUpdateRequest;
import com.jamub.payaccess.api.models.request.MerchantUserBioDataUpdateRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.MerchantService;
import com.jamub.payaccess.api.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/merchant")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    TokenService tokenService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @CrossOrigin
    @RequestMapping(value = "/new-merchant-signup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse newMerchantSignup(@RequestBody MerchantSignUpRequest merchantSignUpRequest) {

        System.out.println("Testing");

        System.out.println("Testing..." + merchantSignUpRequest.getBusinessCategory());

        PayAccessResponse payAccessResponse = merchantService.createNewMerchant(merchantSignUpRequest);
//        merchantService.getAllMerchants();

        return payAccessResponse;
    }

    @RequestMapping(value = "/activate-account/{emailAddress}/{verificationLink}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse activateMerchantAccount(@PathVariable String emailAddress, @PathVariable String verificationLink) throws JsonProcessingException {
        System.out.println("verificationLink");

        System.out.println("verificationLink..." + verificationLink);

        PayAccessResponse payAccessResponse = merchantService.activateAccount(emailAddress, verificationLink);
//        merchantService.getAllMerchants();


        return payAccessResponse;
    }


    @RequestMapping(value = "/update-merchant-bio-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse updateMerchantBioData(@RequestBody MerchantUserBioDataUpdateRequest merchantUserBioDataUpdateRequest,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) throws JsonProcessingException {

            User authenticatedUser = tokenService.getUserFromToken(request);
            if(authenticatedUser!=null)
            {
                PayAccessResponse payAccessResponse = merchantService.updateMerchantBioData(merchantUserBioDataUpdateRequest, authenticatedUser);

                return payAccessResponse;
            }

            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization failed");
            return payAccessResponse;
    }
    @RequestMapping(value = "/update-merchant-business-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse updateMerchantBusinessData(@RequestBody MerchantBusinessDataUpdateRequest merchantBusinessDataUpdateRequest,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            PayAccessResponse payAccessResponse = merchantService.updateMerchantBusinessData(merchantBusinessDataUpdateRequest, authenticatedUser);

            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization failed");
        return payAccessResponse;

    }


    @RequestMapping(value = "/update-merchant-business-bank-account-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse updateMerchantBusinessBankAccountData(@RequestBody MerchantBusinessBankAccountDataUpdateRequest merchantBusinessBankAccountDataUpdateRequest,
                                                                   HttpServletRequest request,
                                                                   HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            PayAccessResponse payAccessResponse = merchantService.updateMerchantBusinessBankAccountData(merchantBusinessBankAccountDataUpdateRequest, authenticatedUser);
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization failed");
        return payAccessResponse;

    }
}
