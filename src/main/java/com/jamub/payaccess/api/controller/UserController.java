package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.Account;
import com.jamub.payaccess.api.models.Customer;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.AccountService;
import com.jamub.payaccess.api.services.CustomerService;
import com.jamub.payaccess.api.services.TokenService;
import com.jamub.payaccess.api.services.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;
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
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    TokenService tokenService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${otp.expiry.period}")
    private long otpExpiryPeriod;

    @Value("${localhost.domain.endpoint}")
    private String localhostDomainEndpoint;

    @Value("${localhost.domain.endpoint.path}")
    private String localhostDomainEndpointPath;

    @Value("${server.port}")
    private int serverPort;
    @Value("${default.page.size}")
    private Integer defaultPageSize;


    @CrossOrigin
    @RequestMapping(value = "/new-signup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse newUserSignup(@RequestBody UserSignUpRequest userSignUpRequest) {

        PayAccessResponse payAccessResponse = userService.createNewUser(userSignUpRequest, otpExpiryPeriod, localhostDomainEndpoint, serverPort, localhostDomainEndpointPath);

        return payAccessResponse;
    }


    @CrossOrigin
    @RequestMapping(value = "/resend-signup-otp", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse resendSignupOTP(@RequestBody UserSignUpRequest userSignUpRequest) {

        PayAccessResponse payAccessResponse = userService.resendSignUpOtp(userSignUpRequest, otpExpiryPeriod, localhostDomainEndpoint, serverPort);

        return payAccessResponse;
    }



    @CrossOrigin
    @RequestMapping(value = "/update-biodata", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse newUserSignup(@RequestBody UpdateBioDataRequest updateBioDataRequest,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        User authenticatedUser = null;
        try {
            authenticatedUser = tokenService.getUserFromToken(request);


            if(authenticatedUser==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization not granted. OTP expired");
                return payAccessResponse;
            }
        }
        catch(Exception e)
        {

        }
        PayAccessResponse payAccessResponse = userService.updateBiodata(updateBioDataRequest, otpExpiryPeriod, authenticatedUser);

        return payAccessResponse;
    }


    @CrossOrigin
    @RequestMapping(value = "/activate-account", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse activateMerchantAccount(@RequestBody ActivateAccountRequest activateAccountRequest) throws JsonProcessingException {

        String emailAddress = activateAccountRequest.getEmailAddress();
        String verificationLink = activateAccountRequest.getVerificationLink();
        String otp = activateAccountRequest.getOtp();
        PayAccessResponse payAccessResponse = userService.activateAccount(emailAddress, verificationLink, otp);

        return payAccessResponse;
    }


    @CrossOrigin
    @RequestMapping(value = {"/list-users", "/list-users/{pageNumber}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse getUsersList(@PathVariable(required = false) Integer pageNumber) throws JsonProcessingException {

        PayAccessResponse payAccessResponse = userService.getUsersList(pageNumber, defaultPageSize);

        return payAccessResponse;
    }




    @CrossOrigin
    @RequestMapping(value = "/generate-user-otp", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse generateUserOtp(HttpServletRequest request,
                                           HttpServletResponse response) {
        User authenticatedUser = null;
        try {
            authenticatedUser = tokenService.getUserFromToken(request);


            if(authenticatedUser==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization not granted. OTP expired");
                return payAccessResponse;
            }
        }
        catch(Exception e)
        {

        }
        PayAccessResponse payAccessResponse = userService.generateUserOtp(otpExpiryPeriod, authenticatedUser);

        return payAccessResponse;
    }


    @CrossOrigin
    @RequestMapping(value = "/validate-user-otp", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse validateUserOtp(
                                            @RequestBody ActivateAccountRequest activateAccountRequest,
                                            HttpServletRequest request,
                                           HttpServletResponse response) {
        User authenticatedUser = null;
        try {
            authenticatedUser = tokenService.getUserFromToken(request);


            if(authenticatedUser==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization not granted. OTP expired");
                return payAccessResponse;
            }
        }
        catch(Exception e)
        {

        }
        String otp = activateAccountRequest.getOtp();
        PayAccessResponse payAccessResponse = userService.validateUserOtp(otp, authenticatedUser);

        return payAccessResponse;
    }







//    @CrossOrigin
//    @RequestMapping(value = "/activate-account", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    public PayAccessResponse activateMerchantAccount(@RequestBody ActivateCustomerAccountRequest activateCustomerAccountRequest) throws JsonProcessingException {
//
//        PayAccessResponse payAccessResponse = customerService.activateAccount(activateCustomerAccountRequest.getEmailAddress(),
//                activateCustomerAccountRequest.getVerificationLink(), activateCustomerAccountRequest.getOtp());
////        merchantService.getAllMerchants();
//
//        return payAccessResponse;
//    }
//
//
//    @CrossOrigin
//    @RequestMapping(value = "/update-customer-bio-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    public PayAccessResponse updateMerchantBioData(@RequestBody CustomerBioDataUpdateRequest customerBioDataUpdateRequest,
//                                                   HttpServletRequest request,
//                                                   HttpServletResponse response) throws JsonProcessingException {
//
//        User authenticatedUser = tokenService.getUserFromToken(request);
//        if(authenticatedUser!=null)
//        {
//            PayAccessResponse payAccessResponse = customerService.updateCustomerBioData(customerBioDataUpdateRequest, authenticatedUser, accountService);
//            return payAccessResponse;
//        }
//
//        PayAccessResponse payAccessResponse = new  PayAccessResponse();
//        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
//        payAccessResponse.setMessage("Authorization failed");
//        return payAccessResponse;
//    }
//
//
//    @CrossOrigin
//    @RequestMapping(value = "/create-customer-account", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    public PayAccessResponse createCustomerAccount(@RequestBody CustomerPinUpdateRequest customerPinUpdateRequest,
//                                                        HttpServletRequest request,
//                                                        HttpServletResponse response) throws JsonProcessingException, NoSuchAlgorithmException {
//
//        User authenticatedUser = tokenService.getUserFromToken(request);
//
//        if(authenticatedUser!=null)
//        {
//            User user = userService.getUserById(authenticatedUser.getId());
//            Customer customer = customerService.getCustomerByUserId(authenticatedUser.getId());
//            Account account = accountService.createNewCustomerWallet(
//                    user.getFirstName() + " " + user.getLastName(),
//                    customer, cbnBankCode, defaultAccountPackageCode, customerPinUpdateRequest.getPin());
////            PayAccessResponse payAccessResponse = customerService.createCustomerAccountPin(account, accountService, customerPinUpdateRequest, authenticatedUser);
//
//            if(account!=null)
//            {
//                PayAccessResponse payAccessResponse = new PayAccessResponse();
//                payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
//                payAccessResponse.setMessage("Your new wallet has been setup successfully with your pin");
//                return payAccessResponse;
//            }
//
//
//
//            PayAccessResponse payAccessResponse = new PayAccessResponse();
//            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
//            payAccessResponse.setMessage("Your new wallet was not setup successfully with your pin. Please try again");
//            return payAccessResponse;
//        }
//
//
//
//        PayAccessResponse payAccessResponse = new  PayAccessResponse();
//        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
//        payAccessResponse.setMessage("Authorization failed");
//        return payAccessResponse;
//    }



    @CrossOrigin
    @RequestMapping(value = "/forgot-password", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest,
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {

        if(forgotPasswordRequest.getEmailAddress().isEmpty())
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode("01");
            payAccessResponse.setMessage("Email address must be provided");
            return payAccessResponse;
        }


        try {

            User user = userService.getUserByEmailAddress(forgotPasswordRequest.getEmailAddress());
            if(user==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode("00");
                payAccessResponse.setMessage("An email containing a link to recover your password has been sent to you");
                return payAccessResponse;
            }

            String forgotPasswordLink = RandomStringUtils.randomAlphanumeric(128);

            String verifyUrl = "http://"+localhostDomainEndpoint+":"+serverPort+"/"+localhostDomainEndpointPath+"/api/v1/auth/update-forgot-password/"+ user.getEmailAddress()+"/" + forgotPasswordLink;
            PayAccessResponse tokenResponse = userService.updateUserForgotPasswordLink(forgotPasswordRequest.getEmailAddress(), verifyUrl, forgotPasswordLink);

            return tokenResponse;


        } catch (RuntimeException e) {
            throw new Exception("Failed to validate otp", e);
        }
    }



    @CrossOrigin
    @RequestMapping(value = "/update-forgot-password", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse updateForgotPassword(@RequestBody UpdateForgotPasswordRequest updateForgotPasswordRequest,
                                              HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {

        if(updateForgotPasswordRequest.getNewPassword().isEmpty())
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode("01");
            payAccessResponse.setMessage("New password and the confirmation password must be provided");
            return payAccessResponse;
        }


        try {

            User user = userService.getUserByEmailAddress(updateForgotPasswordRequest.getEmailAddress());

            String password = updateForgotPasswordRequest.getNewPassword();
            password = BCrypt.hashpw(password, BCrypt.gensalt(12));
            PayAccessResponse payAccessResponse = userService.forgotUserPassword(updateForgotPasswordRequest.getEmailAddress(), updateForgotPasswordRequest.getForgotPasswordLink(),
                    password);

            return payAccessResponse;


        } catch (RuntimeException e) {
            throw new Exception("Failed to validate otp", e);
        }
    }


    @CrossOrigin
    @RequestMapping(value = "/update-user-password", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse updateUserPassword(
            @RequestBody UpdatePasswordRequest updatePasswordRequest,
            HttpServletRequest request,
            HttpServletResponse response) {
        User authenticatedUser = null;
        try {
            authenticatedUser = tokenService.getUserFromToken(request);

            if(authenticatedUser==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization not granted. OTP expired");
                return payAccessResponse;
            }
            String password = updatePasswordRequest.getPassword();
            String newPassword = updatePasswordRequest.getNewPassword();
            PayAccessResponse payAccessResponse = userService.updateUserPassword(password, newPassword, authenticatedUser);
            return payAccessResponse;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode("01");
        payAccessResponse.setMessage("Password change failed");
        return payAccessResponse;
    }
}
