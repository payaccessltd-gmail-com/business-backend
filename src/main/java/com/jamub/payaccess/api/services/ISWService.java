package com.jamub.payaccess.api.services;

import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.request.ISWAuthTokenRequest;
import com.jamub.payaccess.api.models.request.ValidateOtpRequest;
import com.jamub.payaccess.api.models.response.ISWAuthTokenResponse;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.models.response.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Service
public class ISWService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${isw.passport.oauth.token.url}")
    private String oauthTokenEndpoint;
    @Value("${isw.passport.oauth.clientId}")
    private String clientId;
    @Value("${isw.passport.oauth.secretKey}")
    private String secretKey;

    public ISWAuthTokenResponse getToken() throws Exception {
        try
        {
            String base64ClientIdSecretkey = clientId + ":" + secretKey;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] base64ClientIdSecretkeyByte = digest.digest(base64ClientIdSecretkey.getBytes(StandardCharsets.UTF_8));
            base64ClientIdSecretkey = Base64.getEncoder().encodeToString(base64ClientIdSecretkeyByte);

            String uri = UriComponentsBuilder
                    .fromUriString(oauthTokenEndpoint)
                    .build()
                    .toString();

            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Basic " + base64ClientIdSecretkey);
            headers.set("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);

            ISWAuthTokenRequest iswAuthTokenRequest = new ISWAuthTokenRequest();
            HttpEntity<ISWAuthTokenRequest> validateOtpRequestEntity = new HttpEntity<>(iswAuthTokenRequest, headers);

            try
            {

                ResponseEntity<ISWAuthTokenResponse> responseEntity = restTemplate.postForObject(uri, validateOtpRequestEntity, ResponseEntity.class);
                ISWAuthTokenResponse iswAuthTokenResponse = responseEntity.getBody();
                HttpStatus httpStatus = responseEntity.getStatusCode();
                return iswAuthTokenResponse;

            }
            catch(HttpServerErrorException e)
            {
                return null;
            }
        }
        catch(RuntimeException e)
        {
            throw new Exception("Failed to validate otp", e);
        }

    }
}
