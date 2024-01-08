package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.dao.util.UtilityHelper;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.exception.PayAccessAuthException;
import com.jamub.payaccess.api.models.ErrorMessage;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.TokenService;
import com.jamub.payaccess.api.services.UserService;
import io.swagger.annotations.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user")
@Api(produces = "application/json", description = "Operations pertaining to User Management")
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
    @Value("${server.frontend.port}")
    private int serverFrontEndPort;
    @Value("${default.page.size}")
    private Integer defaultPageSize;



    @CrossOrigin
    @PreAuthorize("hasRole('ROLE_CREATE_ADMIN_USER')")
    @RequestMapping(value = "/new-admin-user", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Create new Administrator", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity newAdminUser(@RequestBody @Valid UserCreateRequest userCreateRequest,
                                       BindingResult bindingResult,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws PayAccessAuthException {

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


        User authenticatedUser = null;
        try {
            authenticatedUser = tokenService.getUserFromToken(request);


            if(authenticatedUser==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization not granted. Token expired");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
            }

            String ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }

            return userService.createNewAdminUser(userCreateRequest, ipAddress, authenticatedUser);
        }
        catch(Exception e)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("New administrator could not be created");
            payAccessResponse.setResponseObject(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }
    }




    @CrossOrigin
    @PreAuthorize("hasRole('ROLE_UPDATE_ADMIN_USER')")
    @RequestMapping(value = "/update-admin-user", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Update Administrator profile", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity updateAdminUser(@RequestBody @Valid UserUpdateRequest userUpdateRequest,
                                          BindingResult bindingResult,
                                          HttpServletRequest request,
                                          HttpServletResponse response) throws PayAccessAuthException {

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


        User authenticatedUser = null;
        try {
            authenticatedUser = tokenService.getUserFromToken(request);


            if(authenticatedUser==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization not granted. Token expired");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
            }

            if(userUpdateRequest.getUserId()==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.INVALID_PARAMETER.label);
                payAccessResponse.setMessage("Incomplete parameters provided. This action can not be completed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
            }

            String ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }

            return userService.updateAdminUser(userUpdateRequest, ipAddress, authenticatedUser);
        }
        catch(Exception e)
        {

            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Update of administrator profile was not successful");
            payAccessResponse.setResponseObject(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }
    }

    @CrossOrigin
    @RequestMapping(value = "/new-signup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "New Merchant Sign Up", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity newUserSignup(@RequestBody @Valid UserSignUpRequest userSignUpRequest,
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

        return userService.createNewUser(userSignUpRequest, otpExpiryPeriod, localhostDomainEndpoint, serverFrontEndPort, localhostDomainEndpointPath);
    }


    @CrossOrigin
    @RequestMapping(value = "/resend-signup-otp", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Resend Sign Up OTP", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity resendSignupOTP(@RequestBody @Valid ResendSignupOTPRequest resendSignupOTPRequest,
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

        return userService.resendSignUpOtp(resendSignupOTPRequest, otpExpiryPeriod, localhostDomainEndpoint, serverPort);
    }


    @CrossOrigin
    @PreAuthorize("hasRole('ROLE_VIEW_USER')")
    @RequestMapping(value = "/get-user-details", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Get User details", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getUserDetails(HttpServletRequest request,
                                            HttpServletResponse response) throws PayAccessAuthException {
        User authenticatedUser = null;
        try {
            authenticatedUser = tokenService.getUserFromToken(request);


            if(authenticatedUser==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization not granted. Token expired");
                return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
            }
			
			
			
			User user = userService.getUserByEmailAddress(authenticatedUser.getEmailAddress());

            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setResponseObject(user);
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("User details fetched successfully");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }
        catch(Exception e)
        {

        }
        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("User details could not be fetched");
        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
    }



    @CrossOrigin
    @PreAuthorize("hasRole('ROLE_UPDATE_USER')")
    @RequestMapping(value = "/update-biodata", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Update Users Bio-data", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity newUserSignup(@RequestBody @Valid UpdateBioDataRequest updateBioDataRequest,
                                        BindingResult bindingResult,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws PayAccessAuthException {

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

        User authenticatedUser = null;
        try {
            authenticatedUser = tokenService.getUserFromToken(request);


            if(authenticatedUser==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization not granted. Token expired");
                return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
            }
        }
        catch(Exception e)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization check was not successful. Please log out and log in again to complete this process");
            payAccessResponse.setResponseObject(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }
        return userService.updateBiodata(updateBioDataRequest, otpExpiryPeriod, authenticatedUser);
    }


    @CrossOrigin
    @RequestMapping(value = "/activate-account", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Activate users profile", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity activateMerchantAccount(@RequestBody @Valid ActivateAccountRequest activateAccountRequest,
                                                  BindingResult bindingResult) throws JsonProcessingException {

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
        String emailAddress = activateAccountRequest.getEmailAddress();
        String verificationLink = activateAccountRequest.getVerificationLink();
        String otp = activateAccountRequest.getOtp();
        return userService.activateAccount(emailAddress, verificationLink, otp);
//        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
    }


    @CrossOrigin
    @PreAuthorize("hasRole('ROLE_VIEW_USER')")
    @RequestMapping(value = {"/list-users/{rowCount}", "/list-users/{rowCount}/{pageNumber}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "List users", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getUsersList(
            @PathVariable(required = true) Integer rowCount,
            @PathVariable(required = false) Integer pageNumber,
            FilterUserRequest filterUserRequest,

            HttpServletRequest request,
            HttpServletResponse response) throws JsonProcessingException, PayAccessAuthException {
        User authenticatedUser = tokenService.getUserFromToken(request);

        logger.info("{}", authenticatedUser);

        logger.info("{}", filterUserRequest.getUserRole());

        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. Token expired");
//            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }

        if(pageNumber==null)
            pageNumber = 0;

        return userService.getUsersList(pageNumber, rowCount, filterUserRequest);

//        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
//        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
    }




    @CrossOrigin
    @PreAuthorize("hasRole('ROLE_GENERATE_OTP_FOR_USER')")
    @RequestMapping(value = "/generate-user-otp", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Generate OTP for User", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity generateUserOtp(HttpServletRequest request,
                                           HttpServletResponse response) throws PayAccessAuthException {
        User authenticatedUser = null;
        try {
            authenticatedUser = tokenService.getUserFromToken(request);


            if(authenticatedUser==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization not granted. Token expired");
                return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
            }
        }
        catch(Exception e)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Validation of your One-Time password was not successful at the moment. Please try again later");
            payAccessResponse.setResponseObject(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }
        return userService.generateUserOtp(otpExpiryPeriod, authenticatedUser);

//        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
    }


    @CrossOrigin
    @PreAuthorize("hasRole('ROLE_VALIDATE_OTP_FOR_USER')")
    @RequestMapping(value = "/validate-user-otp", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Validate OTP of User", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity validateUserOtp(
                                            @RequestBody @Valid ActivateAccountRequest activateAccountRequest,
                                            BindingResult bindingResult,
                                            HttpServletRequest request,
                                           HttpServletResponse response) throws PayAccessAuthException {

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
        User authenticatedUser = null;
        try {
            authenticatedUser = tokenService.getUserFromToken(request);


            if(authenticatedUser==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization not granted. Token expired");
                return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
            }
        }
        catch(Exception e)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Validation of your One-Time password was not successful at the moment. Please try again later");
            payAccessResponse.setResponseObject(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }
        String otp = activateAccountRequest.getOtp();
        return userService.validateUserOtp(otp, authenticatedUser);
    }







//    @CrossOrigin
//    @RequestMapping(value = "/activate-account", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity activateMerchantAccount(@RequestBody ActivateCustomerAccountRequest activateCustomerAccountRequest) throws JsonProcessingException {
//
//        PayAccessResponse payAccessResponse = customerService.activateAccount(activateCustomerAccountRequest.getEmailAddress(),
//                activateCustomerAccountRequest.getVerificationLink(), activateCustomerAccountRequest.getOtp());
////        merchantService.getAllMerchants();
//
//        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
//    }
//
//
//    @CrossOrigin
//    @RequestMapping(value = "/update-customer-bio-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity updateMerchantBioData(@RequestBody CustomerBioDataUpdateRequest customerBioDataUpdateRequest,
//                                                   HttpServletRequest request,
//                                                   HttpServletResponse response) throws JsonProcessingException {
//
//        User authenticatedUser = tokenService.getUserFromToken(request);
//        if(authenticatedUser!=null)
//        {
//            PayAccessResponse payAccessResponse = customerService.updateCustomerBioData(customerBioDataUpdateRequest, authenticatedUser, accountService);
//            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
//        }
//
//        PayAccessResponse payAccessResponse = new  PayAccessResponse();
//        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
//        payAccessResponse.setMessage("Authorization failed");
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
//    }
//
//
//    @CrossOrigin
//    @RequestMapping(value = "/create-customer-account", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity createCustomerAccount(@RequestBody CustomerPinUpdateRequest customerPinUpdateRequest,
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
//                return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
//            }
//
//
//
//            PayAccessResponse payAccessResponse = new PayAccessResponse();
//            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
//            payAccessResponse.setMessage("Your new wallet was not setup successfully with your pin. Please try again");
//            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
//        }
//
//
//
//        PayAccessResponse payAccessResponse = new  PayAccessResponse();
//        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
//        payAccessResponse.setMessage("Authorization failed");
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
//    }



    @CrossOrigin
    @RequestMapping(value = "/forgot-password", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Request Password Recovery", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity forgotPassword(@RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest,
                                         BindingResult bindingResult,
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {

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
        if(forgotPasswordRequest.getEmailAddress().isEmpty())
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode("01");
            payAccessResponse.setMessage("Email address must be provided");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }


        try {

            User user = userService.getUserByEmailAddress(forgotPasswordRequest.getEmailAddress());
            if(user==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
                payAccessResponse.setMessage("An email containing a link to recover your password has been sent to you");
                return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
            }

            String forgotPasswordLink = RandomStringUtils.randomAlphanumeric(128);

            String verifyUrl = "http://"+localhostDomainEndpoint+":"+serverFrontEndPort+"/otp?email=" + user.getEmailAddress() + "&link=" + forgotPasswordLink;
            return userService.updateUserForgotPasswordLink(forgotPasswordRequest.getEmailAddress(), verifyUrl, forgotPasswordLink, otpExpiryPeriod);



        } catch (RuntimeException e) {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Password recovery was not successful. Please try again later");
            payAccessResponse.setResponseObject(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }
    }



    @CrossOrigin
    @RequestMapping(value = "/update-forgot-password", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Recover Password", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity updateForgotPassword(@RequestBody @Valid UpdateForgotPasswordRequest updateForgotPasswordRequest,
                                               BindingResult bindingResult,
                                              HttpServletRequest request,
                                              HttpServletResponse response) throws Exception {

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
        if(updateForgotPasswordRequest.getOtp().isEmpty())
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode("01");
            payAccessResponse.setMessage("OTP must be provided");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }


        try {

            User user = userService.getUserByEmailAddress(updateForgotPasswordRequest.getEmailAddress());

            return userService.forgotUserPassword(updateForgotPasswordRequest.getEmailAddress(), updateForgotPasswordRequest.getForgotPasswordLink(),
                    updateForgotPasswordRequest.getOtp());


        } catch (RuntimeException e) {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Recovery of your password was not successful at the moment. Please try again later");
            payAccessResponse.setResponseObject(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }
    }


    @CrossOrigin
    @RequestMapping(value = "/update-forgot-password-admin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Recover Password For Administrators", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity updateForgotPasswordAdmin(@RequestBody @Valid UpdateAdminForgotPasswordRequest updateAdminForgotPasswordRequest,
                                                    BindingResult bindingResult,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) throws Exception {

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
        if(updateAdminForgotPasswordRequest.getPassword().isEmpty())
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode("01");
            payAccessResponse.setMessage("New password must be provided");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }


        try {

            User user = userService.getUserByEmailAddress(updateAdminForgotPasswordRequest.getEmailAddress());

            String password = updateAdminForgotPasswordRequest.getPassword();
            password = BCrypt.hashpw(password, BCrypt.gensalt(12));
            return userService.forgotUserPasswordAdmin(updateAdminForgotPasswordRequest.getEmailAddress(), updateAdminForgotPasswordRequest.getForgotPasswordLink(),
                    password);


        } catch (RuntimeException e) {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Password recovery was not successful at the moment. Please try again later");
            payAccessResponse.setResponseObject(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }
    }



    @CrossOrigin
    @RequestMapping(value = "/set-password", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Set Password of User during Forgot Password Process", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity setPassword(@RequestBody SetPasswordRequest setPasswordRequest,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) throws Exception {

        if(setPasswordRequest.getNewPassword().isEmpty())
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.INCOMPLETE_REQUEST.label);
            payAccessResponse.setMessage("New password must be provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
        }


        try {

            User user = userService.getUserByEmailAddress(setPasswordRequest.getEmailAddress());

            String password = setPasswordRequest.getNewPassword();
//            password = BCrypt.hashpw(password, BCrypt.gensalt(12));
            password = UtilityHelper.generateBCryptPassword(password);
            return userService.setPassword(setPasswordRequest.getEmailAddress(), setPasswordRequest.getForgotPasswordLink(),
                    password);


        } catch (RuntimeException e) {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Password reset was not successful at the moment. Please try again later");
            payAccessResponse.setResponseObject(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }
    }


    @CrossOrigin
    @PreAuthorize("hasRole('ROLE_UPDATE_USER')")
    @RequestMapping(value = "/update-user-password", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Update Users Password", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity updateUserPassword(
            @RequestBody @Valid UpdatePasswordRequest updatePasswordRequest,
            BindingResult bindingResult,
            HttpServletRequest request,
            HttpServletResponse response) throws PayAccessAuthException {

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


        User authenticatedUser = null;
        try {
            authenticatedUser = tokenService.getUserFromToken(request);

            if(authenticatedUser==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization not granted. Token expired");
                return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
            }
            String password = updatePasswordRequest.getPassword();
            String newPassword = updatePasswordRequest.getNewPassword();
            return userService.updateUserPassword(password, newPassword, authenticatedUser);
        }
        catch(Exception e)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Password change was not successful at the moment. Please try again later");
            payAccessResponse.setResponseObject(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }
    }



    @CrossOrigin
    @PreAuthorize("hasRole('ROLE_UPDATE_USER_STATUS')")
    @RequestMapping(value="/update-user-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Update Users status", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity updateUserStatus(
            @RequestBody @Valid UpdateUserStatusRequest updateUserStatusRequest, BindingResult bindingResult,
            HttpServletRequest request,
            HttpServletResponse response) throws PayAccessAuthException {

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

        User authenticatedUser = null;
        try {
            authenticatedUser = tokenService.getUserFromToken(request);

            if(authenticatedUser==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization not granted. Token expired");
                return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
            }
            String ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }
            return userService.updateUserStatus(updateUserStatusRequest, authenticatedUser, ipAddress);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Update of the users status was not successful");
            payAccessResponse.setResponseObject(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }

    }


}
