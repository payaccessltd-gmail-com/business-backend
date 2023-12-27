package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.models.*;
import com.jamub.payaccess.api.models.response.AuthenticateResponse;
import com.jamub.payaccess.api.providers.TokenProvider;
import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.request.CreateAcquirerRequest;
import com.jamub.payaccess.api.models.request.LoginRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.*;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/acquirers")
@Api(produces = "application/json", value = "Operations pertaining to management of Acquirers")
public class AcquirerController {


    @Autowired
    TokenService tokenService;

    @Autowired
    AcquirerService acquirerService;

    @Autowired
    MerchantService merchantService;

    @Autowired
    BankService bankService;

    @Autowired
    UserService userService;
//
//    @Autowired
//    private BCryptPasswordEncoder bcryptEncoder;


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider jwtTokenUtil;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> generateToken(@RequestBody LoginRequest loginUser) throws AuthenticationException, JsonProcessingException {

        logger.info("111111111111....{}", loginUser.getUsername());
//        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser.getEmailAddress(), loginUser.getPassword());
//        logger.info("{}", authenticationToken.getPrincipal());
//        Authentication authentication = authenticationManager.authenticate(authenticationToken);
//        logger.info("{}", authentication.isAuthenticated());
        logger.info("2222....{}", loginUser.getPassword());
        final Authentication authentication = authenticationManager.authenticate(
//                new AuthenticationManagerCustom(loginUser.getEmailAddress(), loginUser.getPassword())
//                new PayAccessAuthenticationProvider()

                new UsernamePasswordAuthenticationToken(
                        loginUser.getUsername(),
                        loginUser.getPassword()
                )
        );

        logger.info("{}", authentication.isAuthenticated());
        logger.info("{}", authentication.getPrincipal());
//        logger.info("{}>>>>", loginUser.getEmailAddress());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = jwtTokenUtil.generateToken(authentication);
        logger.info("token...{}", token);


        List<AuthMerchantData> merchantList = merchantService.getMerchantIdsByUsername(loginUser.getUsername());
        AuthenticateResponse authenticateResponse = new AuthenticateResponse();
        authenticateResponse.setToken(token);
        authenticateResponse.setSubject(loginUser.getUsername());
        authenticateResponse.setMerchantList(merchantList);


        return ResponseEntity.ok(authenticateResponse);
    }



    @CrossOrigin
    @PreAuthorize("hasRole('ROLE_CREATE_NEW_ACQUIRER')")
    @RequestMapping(value = "/create-user", method = RequestMethod.POST)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    public ResponseEntity<?> createUser(@RequestBody @Valid User loginUser,
                                        BindingResult bindingResult,
                                        HttpServletRequest request,
                                        HttpServletResponse response) throws AuthenticationException {

        logger.info("{}...username", 12);
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();
        logger.info("{}...username", username);
//        @RequestBody User loginUser
//        String password = "password";
////        password = bcryptEncoder.encode(password);
//        logger.info("{}...password", password);
//        return userService.createNewUserV2(loginUser, password);
        return ResponseEntity.ok("token");
    }

    @CrossOrigin
    //CREATE_NEW_ACQUIRER
    @PreAuthorize("hasRole('ROLE_CREATE_NEW_ACQUIRER')")
    @RequestMapping(value = "/create-new-acquirer", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Create a new Acquirer", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity createNewAcquirer(@RequestBody @Valid CreateAcquirerRequest createAcquirerRequest,
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

        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. Token expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }


        Bank bank = this.bankService.getBankByBankCode(createAcquirerRequest.getBankCode());

        if(bank==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Invalid bank specified");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }

        Long actorId = authenticatedUser.getId();
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        String description = "Create New Acquirer - " + createAcquirerRequest.getAcquirerName() + " ("+createAcquirerRequest.getAcquirerCode().toUpperCase()+")";

        return acquirerService.createNewAcquirer(createAcquirerRequest.getAcquirerCode().toUpperCase(),
                createAcquirerRequest.getAcquirerName(), bank, authenticatedUser, createAcquirerRequest.getIsBank(),
                actorId, ipAddress, description,
                ApplicationAction.CREATE_NEW_ACQUIRER, authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                Acquirer.class.getCanonicalName(), null);

//        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);


    }



    @CrossOrigin
    //VIEW_ACQUIRERS
    @PreAuthorize("hasRole('ROLE_VIEW_ACQUIRERS')")
    @RequestMapping(value = "/get-acquirers/{pageNumber}/{pageSize}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Get List of Acquirer", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getAcquirers(
            @PathVariable(required = true) Integer pageNumber,
            @PathVariable(required = false) Integer pageSize,
            HttpServletRequest request,
                                            HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);

        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. Token expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }


        return acquirerService.getAcquirers(authenticatedUser, pageNumber, pageSize);


    }



}
