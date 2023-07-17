package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.models.request.MerchantSignUpRequest;
import com.jamub.payaccess.api.models.request.MerchantBusinessBankAccountDataUpdateRequest;
import com.jamub.payaccess.api.models.request.MerchantBusinessDataUpdateRequest;
import com.jamub.payaccess.api.models.request.MerchantUserBioDataUpdateRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.MerchantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/merchant")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

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
    public PayAccessResponse updateMerchantBioData(@RequestBody MerchantUserBioDataUpdateRequest merchantUserBioDataUpdateRequest) {

        PayAccessResponse payAccessResponse = merchantService.updateMerchantBioData(merchantUserBioDataUpdateRequest);
//        merchantService.getAllMerchants();

        return payAccessResponse;
    }
    @RequestMapping(value = "/update-merchant-business-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse updateMerchantBusinessData(@RequestBody MerchantBusinessDataUpdateRequest merchantBusinessDataUpdateRequest) {

        PayAccessResponse payAccessResponse = merchantService.updateMerchantBusinessData(merchantBusinessDataUpdateRequest);
//        merchantService.getAllMerchants();

        return payAccessResponse;
    }


    @RequestMapping(value = "/update-merchant-business-bank-account-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse updateMerchantBusinessBankAccountData(@RequestBody MerchantBusinessBankAccountDataUpdateRequest merchantBusinessBankAccountDataUpdateRequest) {

        PayAccessResponse payAccessResponse = merchantService.updateMerchantBusinessBankAccountData(merchantBusinessBankAccountDataUpdateRequest);
//        merchantService.getAllMerchants();

        return payAccessResponse;
    }
}
