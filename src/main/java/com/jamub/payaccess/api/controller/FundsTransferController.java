package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.Account;
import com.jamub.payaccess.api.models.Customer;
import com.jamub.payaccess.api.models.ErrorMessage;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.ISWAuthTokenResponse;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/ft")
@Api(produces = "application/json", value = "Operations pertaining to Funds Transfer. Not Yet Implemented")
public class FundsTransferController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    TokenService tokenService;

    @Autowired
    UserService userService;

    @Autowired
    AccountService accountService;

    @Autowired
    ISWService iswService;

    @Value("${isw.passport.oauth.token.url}")
    private String oauthTokenEndpoint;
    @Value("${isw.passport.oauth.clientId}")
    private String clientId;

    @Value("${isw.merchant.clientId}")
    private String merchantClientId;

    @Value("${isw.merchant.secretKey}")
    private String merchantSecretKey;

    @Value("${isw.passport.oauth.secretKey}")
    private String secretKey;


    @Value("${interswitch.api.endpoint}")
    private String apiEndpoint;

    @Value("${default.account.package.code}")
    private String defaultAccountPackageCode;
    @Value("${cbn.bank.code}")
    private String cbnBankCode;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestTemplate restTemplate;


    @CrossOrigin
    @RequestMapping(value = "/validate-bank-account", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    public ResponseEntity validateBankAccount(@RequestBody @Valid ValidateAccountRequest validateAccountRequest,
                                              BindingResult bindingResult) throws Exception {


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

        String authorizationString = "";
        String signature = "";

        ISWAuthTokenResponse iswAuthTokenResponse = iswService.getToken();
        if(iswAuthTokenResponse==null) {
            PayAccessResponse payAccessResponse =  new PayAccessResponse();
            payAccessResponse.setMessage("Authorization token to proceed with this request could not be granted");
            payAccessResponse.setStatusCode(PayAccessStatusCode.SWITCH_AUTHORIZATION_GRANT_DENIED.label);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }


        authorizationString = iswAuthTokenResponse.getAccess_token();
        logger.info("authorizationString....{}", authorizationString);
        return accountService.validateAccountRecipient(restTemplate, validateAccountRequest, apiEndpoint,
                authorizationString, signature, clientId, secretKey, merchantClientId, merchantSecretKey);
    }



    @CrossOrigin
    @RequestMapping(value = "/send-funds-to-bank-account", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    public ResponseEntity activateMerchantAccount(@RequestBody @Valid ActivateAccountRequest activateCustomerAccountRequest,
                                                  BindingResult bindingResult) throws Exception {


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
        String authorizationString = "";
        String signature = "";
        ISWAuthTokenResponse iswAuthTokenResponse = iswService.getToken();
        if(iswAuthTokenResponse==null) {
            PayAccessResponse payAccessResponse =  new PayAccessResponse();
            payAccessResponse.setMessage("Authorization token to proceed with this request could not be granted");
            payAccessResponse.setStatusCode(PayAccessStatusCode.SWITCH_AUTHORIZATION_GRANT_DENIED.label);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }

        PayAccessResponse payAccessResponse = null;
//        payAccessResponse = accountService.sendFundsToBankAccount(iswAuthTokenResponse, restTemplate, validateAccountRequest, apiEndpoint, authorizationString, signature);
//        merchantService.getAllMerchants();

        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
    }


    @CrossOrigin
    @RequestMapping(value = "/update-customer-bio-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    public ResponseEntity updateMerchantBioData(@RequestBody @Valid CustomerBioDataUpdateRequest customerBioDataUpdateRequest,
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
                                                        HttpServletResponse response) throws JsonProcessingException, NoSuchAlgorithmException {



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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);

    }
}
