package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.enums.TerminalBrand;
import com.jamub.payaccess.api.enums.TerminalRequestStatus;
import com.jamub.payaccess.api.models.*;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.TerminalRequestService;
import com.jamub.payaccess.api.services.TerminalService;
import com.jamub.payaccess.api.services.TokenService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1/terminal")
public class TerminalController {

    @Autowired
    private TerminalRequestService terminalRequestService;

    @Autowired
    private TerminalService terminalService;

    @Autowired
    TokenService tokenService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${default.page.size}")
    private Integer defaultPageSize;










    @CrossOrigin
    @RequestMapping(value = "/create-terminal-request", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse createTerminalRequest(@RequestBody TerminalOrderRequest terminalOrderRequest,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);


        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. OTP expired");
            return payAccessResponse;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        PayAccessResponse payAccessResponse = terminalRequestService.createTerminalRequest(terminalOrderRequest, authenticatedUser);

        return payAccessResponse;

    }





    @CrossOrigin
    @RequestMapping(value = "/approve-terminal-request", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse approveTerminalRequest(@RequestPart Long terminalRequestId,
                                             @RequestPart Long acquirerId,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);


        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. OTP expired");
            return payAccessResponse;
        }
        TerminalRequest terminalRequest = terminalRequestService.getTerminalRequest(terminalRequestId, null);

        if(terminalRequest!=null && terminalRequest.getTerminalRequestStatus().equals(TerminalRequestStatus.PENDING))
        {
            terminalRequest.setTerminalRequestStatus(TerminalRequestStatus.APPROVED);
            terminalRequest = terminalRequestService.updateTerminalRequest(terminalRequest);


            if(terminalRequest!=null) {
                CreateTerminalRequest createTerminalRequest = new CreateTerminalRequest();
                createTerminalRequest.setTerminalBrand(terminalRequest.getTerminalBrand().name());
                createTerminalRequest.setTerminalType(terminalRequest.getTerminalType().name());
                createTerminalRequest.setTerminalCode(RandomStringUtils.randomAlphanumeric(8).toUpperCase());
                createTerminalRequest.setAcquirerId(acquirerId);
                createTerminalRequest.setMerchantId(terminalRequest.getMerchantId());
                createTerminalRequest.setTerminalRequestId(terminalRequest.getId());


                Terminal terminal = terminalService.createTerminal(createTerminalRequest, authenticatedUser);
                if(terminal!=null) {
                    PayAccessResponse payAccessResponse = new PayAccessResponse();
                    payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
                    payAccessResponse.setMessage("Terminal request approved successfully. Terminal(s) requested for have also been created successfully.");
                    return payAccessResponse;
                }
                PayAccessResponse payAccessResponse = new PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
                payAccessResponse.setMessage("Terminal request could not be created successfully");
                return payAccessResponse;
            }
        }


        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Terminal request approval was not successful");
        return payAccessResponse;

    }


    @CrossOrigin
    @RequestMapping(value = "/delete-terminal-request", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse deleteTerminalRequest(@RequestPart Long terminalRequestId,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);


        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. OTP expired");
            return payAccessResponse;
        }
        TerminalRequest terminalRequest = terminalRequestService.getTerminalRequest(terminalRequestId, null);

        if(terminalRequest!=null && terminalRequest.getCreatedByUserId().equals(authenticatedUser.getId()))
        {
            terminalRequest.setDeletedAt(LocalDateTime.now());
            terminalRequest.setTerminalRequestStatus(TerminalRequestStatus.DELETED);
            terminalRequest = terminalRequestService.updateTerminalRequest(terminalRequest);

            if(terminalRequest!=null) {
                PayAccessResponse payAccessResponse = new PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
                payAccessResponse.setMessage("Terminal request was successfully deleted");
                return payAccessResponse;
            }
        }


        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Terminal request was not successfully deleted");
        return payAccessResponse;

    }


    @CrossOrigin
    @RequestMapping(value = "/filter-terminal-request", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse filterTerminalRequest(@RequestBody TerminalRequestSearchFilterRequest terminalRequestSearchFilterRequest,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);


        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. OTP expired");
            return payAccessResponse;
        }
        List<TerminalRequest> terminalRequestList = terminalRequestService.getTerminalRequestsByFilter(terminalRequestSearchFilterRequest, authenticatedUser);

        if(terminalRequestList!=null) {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setResponseObject(terminalRequestList);
            payAccessResponse.setMessage("Terminal requests filtered successfully");
            return payAccessResponse;
        }


        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Terminal requests filtered were not successfully");
        return payAccessResponse;

    }



    @CrossOrigin
    @RequestMapping(value = "/get-terminal-request/{terminalRequestId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse getTerminalRequest(@PathVariable Long terminalRequestId,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws JsonProcessingException {
        System.out.println("terminalRequestId");

        System.out.println("terminalRequestId..." + terminalRequestId);

        User authenticatedUser = tokenService.getUserFromToken(request);


        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. OTP expired");
            return payAccessResponse;
        }
        TerminalRequest terminalRequest = terminalRequestService.getTerminalRequest(terminalRequestId, null);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setResponseObject(terminalRequest);
        if(terminalRequest!=null)
        {
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Terminal request details fetched successfully");
            return payAccessResponse;
        }
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("Terminal request details fetch failed");
        return payAccessResponse;
    }

    @CrossOrigin
    @RequestMapping(value = {"/get-terminal-requests", "/get-terminal-requests/{pageNumber}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse getTerminalRequests(@PathVariable(required = false) Integer pageNumber,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);

        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. OTP expired");
            return payAccessResponse;
        }
        PayAccessResponse payAccessResponse = terminalRequestService.getTerminalRequests(pageNumber, defaultPageSize);


        return payAccessResponse;
    }

    @CrossOrigin
    @RequestMapping(value = {"/get-terminals", "/get-terminals/{pageNumber}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse getTerminals(@PathVariable(required = false) Integer pageNumber,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);

        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. OTP expired");
            return payAccessResponse;
        }
        PayAccessResponse payAccessResponse = terminalService.getTerminals(pageNumber, defaultPageSize);


        return payAccessResponse;
    }




}
