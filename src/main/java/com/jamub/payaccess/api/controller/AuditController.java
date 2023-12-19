package com.jamub.payaccess.api.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.dao.util.UtilityHelper;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.Merchant;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.AuditTrailFilterRequest;
import com.jamub.payaccess.api.models.request.GetMerchantFilterRequest;
import com.jamub.payaccess.api.models.request.MerchantBusinessDataUpdateRequest;
import com.jamub.payaccess.api.models.request.MerchantBusinessInformationUpdateRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.AuditTrailService;
import com.jamub.payaccess.api.services.MerchantService;
import com.jamub.payaccess.api.services.TokenService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/audit")
public class AuditController {



    @Autowired
    TokenService tokenService;

    @Autowired
    AuditTrailService auditTrailService;
    @CrossOrigin
    @RequestMapping(value = "/get-audit-trails/{pageNumber}/{pageSize}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Get List of Audit Trails", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getAuditTrails(
            @PathVariable(required = true) Integer pageNumber,
            @PathVariable(required = false) Integer pageSize,
            AuditTrailFilterRequest auditTrailFilterRequest,
            HttpServletRequest request,
            HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {

            return auditTrailService.getAuditTrails(pageNumber, pageSize, auditTrailFilterRequest);

//            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);


    }
}
