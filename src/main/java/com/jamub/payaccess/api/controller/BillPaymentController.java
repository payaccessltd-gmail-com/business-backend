package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.exception.PayAccessAuthException;
import com.jamub.payaccess.api.models.Account;
import com.jamub.payaccess.api.models.Customer;
import com.jamub.payaccess.api.models.ErrorMessage;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.CustomerBioDataUpdateRequest;
import com.jamub.payaccess.api.models.request.CustomerPinUpdateRequest;
import com.jamub.payaccess.api.models.request.CustomerSignUpRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.AccountService;
import com.jamub.payaccess.api.services.CustomerService;
import com.jamub.payaccess.api.services.TokenService;
import com.jamub.payaccess.api.services.UserService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bills")
@Api(produces = "application/json", description = "Operations pertaining to Bills. Not Yet Implemented")
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
    public ResponseEntity newCustomerSignup(@RequestBody @Valid CustomerSignUpRequest customerSignUpRequest,
                                            BindingResult bindingResult) {

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
        return customerService.createNewCustomer(customerSignUpRequest);
    }






    @CrossOrigin
    @RequestMapping(value = "/update-customer-bio-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateMerchantBioData(@RequestBody @Valid CustomerBioDataUpdateRequest customerBioDataUpdateRequest,
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
            return customerService.updateCustomerBioData(customerBioDataUpdateRequest, authenticatedUser, accountService);
        }


        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
    }


    @CrossOrigin
    @RequestMapping(value = "/create-customer-account", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createCustomerAccount(@RequestBody @Valid CustomerPinUpdateRequest customerPinUpdateRequest,
                                                BindingResult bindingResult,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws JsonProcessingException, NoSuchAlgorithmException, PayAccessAuthException {



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
                return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
            }



            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Your new wallet was not setup successfully with your pin. Please try again");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }



        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
    }
}
