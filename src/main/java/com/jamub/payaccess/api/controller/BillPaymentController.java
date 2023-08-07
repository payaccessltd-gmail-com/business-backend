package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.Account;
import com.jamub.payaccess.api.models.Customer;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.ActivateCustomerAccountRequest;
import com.jamub.payaccess.api.models.request.CustomerBioDataUpdateRequest;
import com.jamub.payaccess.api.models.request.CustomerPinUpdateRequest;
import com.jamub.payaccess.api.models.request.CustomerSignUpRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.AccountService;
import com.jamub.payaccess.api.services.CustomerService;
import com.jamub.payaccess.api.services.TokenService;
import com.jamub.payaccess.api.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/v1/bills")
public class BillPaymentController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    TokenService tokenService;

    @Autowired
    UserService userService;

    @Autowired
    AccountService accountService;



    @Value("${cbn.bank.code}")
    private String cbnBankCode;

    @Value("${default.account.package.code}")
    private String defaultAccountPackageCode;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @CrossOrigin
    @RequestMapping(value = "/new-customer-signup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse newCustomerSignup(@RequestBody CustomerSignUpRequest customerSignUpRequest) {

        PayAccessResponse payAccessResponse = customerService.createNewCustomer(customerSignUpRequest);

        return payAccessResponse;
    }



    @CrossOrigin
    @RequestMapping(value = "/activate-account", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse activateMerchantAccount(@RequestBody ActivateCustomerAccountRequest activateCustomerAccountRequest) throws JsonProcessingException {

        PayAccessResponse payAccessResponse = customerService.activateAccount(activateCustomerAccountRequest.getEmailAddress(),
                activateCustomerAccountRequest.getVerificationLink(), activateCustomerAccountRequest.getOtp());
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
            PayAccessResponse payAccessResponse = customerService.updateCustomerBioData(customerBioDataUpdateRequest, authenticatedUser, accountService);
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization failed");
        return payAccessResponse;
    }


    @CrossOrigin
    @RequestMapping(value = "/create-customer-account", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse createCustomerAccount(@RequestBody CustomerPinUpdateRequest customerPinUpdateRequest,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws JsonProcessingException, NoSuchAlgorithmException {

        User authenticatedUser = tokenService.getUserFromToken(request);

        if(authenticatedUser!=null)
        {
            User user = userService.getUserById(authenticatedUser.getId());
            Customer customer = customerService.getCustomerByUserId(authenticatedUser.getId());
            Account account = accountService.createNewCustomerWallet(
                    user.getFirstName() + " " + user.getLastName(),
                    customer, cbnBankCode, defaultAccountPackageCode, customerPinUpdateRequest.getPin());
//            PayAccessResponse payAccessResponse = customerService.createCustomerAccountPin(account, accountService, customerPinUpdateRequest, authenticatedUser);

            if(account!=null)
            {
                PayAccessResponse payAccessResponse = new PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
                payAccessResponse.setMessage("Your new wallet has been setup successfully with your pin");
                return payAccessResponse;
            }



            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Your new wallet was not setup successfully with your pin. Please try again");
            return payAccessResponse;
        }



        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization failed");
        return payAccessResponse;
    }
}
