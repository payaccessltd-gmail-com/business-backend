package com.jamub.payaccess.api.controller;


import com.jamub.payaccess.api.models.request.CustomerSignUpRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
public class HolidayContoller {

    @Autowired
    private CustomerService customerService;


    @CrossOrigin
    @RequestMapping(value = "/get-holiday", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse newCustomerSignup(@RequestBody CustomerSignUpRequest customerSignUpRequest) {

        PayAccessResponse payAccessResponse = customerService.createNewCustomer(customerSignUpRequest);

        return payAccessResponse;
    }
}
