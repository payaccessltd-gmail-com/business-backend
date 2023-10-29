package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.Account;
import com.jamub.payaccess.api.models.Customer;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.ISWAuthTokenResponse;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/v1/ft")
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
    public PayAccessResponse validateBankAccount(@RequestBody ValidateAccountRequest validateAccountRequest) throws Exception {

        String authorizationString = "";
        String signature = "";

        ISWAuthTokenResponse iswAuthTokenResponse = iswService.getToken();
        if(iswAuthTokenResponse==null)
            return new PayAccessResponse();


        authorizationString = iswAuthTokenResponse.getAccess_token();
        logger.info("authorizationString....{}", authorizationString);
        PayAccessResponse payAccessResponse = accountService.validateAccountRecipient(restTemplate, validateAccountRequest, apiEndpoint,
                authorizationString, signature, clientId, secretKey, merchantClientId, merchantSecretKey);

        return payAccessResponse;
    }



    @CrossOrigin
    @RequestMapping(value = "/send-funds-to-bank-account", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse activateMerchantAccount(@RequestBody ActivateAccountRequest activateCustomerAccountRequest) throws Exception {


        String authorizationString = "";
        String signature = "";
        ISWAuthTokenResponse iswAuthTokenResponse = iswService.getToken();
        if(iswAuthTokenResponse==null)
            return new PayAccessResponse();

        PayAccessResponse payAccessResponse = null;
//        payAccessResponse = accountService.sendFundsToBankAccount(iswAuthTokenResponse, restTemplate, validateAccountRequest, apiEndpoint, authorizationString, signature);
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
        payAccessResponse.setMessage("Authorization not granted. OTP expired");
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
        payAccessResponse.setMessage("Authorization not granted. OTP expired");
        return payAccessResponse;

    }
}
