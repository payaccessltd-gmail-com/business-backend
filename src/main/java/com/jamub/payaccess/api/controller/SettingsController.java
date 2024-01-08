package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.dao.util.UtilityHelper;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.exception.PayAccessAuthException;
import com.jamub.payaccess.api.models.CountryState;
import com.jamub.payaccess.api.models.ErrorMessage;
import com.jamub.payaccess.api.models.Merchant;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.MerchantService;
import com.jamub.payaccess.api.services.TokenService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/settings")
@Api(produces = "application/json", description = "Operations pertaining to Settings")
public class SettingsController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TokenService tokenService;

    @Autowired
    MerchantService merchantService;

    @Value("${path.uploads.identification_documents}")
    private String fileDestinationPath;


    @CrossOrigin
    //UPDATE_MERCHANT
    @PreAuthorize("hasRole('ROLE_UPDATE_MERCHANT')")
    @RequestMapping(value = "/update-merchant-business-information", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Update Merchant Business Information", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity updateMerchantBusinessData(MerchantBusinessInformationUpdateRequest merchantBusinessInformationUpdateRequest,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws JsonProcessingException, PayAccessAuthException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            Merchant merchant = merchantService.getMerchantById(merchantBusinessInformationUpdateRequest.getMerchantId());

            if(!merchant.getUserId().equals(authenticatedUser.getId()))
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization to carry out this action denied");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
            }
            MerchantBusinessDataUpdateRequest merchantBusinessDataUpdateRequest = new MerchantBusinessDataUpdateRequest();
            merchantBusinessDataUpdateRequest.setBusinessEmail(merchantBusinessInformationUpdateRequest.getBusinessEmail());
            merchantBusinessDataUpdateRequest.setBusinessDescription(merchantBusinessInformationUpdateRequest.getBusinessDescription());
            merchantBusinessDataUpdateRequest.setBusinessState(merchantBusinessInformationUpdateRequest.getBusinessState());
            merchantBusinessDataUpdateRequest.setBusinessWebsite(merchantBusinessInformationUpdateRequest.getBusinessWebsite());
            merchantBusinessDataUpdateRequest.setCountry(merchantBusinessInformationUpdateRequest.getCountry().toUpperCase());
            merchantBusinessDataUpdateRequest.setPrimaryMobile(merchantBusinessInformationUpdateRequest.getPrimaryMobile());
            merchantBusinessDataUpdateRequest.setMerchantId(merchantBusinessInformationUpdateRequest.getMerchantId());
            merchantBusinessDataUpdateRequest.setBusinessLogo(merchant.getBusinessLogo());

            MultipartFile businessLogoFile = merchantBusinessInformationUpdateRequest.getBusinessLogoFile();

            if(businessLogoFile==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
                payAccessResponse.setMessage("Ensure you select a business logo to upload");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
            }
            if(!businessLogoFile.isEmpty())
            {
                try {

                    if(UtilityHelper.checkIfImage(businessLogoFile)==false)
                    {
                        PayAccessResponse payAccessResponse = new  PayAccessResponse();
                        payAccessResponse.setStatusCode(PayAccessStatusCode.INVALID_FILE_TYPE.label);
                        payAccessResponse.setMessage("Ensure you select a valid image file as your business logo");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
                    }

                    String newFileName = UtilityHelper.uploadFile(businessLogoFile, fileDestinationPath);
                    merchantBusinessDataUpdateRequest.setBusinessLogo(newFileName);


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
            return merchantService.updateMerchantBusinessData(merchantBusinessDataUpdateRequest,
                    merchantBusinessInformationUpdateRequest.getMerchantId(), authenticatedUser);
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);


    }



    @CrossOrigin
    //UPDATE_MERCHANT
    @PreAuthorize("hasRole('ROLE_UPDATE_MERCHANT')")
    @RequestMapping(value = "/update-merchant-transaction-fee-payer", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Update Who Pays Transaction Fee", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity updateMerchantTransactionFeePayer(@RequestBody @Valid MerchantTransactionFeePayerRequest merchantTransactionFeePayerRequest,
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


//        Long merchantIdL = Long.valueOf(merchantTransactionFeePayerRequest.getMerchantId());
//        System.out.println(merchantTransactionFeePayerRequest.getMerchantId());
        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            return merchantService.updateMerchantTransactionFeePayer(
                    merchantTransactionFeePayerRequest.getMerchantMustPayTransactionFee(),
                    merchantTransactionFeePayerRequest.getMerchantId(), authenticatedUser);
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);

    }



    @CrossOrigin
    //UPDATE_MERCHANT
    @PreAuthorize("hasRole('ROLE_UPDATE_MERCHANT')")
    @RequestMapping(value = "/update-merchant-receive-earnings", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Update How Merchant Intends to Receive Earnings. By BANK_ACCOUNT or PAYACCESS_WALLET", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity updateMerchantReceiveEarnings(@RequestBody @Valid MerchantReceiveEarningsRequest merchantReceiveEarningsRequest,
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
            return merchantService.updateMerchantReceiveEarnings(
                    merchantReceiveEarningsRequest.getMerchantEarningsOption(), merchantReceiveEarningsRequest.getMerchantId(), authenticatedUser);
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
    }



    @CrossOrigin
    //UPDATE_MERCHANT
    @PreAuthorize("hasRole('ROLE_UPDATE_MERCHANT')")
    @RequestMapping(value = "/update-merchant-business-type", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Update Merchant Business Type", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity updateMerchantBusinessType(@RequestBody @Valid UpdateMerchantBusinessTypeRequest updateMerchantBusinessTypeRequest,
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
            return merchantService.updateMerchantBusinessType(updateMerchantBusinessTypeRequest.getBusinessType(),
                    updateMerchantBusinessTypeRequest.getMerchantId(), authenticatedUser);
        }


        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);

    }



    @CrossOrigin
    //UPDATE_MERCHANT
    @PreAuthorize("hasRole('ROLE_UPDATE_MERCHANT')")
    @RequestMapping(value = "/update-merchant-notifications", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Update Merchant Notifications", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity updateMerchantBusinessType(@RequestBody @Valid NotificationSettingRequest notificationSettingRequest,
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
            return merchantService.updateMerchantNotifications(notificationSettingRequest, authenticatedUser);
        }


        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
    }



    @CrossOrigin
    //UPDATE_MERCHANT
    @PreAuthorize("hasRole('ROLE_UPDATE_MERCHANT')")
    @RequestMapping(value = "/update-merchant-security", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Update Merchant Security", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity updateMerchantSecurity(@RequestBody @Valid MerchantSecuritySettingRequest merchantSecuritySettingRequest,
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
            return merchantService.updateMerchantSecurity(merchantSecuritySettingRequest, authenticatedUser);
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);

    }


    @CrossOrigin
    //UPDATE_MERCHANT
    @PreAuthorize("hasRole('ROLE_UPDATE_MERCHANT')")
    @RequestMapping(value = "/update-merchant-payment-settings", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Update Merchant Payment Settings", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity updateMerchantPaymentSetting(@RequestBody @Valid MerchantPaymentSettingRequest merchantPaymentSettingRequest,
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
            return merchantService.updateMerchantPaymentSetting(merchantPaymentSettingRequest, authenticatedUser);
        }


        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);

    }


    @CrossOrigin
    //VIEW_MERCHANT
    @PreAuthorize("hasRole('ROLE_VIEW_MERCHANT')")
    @RequestMapping(value = "/get-merchant-settings/{merchantId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "View merchant settings", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getMerchantSettings(@PathVariable Long merchantId,
                                                          HttpServletRequest request,
                                                          HttpServletResponse response) throws JsonProcessingException, PayAccessAuthException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            return merchantService.getMerchantSettings(merchantId, authenticatedUser);
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);

    }




    @CrossOrigin
    @RequestMapping(value = "/get-countries-list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "List countries", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getCountriesList(HttpServletRequest request,
                                              HttpServletResponse response) throws JsonProcessingException, PayAccessAuthException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {

            ArrayList<String> countryList = UtilityHelper.getCountryList();

            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("List of countries");
            payAccessResponse.setResponseObject(countryList);
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);

    }




    @CrossOrigin
    @RequestMapping(value = "/get-states-list/{country}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "List Nigerian States", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getStateListByCountry(@PathVariable String country,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws JsonProcessingException, PayAccessAuthException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {

            List<CountryState> stateList = merchantService.getStatesByCountry(country.toUpperCase());
            stateList = stateList.stream().sorted((o1, o2)->{
                return o1.getName().compareTo(o2.getName());
            }).collect(Collectors.toList());

            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("List of states");
            payAccessResponse.setResponseObject(stateList);
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);

    }
}
