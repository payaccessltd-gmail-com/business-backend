package com.jamub.payaccess.api.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.dao.util.UtilityHelper;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.enums.Permission;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.ForgotPasswordRequest;
import com.jamub.payaccess.api.models.request.MerchantUserBioDataUpdateRequest;
import com.jamub.payaccess.api.models.request.UpdateForgotPasswordRequest;
import com.jamub.payaccess.api.models.request.ValidateOtpRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.models.response.TokenResponse;
import com.jamub.payaccess.api.services.TokenService;
import com.jamub.payaccess.api.services.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    @Autowired
    TokenService tokenService;

    @Autowired
    UserService userService;

    @Value("${token.end.point.url}")
    String tokenEndpointUrl;

    @Autowired
    private RestTemplate restTemplate;


    @CrossOrigin
    @RequestMapping(value = "/otp-validate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse validateOtp(@RequestBody ValidateOtpRequest validateOtpRequest,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) throws Exception {

        JWTClaimsSet jwtClaimsSet = tokenService.getClaimsFromToken(request);
        if(jwtClaimsSet==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization failed");
            return payAccessResponse;
        }

        List<String> permissionList = (List<String>)jwtClaimsSet.getClaim("permissions");
        String key = (String)jwtClaimsSet.getClaim("key");

        if(!permissionList.contains(Permission.AUTHENTICATE_WITH_OTP.name()))
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization failed");
            return payAccessResponse;
        }

        try {
            String uri = UriComponentsBuilder
                    .fromUriString(tokenEndpointUrl)
                    .path(String.format("%s", "api/validate-otp"))
                    .build()
                    .toString();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
//            restTemplate.set
            validateOtpRequest.setKey(key);
            HttpEntity<ValidateOtpRequest> validateOtpRequestEntity = new HttpEntity<>(validateOtpRequest, headers);
            try
            {

                ResponseEntity<TokenResponse> responseEntity = restTemplate.postForObject(uri, validateOtpRequestEntity, ResponseEntity.class);
                TokenResponse tokenResponse = responseEntity.getBody();
                HttpStatus httpStatus = responseEntity.getStatusCode();
                PayAccessResponse payAccessResponse = new  PayAccessResponse();


                if(tokenResponse.getResponseCode().equals("00"))
                {
                    payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
                    payAccessResponse.setMessage("Authorization Success");
                    payAccessResponse.setResponseObject(tokenResponse.getMessage());
                    return payAccessResponse;
                }

                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization Failed");
                payAccessResponse.setResponseObject(null);
                return payAccessResponse;

            }
            catch(HttpServerErrorException e)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Authorization Failed");
                payAccessResponse.setResponseObject(null);
                return payAccessResponse;
            }



        } catch (RuntimeException e) {
            throw new Exception("Failed to validate otp", e);
        }
    }
}
