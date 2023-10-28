package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.dao.util.UtilityHelper;
import com.jamub.payaccess.api.enums.Country;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.Merchant;
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




@RestController
@RequestMapping("/api/v1/settings")
public class SettingsController {


    @Autowired
    TokenService tokenService;

    @Autowired
    MerchantService merchantService;

    @Value("${path.uploads.identification_documents}")
    private String fileDestinationPath;


    @CrossOrigin
    @RequestMapping(value = "/update-merchant-business-information", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse updateMerchantBusinessData(MerchantBusinessInformationUpdateRequest merchantBusinessInformationUpdateRequest,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            Merchant merchant = merchantService.getMerchantById(merchantBusinessInformationUpdateRequest.getMerchantId());

            if(!merchant.getUserId().equals(authenticatedUser.getId()))
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization to carry out this action denied");
                return payAccessResponse;
            }
            MerchantBusinessDataUpdateRequest merchantBusinessDataUpdateRequest = new MerchantBusinessDataUpdateRequest();
            merchantBusinessDataUpdateRequest.setBusinessEmail(merchantBusinessInformationUpdateRequest.getBusinessEmail());
            merchantBusinessDataUpdateRequest.setBusinessDescription(merchantBusinessInformationUpdateRequest.getBusinessDescription());
            merchantBusinessDataUpdateRequest.setBusinessState(merchantBusinessInformationUpdateRequest.getBusinessState());
            merchantBusinessDataUpdateRequest.setBusinessWebsite(merchantBusinessInformationUpdateRequest.getBusinessWebsite());
            merchantBusinessDataUpdateRequest.setCountry(merchantBusinessInformationUpdateRequest.getCountry());
            merchantBusinessDataUpdateRequest.setPrimaryMobile(merchantBusinessInformationUpdateRequest.getPrimaryMobile());
            merchantBusinessDataUpdateRequest.setMerchantId(merchantBusinessInformationUpdateRequest.getMerchantId());
            merchantBusinessDataUpdateRequest.setBusinessLogo(merchant.getBusinessLogo());

            MultipartFile businessLogoFile = merchantBusinessInformationUpdateRequest.getBusinessLogoFile();
            if(!businessLogoFile.isEmpty())
            {
                try {
                    String newFileName = UtilityHelper.uploadFile(businessLogoFile, fileDestinationPath);
                    merchantBusinessDataUpdateRequest.setBusinessLogo(newFileName);


                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }

            }
            PayAccessResponse payAccessResponse = merchantService.updateMerchantBusinessData(merchantBusinessDataUpdateRequest,
                    merchantBusinessInformationUpdateRequest.getMerchantId(), authenticatedUser);

            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization failed");
        return payAccessResponse;

    }



    @CrossOrigin
    @RequestMapping(value = "/update-merchant-transaction-fee-payer", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse updateMerchantTransactionFeePayer(@RequestBody MerchantTransactionFeePayerRequest merchantTransactionFeePayerRequest,
                                                        HttpServletRequest request,
                                                               HttpServletResponse response) throws JsonProcessingException {

//        Long merchantIdL = Long.valueOf(merchantTransactionFeePayerRequest.getMerchantId());
        System.out.println(merchantTransactionFeePayerRequest.getMerchantId());
        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            PayAccessResponse payAccessResponse = merchantService.updateMerchantTransactionFeePayer(
                    merchantTransactionFeePayerRequest.getMerchantMustPayTransactionFee(),
                    merchantTransactionFeePayerRequest.getMerchantId(), authenticatedUser);

            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization failed");
        return payAccessResponse;
    }



    @CrossOrigin
    @RequestMapping(value = "/update-merchant-receive-earnings", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse updateMerchantReceiveEarnings(@RequestBody MerchantReceiveEarningsRequest merchantReceiveEarningsRequest,
                                                               HttpServletRequest request,
                                                               HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            PayAccessResponse payAccessResponse = merchantService.updateMerchantReceiveEarnings(
                    merchantReceiveEarningsRequest.getMerchantEarningsOption().name(), merchantReceiveEarningsRequest.getMerchantId(), authenticatedUser);

            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization failed");
        return payAccessResponse;
    }



    @CrossOrigin
    @RequestMapping(value = "/update-merchant-business-type", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse updateMerchantBusinessType(@RequestBody MerchantSignUpRequest merchantSignUpRequest,
                                                           HttpServletRequest request,
                                                           HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            PayAccessResponse payAccessResponse = merchantService.updateMerchantBusinessType(merchantSignUpRequest.getBusinessType(),
                    merchantSignUpRequest.getMerchantId(), authenticatedUser);

            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization failed");
        return payAccessResponse;
    }



    @CrossOrigin
    @RequestMapping(value = "/update-merchant-notifications", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse updateMerchantBusinessType(@RequestBody NotificationSettingRequest notificationSettingRequest,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            PayAccessResponse payAccessResponse = merchantService.updateMerchantNotifications(notificationSettingRequest, authenticatedUser);

            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization failed");
        return payAccessResponse;
    }



    @CrossOrigin
    @RequestMapping(value = "/update-merchant-security", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse updateMerchantSecurity(@RequestBody MerchantSecuritySettingRequest merchantSecuritySettingRequest,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            PayAccessResponse payAccessResponse = merchantService.updateMerchantSecurity(merchantSecuritySettingRequest, authenticatedUser);

            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization failed");
        return payAccessResponse;
    }


    @CrossOrigin
    @RequestMapping(value = "/update-merchant-payment-settings", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse updateMerchantPaymentSetting(@RequestBody MerchantPaymentSettingRequest merchantPaymentSettingRequest,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            PayAccessResponse payAccessResponse = merchantService.updateMerchantPaymentSetting(merchantPaymentSettingRequest, authenticatedUser);

            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization failed");
        return payAccessResponse;
    }


    @CrossOrigin
    @RequestMapping(value = "/get-merchant-settings/{merchantId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse getMerchantSettings(@PathVariable Long merchantId,
                                                          HttpServletRequest request,
                                                          HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            PayAccessResponse payAccessResponse = merchantService.getMerchantSettings(merchantId, authenticatedUser);

            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization failed");
        return payAccessResponse;
    }
}
