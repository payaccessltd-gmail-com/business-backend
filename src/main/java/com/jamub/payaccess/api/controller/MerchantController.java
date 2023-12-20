package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.dao.util.UtilityHelper;
import com.jamub.payaccess.api.enums.*;
import com.jamub.payaccess.api.models.*;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.EmailService;
import com.jamub.payaccess.api.services.MerchantService;
import com.jamub.payaccess.api.services.TokenService;
import com.sun.mail.smtp.SMTPTransport;
import io.swagger.annotations.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/merchant")
@Api(produces = "application/json", value = "Operations pertaining to Merchants")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private EmailService emailService;

    @Autowired
    TokenService tokenService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${default.page.size}")
    private Integer defaultPageSize;

    @Value("${path.uploads.identification_documents}")
    private String fileDestinationPath;







    @CrossOrigin
    //ADD_NEW_MERCHANT
    @RequestMapping(value = "/add-new-merchant-to-existing-user", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Add Merchant to Existing User Profile", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity addNewMerchantToExistingUser(@RequestBody @Valid AddMerchantRequest addMerchantRequest,
                                                       BindingResult bindingResult,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {

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
        User authenticatedUser = null;
        try {
            authenticatedUser = tokenService.getUserFromToken(request);


            if(authenticatedUser==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization not granted. Token expired");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
            }
        }
        catch(Exception e)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("New merchant could not be added to your profile");
            payAccessResponse.setResponseObject(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }
        return merchantService.addNewMerchantToExistingUser(addMerchantRequest, authenticatedUser);
    }




    @CrossOrigin
    @RequestMapping(value = "/update-about-business", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Update Merchants About Business", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity newUserSignup(@RequestBody @Valid MerchantSignUpRequest merchantSignUpRequest,
                                        BindingResult bindingResult,
                HttpServletRequest request,
                HttpServletResponse response) {

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

//        boolean enumCheckCategory = UtilityHelper.checkValidEnumValue(merchantSignUpRequest.getBusinessCategory(), BusinessCategory.class);
//        boolean enumCheckType = UtilityHelper.checkValidEnumValue(merchantSignUpRequest.getBusinessType(), BusinessType.class);
//
//        if(enumCheckCategory==false || enumCheckType==false)
//        {
//
//            PayAccessResponse payAccessResponse = new PayAccessResponse();
////            payAccessResponse.setResponseObject(errorMessageList);
//            payAccessResponse.setStatusCode(PayAccessStatusCode.VALIDATION_FAILED.label);
//            payAccessResponse.setMessage("Request validation failed");
//            return ResponseEntity.badRequest().body(payAccessResponse);
//        }



        User authenticatedUser = null;
        try {
            authenticatedUser = tokenService.getUserFromToken(request);


            if(authenticatedUser==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization not granted. Token expired");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
            }

            return merchantService.updateMerchantAboutBusiness(merchantSignUpRequest, authenticatedUser);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Update about the business was not successful.");
            payAccessResponse.setResponseObject(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }
    }


    @CrossOrigin
    //UPDATE_MERCHANT
    @RequestMapping(value = "/update-merchant-country", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Update Merchants Country", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity updateMerchantCountry(@RequestBody @Valid UpdateMerchantCountryRequest updateMerchantCountryRequest,
                                                BindingResult bindingResult,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {

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
        User authenticatedUser = null;
        try {
            authenticatedUser = tokenService.getUserFromToken(request);


            if(authenticatedUser==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization not granted. Token expired");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
            }

            return merchantService.updateMerchantCountry(updateMerchantCountryRequest, authenticatedUser);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Update of country was not successful. Please try again");
            payAccessResponse.setResponseObject(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }

    }



    @CrossOrigin
    //UPDATE_MERCHANT
    @RequestMapping(value = "/update-merchant-payaccess-usage/{payAccessUse}/{merchantId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Update PayAccess Usage By Merchant", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity updateMerchantPayAccessUsage(@PathVariable String payAccessUse,
                                                          @PathVariable Long merchantId,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {
        User authenticatedUser = null;
        try {
            authenticatedUser = tokenService.getUserFromToken(request);


            if(authenticatedUser==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization not granted. Token expired");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
            }

            return merchantService.updateMerchantPayAccessUsage(payAccessUse, authenticatedUser, merchantId);
//            PayAccessResponse payAccessResponse = merchantService.updateMerchantCountry(merchantSignUpRequest, authenticatedUser);

//            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Update of merchant profile was not successful");
            payAccessResponse.setResponseObject(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }
    }



//    @CrossOrigin
//    @RequestMapping(value = "/activate-account/{emailAddress}/{verificationLink}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
//    public ResponseEntity activateMerchantAccount(@PathVariable String emailAddress, @PathVariable String verificationLink) throws JsonProcessingException {
//
//        return merchantService.activateAccount(emailAddress, verificationLink);
//    }



    @CrossOrigin
    //UPDATE_MERCHANT
    @RequestMapping(value = "/update-merchant-bio-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Update Merchant Bio Data", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity updateMerchantBioData(@Valid UpdateMerchantBioDataRequest merchantBioDataRequest,
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

        LocalDate dateOfBirthLD = LocalDate.parse(merchantBioDataRequest.getDateOfBirth());
        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            MerchantUserBioDataUpdateRequest merchantUserBioDataUpdateRequest = new MerchantUserBioDataUpdateRequest();
            merchantUserBioDataUpdateRequest.setGender(merchantBioDataRequest.getGender());
            merchantUserBioDataUpdateRequest.setEmailAddress(merchantBioDataRequest.getEmailAddress());
            merchantUserBioDataUpdateRequest.setDateOfBirth(dateOfBirthLD);
            merchantUserBioDataUpdateRequest.setIdentificationNumber(merchantBioDataRequest.getIdentificationNumber());
            merchantUserBioDataUpdateRequest.setIdentificationDocument(merchantBioDataRequest.getIdentificationDocument());
            merchantUserBioDataUpdateRequest.setMerchantId(Long.valueOf(merchantBioDataRequest.getMerchantId()));

            if(!merchantBioDataRequest.getIdentificationDocumentPath().isEmpty())
            {
                try {

                    if(UtilityHelper.checkIfImage(merchantBioDataRequest.getIdentificationDocumentPath())==false)
                    {
                        PayAccessResponse payAccessResponse = new  PayAccessResponse();
                        payAccessResponse.setStatusCode(PayAccessStatusCode.INVALID_FILE_TYPE.label);
                        payAccessResponse.setMessage("Ensure you select a valid image file as your business logo");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
                    }

                    String newFileName = UtilityHelper.uploadFile(merchantBioDataRequest.getIdentificationDocumentPath(), fileDestinationPath);
                    merchantUserBioDataUpdateRequest.setIdentificationDocumentPath(newFileName);
                    return merchantService.updateMerchantBioData(merchantUserBioDataUpdateRequest, authenticatedUser);
                }
                catch(IOException e)
                {
                    e.printStackTrace();

                    PayAccessResponse payAccessResponse = new  PayAccessResponse();
                    payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                    payAccessResponse.setMessage("Message could not be sent. Resource access denied");
                    payAccessResponse.setResponseObject(e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
                }

            }


        }


        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);

    }

    @CrossOrigin
    //UPDATE_MERCHANT
    @RequestMapping(value = "/update-merchant-kyc", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Update Merchant KYC", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity updateMerchantKYC(
                                                   @Valid UpdateMerchantKYCRequest updateMerchantKYCRequest,
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


        String governmentApprovedDocumentFileName = null;
        String directorsProofOfIdentityFileName = null;
        String businessOwnersDocumentFileName = null;
        String shareholdersDocumentFileName = null;
        User authenticatedUser = tokenService.getUserFromToken(request);

        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. Token expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }

        logger.info("{}", updateMerchantKYCRequest.getGovernmentApprovedDocument().getSize());
        logger.info("{}", updateMerchantKYCRequest.getDirectorsProofOfIdentity().getSize());
        logger.info("{}", updateMerchantKYCRequest.getBusinessOwnersDocument().getSize());
        logger.info("{}", updateMerchantKYCRequest.getShareholdersDocument().getSize());

        if(!updateMerchantKYCRequest.getGovernmentApprovedDocument().isEmpty() ||
                !updateMerchantKYCRequest.getDirectorsProofOfIdentity().isEmpty() ||
                !updateMerchantKYCRequest.getBusinessOwnersDocument().isEmpty() ||
                !updateMerchantKYCRequest.getShareholdersDocument().isEmpty())
        {
            try {
                governmentApprovedDocumentFileName = UtilityHelper.uploadFile(updateMerchantKYCRequest.getGovernmentApprovedDocument(), fileDestinationPath);
                directorsProofOfIdentityFileName = UtilityHelper.uploadFile(updateMerchantKYCRequest.getDirectorsProofOfIdentity(), fileDestinationPath);
                businessOwnersDocumentFileName = UtilityHelper.uploadFile(updateMerchantKYCRequest.getBusinessOwnersDocument(), fileDestinationPath);
                shareholdersDocumentFileName = UtilityHelper.uploadFile(updateMerchantKYCRequest.getShareholdersDocument(), fileDestinationPath);


                return merchantService.updateMerchantKYCDocuments(governmentApprovedDocumentFileName,
                        directorsProofOfIdentityFileName,
                        businessOwnersDocumentFileName,
                        shareholdersDocumentFileName,
                        updateMerchantKYCRequest.getMerchantId(),
                        authenticatedUser);
            }
            catch(IOException e)
            {
                e.printStackTrace();

                PayAccessResponse payAccessResponse = new PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
                payAccessResponse.setMessage("Invoice could not be created. Resource denial error");
                payAccessResponse.setResponseObject(e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
            }

        }



        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Merchant KYC data update was not successful1");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);


    }


    @CrossOrigin
    //UPDATE_MERCHANT
    @RequestMapping(value = "/update-merchant-business-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Update Merchant Business Data Info", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity updateMerchantBusinessData(@Valid UpdateMerchantBusinessDataRequest updateMerchantBusinessDataRequest,
                                                        BindingResult bindingResult,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws JsonProcessingException {
//        Long merchantIdL = Long.valueOf(merchantId);
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


        ArrayList<String> countryList = UtilityHelper.getCountryList();
        logger.info("{}...{}", countryList, updateMerchantBusinessDataRequest.getBusinessCountry().toUpperCase());
        if(!countryList.contains(updateMerchantBusinessDataRequest.getBusinessCountry().toUpperCase()))
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.INCOMPLETE_REQUEST.label);
            payAccessResponse.setMessage("Invalid country provided. Country provided does not match valid country list");
            return ResponseEntity.badRequest().body(payAccessResponse);
        }

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            MerchantBusinessDataUpdateRequest merchantBusinessDataUpdateRequest = new MerchantBusinessDataUpdateRequest();
            merchantBusinessDataUpdateRequest.setBusinessCity(updateMerchantBusinessDataRequest.getBusinessCity());
            merchantBusinessDataUpdateRequest.setBusinessEmail(updateMerchantBusinessDataRequest.getBusinessEmail());
            merchantBusinessDataUpdateRequest.setBusinessDescription(updateMerchantBusinessDataRequest.getBusinessDescription());
            merchantBusinessDataUpdateRequest.setBusinessState(updateMerchantBusinessDataRequest.getBusinessState().toUpperCase());
            merchantBusinessDataUpdateRequest.setBusinessWebsite(updateMerchantBusinessDataRequest.getBusinessWebsite());
            merchantBusinessDataUpdateRequest.setSupportContact(updateMerchantBusinessDataRequest.getSupportContact());
            merchantBusinessDataUpdateRequest.setPrimaryMobile(updateMerchantBusinessDataRequest.getPrimaryMobile());
            merchantBusinessDataUpdateRequest.setMerchantId(updateMerchantBusinessDataRequest.getMerchantId());
            merchantBusinessDataUpdateRequest.setBusinessAddress(updateMerchantBusinessDataRequest.getBusinessAddress());
            merchantBusinessDataUpdateRequest.setCountry(updateMerchantBusinessDataRequest.getBusinessCountry().toUpperCase());


            MultipartFile businessLogoFile = updateMerchantBusinessDataRequest.getBusinessLogoFile();
            MultipartFile businessCertificateFile = updateMerchantBusinessDataRequest.getBusinessCertificateFile();
            Merchant merchant = (Merchant)merchantService.getMerchantById(updateMerchantBusinessDataRequest.getMerchantId());

            if(merchant!=null &&
                    merchant.getBusinessType()!=null &&
                    (merchant.getBusinessType().equals(BusinessType.REGISTERED_BUSINESS) || merchant.getBusinessType().equals(BusinessType.NGO_BUSINESS)) &&
                    businessLogoFile==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
                payAccessResponse.setMessage("Ensure you select a business logo to upload");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
            }

            if(merchant!=null &&
                    merchant.getBusinessType()!=null &&
                    (merchant.getBusinessType().equals(BusinessType.REGISTERED_BUSINESS) || merchant.getBusinessType().equals(BusinessType.NGO_BUSINESS)) &&
                    businessCertificateFile==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
                payAccessResponse.setMessage("Ensure you select your business certificate to upload");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
            }


            if(merchant!=null && merchant.getBusinessType()!=null && merchant.getBusinessType().equals(BusinessType.INDIVIDUAL))
            {
                return merchantService.updateMerchantBusinessData(merchantBusinessDataUpdateRequest, updateMerchantBusinessDataRequest.getMerchantId(), authenticatedUser);

            }
            else if(merchant!=null &&
                    merchant.getBusinessType()!=null &&
                    (merchant.getBusinessType().equals(BusinessType.REGISTERED_BUSINESS) || merchant.getBusinessType().equals(BusinessType.NGO_BUSINESS)))
            {
                if(!businessLogoFile.isEmpty())
                {


                    try {
                        String newFileName = UtilityHelper.uploadFile(businessLogoFile, fileDestinationPath);
                        merchantBusinessDataUpdateRequest.setBusinessLogo(newFileName);


                        if(merchant!=null &&
                                merchant.getBusinessType()!=null &&
                                ((merchant.getBusinessType().equals(BusinessType.REGISTERED_BUSINESS) || merchant.getBusinessType().equals(BusinessType.NGO_BUSINESS))))
                        {

                            String businessCertificateFileName = UtilityHelper.uploadFile(businessLogoFile, fileDestinationPath);
                            merchantBusinessDataUpdateRequest.setBusinessCertificateFile(businessCertificateFileName);
                        }

                        return merchantService.updateMerchantBusinessData(merchantBusinessDataUpdateRequest, updateMerchantBusinessDataRequest.getMerchantId(), authenticatedUser);
                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();

                        PayAccessResponse payAccessResponse = new  PayAccessResponse();
                        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                        payAccessResponse.setMessage("Message could not be sent. Resource access denied");
                        payAccessResponse.setResponseObject(e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
                    }

                }
            }


        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);


    }


    @CrossOrigin
    //UPDATE_MERCHANT
    @RequestMapping(value = "/update-merchant-business-bank-account-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Update Merchant Bank Account Info", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity updateMerchantBusinessBankAccountData(@RequestBody @Valid MerchantBusinessBankAccountDataUpdateRequest merchantBusinessBankAccountDataUpdateRequest,
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
        if(authenticatedUser!=null)
        {
            return  merchantService.updateMerchantBusinessBankAccountData(merchantBusinessBankAccountDataUpdateRequest, authenticatedUser);

        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);


    }



    @CrossOrigin
    //UPDATE_MERCHANT
    @RequestMapping(value = "/request-merchant-approval/{merchantCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Request Approval From Admin For Merchant profile", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity requestMerchantApproval(@PathVariable String merchantCode, HttpServletRequest request,
                                                HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. Token expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }
        System.out.println("merchantCode");

        System.out.println("merchantCode..." + merchantCode);

        return merchantService.requestMerchantApproval(merchantCode, authenticatedUser);
    }


    @CrossOrigin
    //VIEW_MERCHANT
    @RequestMapping(value = "/get-merchant-details/{merchantCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Get Merchant Details", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getMerchantDetails(@PathVariable String merchantCode, HttpServletRequest request,
                                                HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. Token expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }
        System.out.println("merchantCode");

        System.out.println("merchantCode..." + merchantCode);

        List<?> merchantDetails = merchantService.getMerchantDetails(merchantCode);
        if(merchantDetails.get(0)==null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.MERCHANT_NOT_FOUND.label);
            payAccessResponse.setMessage("Merchant details not found matching the merchant code");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setResponseObject(merchantDetails);
        payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
        payAccessResponse.setMessage("Merchant details fetched successfully");
        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
    }

    @CrossOrigin
    //APPROVE_MERCHANT
    @RequestMapping(value = "/approve-merchant", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Approve Merchant", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity approveMerchant(@RequestBody @Valid MerchantStatusUpdateRequest merchantStatusUpdateRequest,
                                          BindingResult bindingResult, HttpServletRequest request,
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
        return merchantService.approveMerchant(merchantStatusUpdateRequest.getMerchantCode());

    }


    @CrossOrigin
    //DISAPPROVE_MERCHANT
    @RequestMapping(value = "/disapprove-merchant", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Disapprove Merchant", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity disapproveMerchant(@RequestBody @Valid MerchantStatusUpdateRequest merchantStatusUpdateRequest,
                                             BindingResult bindingResult, HttpServletRequest request,
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
        return merchantService.disapproveMerchant(merchantStatusUpdateRequest.getMerchantCode());
    }


    @CrossOrigin
    //UPDATE_MERCHANT_STATUS
    @RequestMapping(value = "/update-merchant-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Update Merchant Status", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity updateMerchantStatus(@RequestBody @Valid MerchantStatusUpdateRequest merchantStatusUpdateRequest,
                                               BindingResult bindingResult, HttpServletRequest request,
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
        if (authenticatedUser == null) {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. Token expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        Merchant merchant = merchantService.updateMerchantStatus(merchantStatusUpdateRequest, authenticatedUser);
        payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
        payAccessResponse.setResponseObject(merchant);
        payAccessResponse.setMessage("Merchant status updated successful");
        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
    }


    @CrossOrigin
    //VIEW_MERCHANT
    @RequestMapping(value = {"/get-merchants/{rowCount}", "/get-merchants/{rowCount}/{pageNumber}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "List Merchants", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getMerchants(
            @PathVariable(required = true) Integer rowCount,
            @PathVariable(required = false) Integer pageNumber,
            GetMerchantFilterRequest getMerchantFilterRequest,
            HttpServletRequest request,
            HttpServletResponse response) throws JsonProcessingException {

        logger.info("{}, {}", getMerchantFilterRequest.getStartDate(), getMerchantFilterRequest.getEndDate());

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. Token expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }
        return merchantService.getMerchants(pageNumber, rowCount, getMerchantFilterRequest);
    }

    @CrossOrigin
    //REVIEW_MERCHANT_STATUS
    @RequestMapping(value = "/review-merchant-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Review Merchant & Update Status (Maker-Checker)", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity reviewMerchantStatus(@RequestBody @Valid MerchantReviewUpdateStatusRequest merchantReviewUpdateStatusRequest,
                                               BindingResult bindingResult, HttpServletRequest request,
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

        List<MerchantApproval> merchantApprovalList = merchantService.fetchMerchantApprovalListByMerchant(merchantReviewUpdateStatusRequest.getMerchantCode(),
                authenticatedUser);

        List<String> existingMerchantStages  = merchantApprovalList.stream().filter(merchantApproval -> {
            return (merchantApproval.getIsValid().equals(Boolean.TRUE) && merchantApproval.getActedByUserId().equals(authenticatedUser.getId()));
        }).map(merchantApproval -> {
            return merchantApproval.getMerchantStage().name();
        }).collect(Collectors.toList());

        if(existingMerchantStages.contains(merchantReviewUpdateStatusRequest.getMerchantStage()))
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.MERCHANT_REVIEW_EXISTS.label);
            HashMap mapResp = new HashMap<>();
            mapResp.put("existingReviews", existingMerchantStages);
            payAccessResponse.setResponseObject(mapResp);
            payAccessResponse.setMessage("The merchant has already been reviewed for this action at this stage");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        Merchant merchant = merchantService.handleReviewMerchantStatus(merchantReviewUpdateStatusRequest, authenticatedUser);
        payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
        payAccessResponse.setResponseObject(merchant);
        payAccessResponse.setMessage("Merchant status updated successful");
        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);

//        payAccessResponse.setResponseObject(merchant);
//        if(merchant!=null)
//        {
//            if(merchantStatusUpdateRequest.getMerchantStatus().equals(MerchantStatus.REQUEST_UPDATE))
//            {
//                String merchantStatusForEmail = "placed on hold pending when you provide a few more information. Full details are provided below";
//                String htmlMessage = "<div style='background:#f5f5f5;background-color:#f5f5f5;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#f5f5f5;background-color:#f5f5f5;width:100%;'>	<tbody><tr>	<td><div style='Margin:0px auto;max-width:780px;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='width:100%;max-width:780px;background:#F5F5F5;background-color:#F5F5F5;'><tbody>	<tr><td style='direction:ltr;font-size:0px;padding-bottom:0px;text-align:center;vertical-align:top;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:transparent;background-color:transparent;width:100%;'><tbody>	<tr><td>	<div style='Margin:0px auto;max-width:620px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:30px;padding-bottom:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'>  </table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table><table align='center' style='width:100%;max-width:780px;background:#F5F5F5;background-color:#F5F5F5;' '=''>	<tbody><tr style='width:{headerImageWidth}px;'>	<td align='center' style='font-size:0px;padding:0px;word-break:break-word;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='border-collapse:collapse;border-spacing:0px;'>	<tbody><tr>	<td style='width:780px;padding:0pm 0px 0px 0px;padding-bottom:0px;'><img alt='Vend' height='auto' src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_header.png' style='border:0;display:block;outline:none;text-decoration:none;height:auto;width:100%;' width='{headerImageWidth}'> 	</td></tr>	</tbody></table>	</td></tr>	</tbody></table><div class='main-content' style='background:#fff;background-color:#fff;Margin:0px auto;max-width:780px;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#fff;background-color:#fff;width:100%'><tbody>	<tr><td colspan='3' style='height:30px'></td>	</tr>	<tr><td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;' class='mktoContainer' id='container'>	<table class='mktoModule' id='textSection' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable'><h1 style='text-align: center;'>Welcome to PayAccess! Let's get&nbsp;started.</h1><p style='padding-bottom: 20px;'>&nbsp;<br>Hello! Your merchant account has been reviewed and our reviewers have advised as follows:<br>" +
//
//                        "Your merchant profile has been " + merchantStatusForEmail +
//                        "<br><br>"+merchantStatusUpdateRequest.getReason()+"</p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='textSection4fc63ee1-701e-488b-a2a4-b1d5cdffd971' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable4fc63ee1-701e-488b-a2a4-b1d5cdffd971'><table width='100%' style='background-color: #e9f6e8;'>	<tbody><tr>	<td width='10%'>&nbsp;</td>	<td width='80%' align='center' style='text-align: center;'><h2 style='font-family: Helvetica, Arial, sans-serif; color: #626262; padding-top: 20px; padding-bottom: 10px;'>Your PayAccess Login Details</h2><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262; padding-bottom: 10px;'></p><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='border-collapse: separate; line-height: 100%;'>	<tbody><tr>	<td align='center' bgcolor='#41af4b' role='presentation' style='border: 2px solid transparent; border-radius: 0px; cursor: auto; padding: 14px 24px;' valign='middle'></td></tr>	</tbody></table><p>&nbsp;</p>	</td>	<td width='10%'>&nbsp;</td></tr>	</tbody></table><p>&nbsp;</p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='textSection4fc63ee1-701e-488b-a2a4-b1d5cdffd9718b5d4c4a-486c-40c3-9236-43cdf187ad89' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable4fc63ee1-701e-488b-a2a4-b1d5cdffd9718b5d4c4a-486c-40c3-9236-43cdf187ad89'><table width='100%' style='background-color: #f8f8f5;'>	<tbody><tr>	<td width='15%'>&nbsp;</td>	<td width='70%' align='center' style='text-align: center;'><h2 style='font-family: Helvetica, Arial, sans-serif; color: #626262; padding-top: 20px; padding-bottom: 10px;'>Set up PayAccess</h2><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>Get started with PayAccess by reading these four essential guides from our Help Centre and you'll be selling in no time!</p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_01.png' alt='V2439-Adoption-Onboarding-nurture-email-1_01.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Set up your outlets and registers</h3><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>Learn how to get PayAccess running on all of your registers and outlets.<br><a href='#'>Learn about registers</a></p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_02.png' alt='V2439-Adoption-Onboarding-nurture-email-1_02.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Organise your sales taxes</h3><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>Add any sales or value-added taxes (VAT) that are for your location&nbsp;or&nbsp;products.<br><a href='#'>Learn about taxes</a></p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_03.png' alt='V2439-Adoption-Onboarding-nurture-email-1_03.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Create different payment types</h3><p>Set up your payment terminal and registers so you can accept cash, cards and other&nbsp;payment&nbsp;types.<br><a href='#'>Learn about integrated payments</a></p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_04.png' alt='V2439-Adoption-Onboarding-nurture-email-1_04.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Add a product</h3><p>Start adding your products, stock levels and descriptions to&nbsp;your&nbsp;catalog.<br><a href='#'>Learn how to add products</a></p><p>&nbsp;</p>	</td>	<td width='15%'>&nbsp;</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='textSection2b16ab9a-73ae-43cf-8972-6db3159390c2' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable2b16ab9a-73ae-43cf-8972-6db3159390c2'><p>&nbsp;</p><h2>Your PayAccess to-do list</h2><p>Like to read ahead? Our <a href='#'>setup checklist</a> gives you a list of steps that you can check off at your own pace to get PayAccess set up.</p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='signoffWithoutProfilePhoto15f0ef74-c2b9-420f-a62b-2e96f521ed08' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;padding-top:30px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;'><p>Here's to your retail success, <br><br> <strong>Peters</strong> <br>Director of Adoption <br> </p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></td>	</tr>	<tr><td colspan='3' style='height:50px'></td>	</tr></tbody>	</table></div><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#f5f5f5;background-color:#f5f5f5;width:100%;'>	<tbody><tr>	<td><div style='Margin:0px auto;max-width:680px;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='width:100%;'><tbody>	<tr><td style='direction:ltr;font-size:0px;padding:5px;text-align:center;vertical-align:top;'>	<div style='background:#f5f5f5;background-color:#f5f5f5;Margin:0px auto;max-width:650px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#f5f5f5;background-color:#f5f5f5;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:15px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='center' style='font-size:0px;padding:0px;word-break:break-word;'>	<div class='mktoSnippet' id='unsubscribeFooter'><div style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>	PayAccess HQ, 2-36 Obalende Street, Abuja, Nigeria 	<br> 	<a style='color: #008cc5 !important; text-decoration: none !important;' href='#' target='_blank'>Unsubscribe</a> ∙ 	<a style='color: #008cc5 !important; text-decoration: none !important;' href='https://email.vendhq.com/Nzc2LVFGTy0zMzQAAAF_hWiDyzkDXU0MPpA_mZQkOV6uelqxQNlKl80Dp7nbfZsoBZZomppxXFRKN_z6O69Y_RlWN_c=' target='_blank'>Privacy Policy</a></div>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table></div>";
//
//
//                String recipient = merchant.getBusinessEmail();
//                String subject = "PayAccess - Merchant profile Review Update";
//                CreateEmailDocumentRequest createEmailDocumentRequest = new CreateEmailDocumentRequest();
//                createEmailDocumentRequest.setCreatedByUserId(authenticatedUser.getId());
//                createEmailDocumentRequest.setSubject(subject);
//                createEmailDocumentRequest.setRecipients(recipient);
//                createEmailDocumentRequest.setAttachmentList(null);
//                createEmailDocumentRequest.setHtmlMessage(htmlMessage);
//                EmailDocument emailDocument = emailService.createEmailDocument(createEmailDocumentRequest, authenticatedUser, EmailDocumentPriorityLevel.LOW);
//
//
//                try {
//                    logger.info("=========================");
//                    Properties props = System.getProperties();
//                    props.put("mail.smtps.host", "smtp.mailgun.org");
//                    props.put("mail.smtps.auth", "true");
//
//                    Session session = Session.getInstance(props, null);
//                    Message msg = new MimeMessage(session);
//                    msg.setFrom(new InternetAddress("emailer@payaccess.com", "PayAccess"));
//
//                    InternetAddress[] addrs = InternetAddress.parse(emailDocument.getRecipients(), false);
//                    msg.setRecipients(Message.RecipientType.TO, addrs);
//
//                    msg.setSubject("Welcome to PayAccess");
//                    msg.setContent(createEmailDocumentRequest.getHtmlMessage(), "text/html; charset=utf-8");
//
//                    //msg.setText("Copy the url and paste in your browser to activate your account - http://137.184.47.182:8081/payaccess/api/v1/user/activate-account/"+user.getEmailAddress()+"/" + verificationLink +" - providing the OTP: " + otp);
//
//                    msg.setSentDate(new Date());
//
//                    SMTPTransport t =
//                            (SMTPTransport) session.getTransport("smtps");
//                    t.connect("smtp.mailgun.org", "postmaster@mails.valuenaira.com", "k0l01qaz!QAZ");
//                    t.sendMessage(msg, msg.getAllRecipients());
//
//                    logger.info("Response: {}" , t.getLastServerResponse());
//
//                    t.close();
//
//                    emailDocument.setEmailDocumentStatus(EmailDocumentStatus.SENT);
//                    emailService.updateEmailDocument(emailDocument);
//                }
//                catch(Exception e)
//                {
//                    e.printStackTrace();
//                    logger.error("Error Sending Mail ...{}", e);
//                }
//            }
//
//
//
//            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
//            payAccessResponse.setMessage("Merchant status updated successfully");
//            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
//        }
//        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
//        payAccessResponse.setMessage("Merchant status updated failed. Invalid merchant code provided");
//        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);

    }





    @CrossOrigin
    //VIEW_MERCHANT_REVIEW
    @RequestMapping(value = {"/get-merchant-approval/{merchantCode}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "List all approvals/disapprovals of Merchants From Review", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getMerchantApproval(
            @PathVariable(required = true) String merchantCode,
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
        return merchantService.getMerchantApproval(merchantCode);
    }




    @CrossOrigin
    //SWITCH_API_MODE
    @RequestMapping(value = {"/switch-api-mode/{merchantCode}"}, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Switch API Mode (Live/Test Mode)", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity switchApiMode(
            @PathVariable(required = true) String merchantCode,
            @RequestBody @Valid SwitchApiModeRequest switchApiMode,
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

        logger.info("{}", 1);


        return merchantService.updateApiMode(merchantCode, switchApiMode.getIsLive(), authenticatedUser);
    }






}
