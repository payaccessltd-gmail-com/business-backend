package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/new-customer-signup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse newCustomerSignup(@RequestBody CustomerSignUpRequest customerSignUpRequest) {

        PayAccessResponse payAccessResponse = customerService.createNewCustomer(customerSignUpRequest);

        return payAccessResponse;
    }

    @RequestMapping(value = "/activate-account/{emailAddress}/{otp}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse activateMerchantAccount(@PathVariable String emailAddress, @PathVariable String otp) throws JsonProcessingException {

        PayAccessResponse payAccessResponse = customerService.activateAccount(emailAddress, otp);
//        merchantService.getAllMerchants();

        return payAccessResponse;
    }


    @RequestMapping(value = "/update-customer-bio-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse updateMerchantBioData(@RequestBody CustomerBioDataUpdateRequest customerBioDataUpdateRequest) {

        PayAccessResponse payAccessResponse = customerService.updateCustomerBioData(customerBioDataUpdateRequest);
//        merchantService.getAllMerchants();

        return payAccessResponse;
    }
    @RequestMapping(value = "/update-customer-pin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse updateMerchantBusinessData(@RequestBody CustomerPinUpdateRequest customerPinUpdateRequest) {

        PayAccessResponse payAccessResponse = customerService.updateUserPin(customerPinUpdateRequest);
//        merchantService.getAllMerchants();

        return payAccessResponse;
    }
}
