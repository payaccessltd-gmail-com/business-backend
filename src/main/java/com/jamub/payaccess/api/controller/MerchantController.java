package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.dao.util.UtilityHelper;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.MerchantService;
import com.jamub.payaccess.api.services.TokenService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/merchant")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    TokenService tokenService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${default.page.size}")
    private Integer defaultPageSize;

    @Value("${path.uploads.identification_documents}")
    private String fileDestinationPath;







    @CrossOrigin
    @RequestMapping(value = "/add-new-merchant-to-existing-user", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse addNewMerchantToExistingUser(@RequestBody AddMerchantRequest addMerchantRequest,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        User authenticatedUser = null;
        try {
            authenticatedUser = tokenService.getUserFromToken(request);


            if(authenticatedUser==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization not granted. OTP expired");
                return payAccessResponse;
            }
        }
        catch(Exception e)
        {

        }
        PayAccessResponse payAccessResponse = merchantService.addNewMerchantToExistingUser(addMerchantRequest, authenticatedUser);

        return payAccessResponse;
    }




    @CrossOrigin
    @RequestMapping(value = "/update-about-business", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse newUserSignup(@RequestBody MerchantSignUpRequest merchantSignUpRequest,
                HttpServletRequest request,
                HttpServletResponse response) {
        User authenticatedUser = null;
        try {
            authenticatedUser = tokenService.getUserFromToken(request);


            if(authenticatedUser==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization not granted. OTP expired");
                return payAccessResponse;
            }

            PayAccessResponse payAccessResponse = merchantService.updateMerchantAboutBusiness(merchantSignUpRequest, authenticatedUser);

            return payAccessResponse;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode. GENERAL_ERROR.label);
        payAccessResponse.setMessage("Update was not successful");
        return payAccessResponse;
    }



    @CrossOrigin
    @RequestMapping(value = "/activate-account/{emailAddress}/{verificationLink}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse activateMerchantAccount(@PathVariable String emailAddress, @PathVariable String verificationLink) throws JsonProcessingException {

        PayAccessResponse payAccessResponse = merchantService.activateAccount(emailAddress, verificationLink);


        return payAccessResponse;
    }


    @CrossOrigin
    @RequestMapping(value = "/update-merchant-bio-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse updateMerchantBioData(@RequestPart String emailAddress,
                                                   @RequestPart String gender,
                                                   @RequestPart String dateOfBirth,
                                                   @RequestPart String identificationDocument,
                                                   @RequestPart String identificationNumber,
                                                   @RequestPart String merchantId,
                                                   @RequestPart("identificationDocumentPath") MultipartFile identificationDocumentPath,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) throws JsonProcessingException {

        LocalDate dateOfBirthLD = LocalDate.parse(dateOfBirth);
        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            MerchantUserBioDataUpdateRequest merchantUserBioDataUpdateRequest = new MerchantUserBioDataUpdateRequest();
            merchantUserBioDataUpdateRequest.setGender(gender);
            merchantUserBioDataUpdateRequest.setEmailAddress(emailAddress);
            merchantUserBioDataUpdateRequest.setDateOfBirth(dateOfBirthLD);
            merchantUserBioDataUpdateRequest.setIdentificationNumber(identificationNumber);
            merchantUserBioDataUpdateRequest.setIdentificationDocument(identificationDocument);
            merchantUserBioDataUpdateRequest.setMerchantId(Long.valueOf(merchantId));

            if(!identificationDocumentPath.isEmpty())
            {
                try {
                    String newFileName = UtilityHelper.uploadFile(identificationDocumentPath, fileDestinationPath);
                    merchantUserBioDataUpdateRequest.setIdentificationDocumentPath(newFileName);
                    PayAccessResponse payAccessResponse = merchantService.updateMerchantBioData(merchantUserBioDataUpdateRequest, authenticatedUser);

                    return payAccessResponse;
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }

            }

            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Merchant bio-data update was not successful");
            return payAccessResponse;
        }


        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. OTP expired");
        return payAccessResponse;

    }


    @CrossOrigin
    @RequestMapping(value = "/update-merchant-business-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse updateMerchantBusinessData(@RequestPart String businessDescription,
                                                        @RequestPart String businessEmail,
                                                        @RequestPart String primaryMobile,
                                                        @RequestPart String supportContact,
                                                        @RequestPart String businessCity,
                                                        @RequestPart String businessState,
                                                        @RequestPart String businessWebsite,
                                                        @RequestPart String merchantId,
                                                        @RequestPart("businessLogoFile") MultipartFile businessLogoFile,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws JsonProcessingException {
        Long merchantIdL = Long.valueOf(merchantId);
        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            MerchantBusinessDataUpdateRequest merchantBusinessDataUpdateRequest = new MerchantBusinessDataUpdateRequest();
            merchantBusinessDataUpdateRequest.setBusinessCity(businessCity);
            merchantBusinessDataUpdateRequest.setBusinessEmail(businessEmail);
            merchantBusinessDataUpdateRequest.setBusinessDescription(businessDescription);
            merchantBusinessDataUpdateRequest.setBusinessState(businessState);
            merchantBusinessDataUpdateRequest.setBusinessWebsite(businessWebsite);
            merchantBusinessDataUpdateRequest.setSupportContact(supportContact);
            merchantBusinessDataUpdateRequest.setPrimaryMobile(primaryMobile);
            merchantBusinessDataUpdateRequest.setMerchantId(merchantIdL);

            if(!businessLogoFile.isEmpty())
            {
                try {
                    String newFileName = UtilityHelper.uploadFile(businessLogoFile, fileDestinationPath);
                    merchantBusinessDataUpdateRequest.setBusinessLogo(newFileName);

                    PayAccessResponse payAccessResponse = merchantService.updateMerchantBusinessData(merchantBusinessDataUpdateRequest, merchantIdL, authenticatedUser);

                    return payAccessResponse;
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }

            }

            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Merchant Business Data update failed");
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. OTP expired");
        return payAccessResponse;


    }


    @CrossOrigin
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
        payAccessResponse.setMessage("Authorization not granted. OTP expired");
        return payAccessResponse;


    }

    @CrossOrigin
    @RequestMapping(value = "/get-merchant-details/{merchantCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse getMerchantDetails(@PathVariable String merchantCode) throws JsonProcessingException {
        System.out.println("merchantCode");

        System.out.println("merchantCode..." + merchantCode);

        List<?> merchantDetails = merchantService.getMerchantDetails(merchantCode);

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setResponseObject(merchantDetails);
        payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
        payAccessResponse.setMessage("Merchant details fetched successfully");
        return payAccessResponse;
    }

    @CrossOrigin
    @RequestMapping(value = "/approve-merchant/{merchantCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse approveMerchant(@PathVariable String merchantCode) throws JsonProcessingException {


        PayAccessResponse payAccessResponse = merchantService.approveMerchant(merchantCode);


        return payAccessResponse;
    }

    @CrossOrigin
    @RequestMapping(value = {"/get-merchants", "/get-merchants/{pageNumber}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse approveMerchant(@PathVariable(required = false) Integer pageNumber) throws JsonProcessingException {

        PayAccessResponse payAccessResponse = merchantService.getMerchants(pageNumber, defaultPageSize);


        return payAccessResponse;
    }





}
