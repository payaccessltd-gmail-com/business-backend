package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
//import com.itextpdf.text.DocumentException;
import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.enums.TicketStatus;
import com.jamub.payaccess.api.enums.UserRole;
import com.jamub.payaccess.api.exception.PayAccessAuthException;
import com.jamub.payaccess.api.models.*;
import com.jamub.payaccess.api.models.request.AssignTransactionTicketRequest;
import com.jamub.payaccess.api.models.request.CloseTransactionTicketRequest;
import com.jamub.payaccess.api.models.request.CreateTransactionTicketRequest;
import com.jamub.payaccess.api.models.request.TransactionFilterRequest;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/reports")
@Api(produces = "application/json", description = "Operations pertaining to Reports. Under consideration for implementation")
public class ReportController {


//    @Autowired
//    private ReportService reportService;
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private TransactionService transactionService;
//    @Autowired
//    private TokenService tokenService;
//    @Autowired
//    private MerchantService merchantService;
//
//    @CrossOrigin
//    //CREATE_TICKET
//    @PreAuthorize("hasRole('ROLE_CREATE_TICKET')")
//    @RequestMapping(value = "/create-transaction-report", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
//    @ApiOperation(value = "Create transaction report", response = ResponseEntity.class)
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "Successful"),
//            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
//            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
//            @ApiResponse(code = 500, message = "Application failed to process the request")
//    })
//    public ResponseEntity createTransactionReport(@RequestBody @Valid TransactionFilterRequest transactionFilterRequest,
//                                                  BindingResult bindingResult,
//                                                  HttpServletRequest request,
//                                                  HttpServletResponse response) throws IOException, PayAccessAuthException {
//
//
//
//        if (bindingResult.hasErrors()) {
//            List errorMessageList =  bindingResult.getFieldErrors().stream().map(fe -> {
//                return new ErrorMessage(fe.getField(), fe.getDefaultMessage());
//            }).collect(Collectors.toList());
//
//            PayAccessResponse payAccessResponse = new PayAccessResponse();
//            payAccessResponse.setResponseObject(errorMessageList);
//            payAccessResponse.setStatusCode(PayAccessStatusCode.VALIDATION_FAILED.label);
//            payAccessResponse.setMessage("Request validation failed");
//            return ResponseEntity.badRequest().body(payAccessResponse);
//        }
//        User authenticatedUser = tokenService.getUserFromToken(request);
//
//
//        if(authenticatedUser==null)
//        {
//            PayAccessResponse payAccessResponse = new  PayAccessResponse();
//            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
//            payAccessResponse.setMessage("Authorization not granted. Token expired");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
//        }
//
//        String ipAddress = request.getHeader("X-FORWARDED-FOR");
//        if (ipAddress == null) {
//            ipAddress = request.getRemoteAddr();
//        }
//
//
//        String description = "Generate Transaction Report";
//
//
//        return reportService.createTransactionReport(transactionFilterRequest, authenticatedUser, authenticatedUser.getId(),
//                ipAddress, description, ApplicationAction.CREATE_TRANSACTION_TICKET,
//                authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName(),
//                TransactionTicket.class.getCanonicalName());
//
//    }


}
