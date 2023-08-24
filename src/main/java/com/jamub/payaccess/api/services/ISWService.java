package com.jamub.payaccess.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamub.payaccess.api.dao.util.ObjectToUrlEncodedConverter;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.request.ISWAuthTokenRequest;
import com.jamub.payaccess.api.models.request.ValidateOtpRequest;
import com.jamub.payaccess.api.models.response.ISWAuthTokenResponse;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.models.response.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public ISWAuthTokenResponse getToken() throws Exception {
        try
        {
            String base64ClientIdSecretkey = clientId + ":" + secretKey;
            logger.info("{} base64clientId", base64ClientIdSecretkey);
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            byte[] base64ClientIdSecretkeyByte = digest.digest(base64ClientIdSecretkey.getBytes(StandardCharsets.UTF_8));
            base64ClientIdSecretkey = Base64.getEncoder().encodeToString(base64ClientIdSecretkey.getBytes());

            String uri = UriComponentsBuilder
                    .fromUriString(oauthTokenEndpoint)
                    .build()
                    .toString();

            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            logger.info("{} base64clientId", base64ClientIdSecretkey);
            headers.set("Authorization", "Basic " + base64ClientIdSecretkey);
            headers.set("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);

            ISWAuthTokenRequest iswAuthTokenRequest = new ISWAuthTokenRequest();
            HttpEntity<ISWAuthTokenRequest> validateOtpRequestEntity = new HttpEntity<>(iswAuthTokenRequest, headers);

            try
            {

                ObjectMapper mapper = new ObjectMapper();
                restTemplate.getMessageConverters().add(new ObjectToUrlEncodedConverter(mapper));
//                restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

                MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
                map.add("grant_type","client_credentials");
                HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

                ResponseEntity<ISWAuthTokenResponse> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, entity, ISWAuthTokenResponse.class);
                ISWAuthTokenResponse iswAuthTokenResponse = responseEntity.getBody();
                logger.info("iswAuthTokenResponse...{}", iswAuthTokenResponse);
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
