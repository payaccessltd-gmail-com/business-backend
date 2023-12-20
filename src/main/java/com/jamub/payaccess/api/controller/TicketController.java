package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.enums.TicketStatus;
import com.jamub.payaccess.api.enums.UserRole;
import com.jamub.payaccess.api.models.*;
import com.jamub.payaccess.api.models.request.AssignTransactionTicketRequest;
import com.jamub.payaccess.api.models.request.CloseTransactionTicketRequest;
import com.jamub.payaccess.api.models.request.CreateTransactionTicketRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.*;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;




@RestController
@RequestMapping("/api/v1/tickets")
@Api(produces = "application/json", value = "Operations pertaining to Support Tickets")
public class TicketController {


    @Autowired
    private TicketService ticketService;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private MerchantService merchantService;

    @CrossOrigin
    //CREATE_TICKET
    @RequestMapping(value = "/create-transaction-ticket", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Create transaction ticket", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity createTransactionTicket(@Valid CreateTransactionTicketRequest createTransactionTicketRequest,
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

        Merchant merchant = merchantService.getMerchantById(createTransactionTicketRequest.getMerchantId());
        Transaction transaction = transactionService.getTransactionByOrderRef(createTransactionTicketRequest.getOrderRef(), merchant.getMerchantCode());
        String description = "Ticket created by " + authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName() + " about ticket Ref :" + transaction.getOrderRef();

        return ticketService.createTransactionTicket(createTransactionTicketRequest, authenticatedUser, authenticatedUser.getId(),
                ipAddress, description, ApplicationAction.CREATE_TRANSACTION_TICKET,
                authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                TransactionTicket.class.getCanonicalName(), transaction.getId());

    }




    @CrossOrigin
    //ASSIGN_TICKET
    @RequestMapping(value = "/assign-transaction-ticket", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Assign transaction ticket", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity assignTransactionTicket(@RequestBody @Valid AssignTransactionTicketRequest assignTransactionTicketRequest,
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

        TransactionTicket transactionTicket = ticketService.getTransactionTicketByTicketNumber(assignTransactionTicketRequest.getTicketNumber());

        if(transactionTicket.getTicketStatus().equals(TicketStatus.CLOSED))
        {

            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setResponseObject(transactionTicket);
            payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
            payAccessResponse.setMessage("This ticket is currently closed. Closed tickets can not be assigned to another administrator");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
        }
        User userAssigned = userService.getUserById(assignTransactionTicketRequest.getAssignToUserId());

        if(!Arrays.asList(new UserRole[]{UserRole.ADMINISTRATOR}).contains(userAssigned.getUserRole()))
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.ACCESS_LEVELS_INSUFFICIENT.label);
            payAccessResponse.setMessage("Invalid request. You can only assign this ticket to administrators");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
        }

        String description = "Assign Ticket to " + userAssigned.getFirstName() + " " + userAssigned.getLastName() + " - ticket Ref :" + transactionTicket.getTicketNumber();

        return ticketService.assignTransactionTicket(userAssigned, assignTransactionTicketRequest, authenticatedUser, authenticatedUser.getId(),
                ipAddress, description, ApplicationAction.CREATE_TRANSACTION_TICKET,
                authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                TransactionTicket.class.getCanonicalName(), transactionTicket.getId());

    }





    @CrossOrigin
    //CLOSE_TICKET
    @RequestMapping(value = "/close-ticket", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Close transaction ticket", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity closeTicket(@RequestBody @Valid CloseTransactionTicketRequest closeTransactionTicketRequest,
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

        TransactionTicket transactionTicket = ticketService.getTransactionTicketByTicketNumber(closeTransactionTicketRequest.getTicketNumber());
        if(transactionTicket.getTicketStatus().equals(TicketStatus.CLOSED))
        {

            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setResponseObject(transactionTicket);
            payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
            payAccessResponse.setMessage("This ticket is currently closed. It can not be closed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
        }

        String description = "Close Ticket " + " - ticket Ref :" + transactionTicket.getTicketNumber() + " - Closed by " + authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName();

        return ticketService.closeTransactionTicket(authenticatedUser, closeTransactionTicketRequest, authenticatedUser.getId(),
                ipAddress, description, ApplicationAction.CLOSE_TRANSACTION_TICKET,
                authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
                TransactionTicket.class.getCanonicalName(), transactionTicket.getId());

    }


    @CrossOrigin
    //VIEW_TICKETS
    @RequestMapping(value = {"/get-tickets/{merchantId}/{rowCount}/{pageNumber}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "List transaction tickets", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getTickets(@PathVariable(required = true) Long merchantId,
                                      @PathVariable(required = true) Integer rowCount,
                                      @PathVariable(required = false) Integer pageNumber,
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
        Map ticketsList = ticketService.getTransactionTicketByPagination(pageNumber, rowCount, merchantId);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setResponseObject(ticketsList);
        if(ticketsList!=null)
        {
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Tickets fetched successfully");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("Ticket listing fetch failed");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
    }
}
