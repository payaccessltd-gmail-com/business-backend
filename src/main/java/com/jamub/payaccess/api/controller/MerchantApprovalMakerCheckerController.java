package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.enums.MakerCheckerType;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.*;
import com.jamub.payaccess.api.models.request.MerchantApprovalMakerCheckerRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/maker-checker")
@Api(produces = "application/json", value = "Operations pertaining to Maker-Checker")
public class MerchantApprovalMakerCheckerController {


    @Autowired
    private MerchantService merchantService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserService userService;

    @CrossOrigin
    //CREATE_MAKER_CHECKER
    @PreAuthorize("hasRole('ROLE_CREATE_MAKER_CHECKER')")
    @RequestMapping(value = "/create-maker-checker", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Create Maker-Checker", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity createMakerChecker(@RequestBody @Valid MerchantApprovalMakerCheckerRequest merchantApprovalMakerCheckerRequest,
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

        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }


        List<MakerChecker> makerCheckers = merchantService.getMakerCheckerByUser(merchantApprovalMakerCheckerRequest.getApproverEmailAddress(),
                MakerCheckerType.MERCHANT_APPROVAL.name());

        if(!makerCheckers.isEmpty())
        {

            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
            payAccessResponse.setMessage("Maker-checker entry for this user already exists. You can not have the same user withing a maker-checker matrix");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }

        User user = userService.getUserByEmailAddress(merchantApprovalMakerCheckerRequest.getApproverEmailAddress());
        String description = "Maker Checker created by " + authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName() + " for the User :" +
                merchantApprovalMakerCheckerRequest.getApproverEmailAddress() + " with the approval level " + merchantApprovalMakerCheckerRequest.getCheckerLevel();


        return merchantService.createMerchantApprovalMakerChecker(merchantApprovalMakerCheckerRequest, authenticatedUser, authenticatedUser.getId(),
                ipAddress, description, ApplicationAction.CREATE_MAKER_CHECKER,
                authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                MakerChecker.class.getCanonicalName(), null);

    }

    @CrossOrigin
    //VIEW_MAKER_CHECKER
    @PreAuthorize("hasRole('ROLE_VIEW_MAKER_CHECKER')")
    @RequestMapping(value = "/get-maker-checker-by-user/{emailAddress}/{makerCheckerType}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Get List of Maker Checker By User Email Address", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getMakerCheckerByUser(@PathVariable(required = true) String emailAddress,
                                                @PathVariable(required = true) String makerCheckerType,
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

        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }



        List<MakerChecker> makerChecker = merchantService.getMakerCheckerByUser(emailAddress, makerCheckerType);

        if(makerChecker!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setResponseObject(makerChecker);
            payAccessResponse.setMessage("Maker-checkers fetched successfully");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("Maker-checker could not be found");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);

    }



    @CrossOrigin
    //VIEW_MAKER_CHECKER
    @PreAuthorize("hasRole('ROLE_VIEW_MAKER_CHECKER')")
    @RequestMapping(value = {"/get-maker-checker-list/{rowCount}/{pageNumber}/{makerCheckerType}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Get List of Maker-Checker By Type", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getMakerCheckerListByType(@PathVariable(required = false) String makerCheckerType,
                                     @PathVariable(required = true) Integer rowCount,
                                     @PathVariable(required = true) Integer pageNumber,
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
        Map ticketsList = merchantService.getMakerCheckerList(makerCheckerType, rowCount, pageNumber);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setResponseObject(ticketsList);
        if(ticketsList!=null)
        {
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Maker-checker list fetched successfully");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("Maker-checker listing fetch failed");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
    }
}
