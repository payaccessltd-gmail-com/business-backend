package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.CustomerService;
import com.jamub.payaccess.api.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    TokenService tokenService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @CrossOrigin
    @RequestMapping(value = "/new-customer-signup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse newCustomerSignup(@RequestBody CustomerSignUpRequest customerSignUpRequest) {

        PayAccessResponse payAccessResponse = customerService.createNewCustomer(customerSignUpRequest);

        return payAccessResponse;
    }



    @CrossOrigin
    @RequestMapping(value = "/activate-account/{emailAddress}/{otp}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse activateMerchantAccount(@PathVariable String emailAddress, @PathVariable String otp) throws JsonProcessingException {

        PayAccessResponse payAccessResponse = customerService.activateAccount(emailAddress, otp);
//        merchantService.getAllMerchants();

        return payAccessResponse;
    }


    @CrossOrigin
    @RequestMapping(value = "/update-customer-bio-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse updateMerchantBioData(@RequestBody CustomerBioDataUpdateRequest customerBioDataUpdateRequest,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            PayAccessResponse payAccessResponse = customerService.updateCustomerBioData(customerBioDataUpdateRequest, authenticatedUser);
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization failed");
        return payAccessResponse;
    }


    @CrossOrigin
    @RequestMapping(value = "/update-customer-pin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse updateMerchantBusinessData(@RequestBody CustomerPinUpdateRequest customerPinUpdateRequest,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            PayAccessResponse payAccessResponse = customerService.updateUserPin(customerPinUpdateRequest, authenticatedUser);
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization failed");
        return payAccessResponse;
    }
}
