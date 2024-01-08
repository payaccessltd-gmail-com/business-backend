package com.jamub.payaccess.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.ObjectArrays;
import com.jamub.payaccess.api.dao.util.ObjectToUrlEncodedConverter;
import com.jamub.payaccess.api.dao.util.UtilityHelper;
import com.jamub.payaccess.api.enums.PaymentRequestType;
import com.jamub.payaccess.api.exception.ConfirmTransactionStatusException;
import com.jamub.payaccess.api.exception.ISWCardPaymentErrorHandler;
import com.jamub.payaccess.api.models.PaymentRequest;
import com.jamub.payaccess.api.models.Transaction;
import com.jamub.payaccess.api.models.response.*;
import com.jamub.payaccess.api.models.Merchant;
import com.jamub.payaccess.api.models.request.AuthenticateCardPaymentOtpRequest;
import com.jamub.payaccess.api.models.request.ISWAuthTokenRequest;
import com.jamub.payaccess.api.models.request.InitiateTransactionRequest;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

@Service
public class ISWService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${isw.passport.oauth.token.url}")
    private String oauthTokenEndpoint;
    @Value("${isw.card.purchase.api.url}")
    private String purchaseApiUrl;
    @Value("${isw.card.auth.otp.api.url}")
    private String authOTPApiUrl;

    @Value("${isw.confirm.transaction.status.url}")
    private String confirmTransactionStatusUrl;
    @Value("${isw.passport.oauth.clientId}")
    private String clientId;
    @Value("${isw.passport.oauth.secretKey}")
    private String secretKey;


    @Value("${isw.merchant.code}")
    private String iswMerchantClientId;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${isw.payaccess.payment.prefix}")
    private String payAccessPaymentPrefix;

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
                restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

                MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
                map.add("grant_type","client_credentials");
                HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

                ResponseEntity<ISWAuthTokenResponse> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, entity, ISWAuthTokenResponse.class);
                ISWAuthTokenResponse iswAuthTokenResponse = responseEntity.getBody();
                logger.info("iswAuthTokenResponse...{}", iswAuthTokenResponse.getAccess_token());
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

    public ISWCardPaymentResponse handleCardPurchase(ISWAuthTokenResponse iswAuthTokenResponse, InitiateTransactionRequest initiateTransactionRequest, Merchant merchant) throws Exception {
        try
        {


            String uri = UriComponentsBuilder
                    .fromUriString(purchaseApiUrl)
                    .build()
                    .toString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + iswAuthTokenResponse.getAccess_token());
            headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);


            ISWAuthTokenRequest iswAuthTokenRequest = new ISWAuthTokenRequest();
            HttpEntity<ISWAuthTokenRequest> validateOtpRequestEntity = new HttpEntity<>(iswAuthTokenRequest, headers);

            try
            {
                String authDataVersion = "1";
                String pan = initiateTransactionRequest.getCardDetails().getPan();        // Payment Card
                String expiryDate = initiateTransactionRequest.getCardDetails().getExpDate();                //Card Expiry date: April (04), 2020 (20) - YYMM
                String cvv2 = initiateTransactionRequest.getCardDetails().getCvv();                       // Card CVV2
                String pin = initiateTransactionRequest.getCardDetails().getPin();                       // Card pin
                String authData = UtilityHelper.getAuthData(authDataVersion, pan, pin, expiryDate, cvv2);
                ObjectMapper mapper = new ObjectMapper();



                ISWReq iswReq = new ISWReq();
                iswReq.setAmount(initiateTransactionRequest.getAmount().multiply(BigDecimal.valueOf(10L)).toString());
                iswReq.setCustomerId(initiateTransactionRequest.getCustomerId());
                iswReq.setTransactionRef(payAccessPaymentPrefix.concat(initiateTransactionRequest.getOrderRef()));
                iswReq.setCurrency(initiateTransactionRequest.getCurrencyCode());
                iswReq.setAuthData(authData);
                ObjectWriter ow = new ObjectMapper().writer();
                String strJson = ow.writeValueAsString(iswReq);

                HttpEntity<String> entity = new HttpEntity<>(strJson, headers);
                logger.info("entity.... {}", strJson);

                try {
                    restTemplate.setErrorHandler(new ISWCardPaymentErrorHandler());
                    ResponseEntity<ISWCardPaymentResponse> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, entity, ISWCardPaymentResponse.class);
                    ISWCardPaymentResponse iswCardPaymentResponse = responseEntity.getBody();
                    logger.info("iswCardPaymentResponse...{}", iswCardPaymentResponse);
                    return iswCardPaymentResponse;
                }
                catch(HttpClientErrorException e)
                {
                    logger.info("{}", e.getMessage());
                    return null;
                }



            }
            catch(HttpServerErrorException e)
            {
                return null;
            }
        }
        catch(RuntimeException e)
        {
            e.printStackTrace();
            throw new Exception("Failed to validate otp", e);
        }
    }

    public AuthOTPResponse authenticateCardPaymentOtp(ISWAuthTokenResponse iswAuthTokenResponse,
                                                      AuthenticateCardPaymentOtpRequest authenticateCardPaymentOtpRequest, Merchant merchant,
                                                      Transaction transaction) {
        String uri = UriComponentsBuilder
                .fromUriString(authOTPApiUrl)
                .build()
                .toString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + iswAuthTokenResponse.getAccess_token());
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);


        ISWAuthTokenRequest iswAuthTokenRequest = new ISWAuthTokenRequest();
        HttpEntity<ISWAuthTokenRequest> validateOtpRequestEntity = new HttpEntity<>(iswAuthTokenRequest, headers);

        try
        {
            String orderRef = transaction.getTransactionRef();
            String otp = authenticateCardPaymentOtpRequest.getOtp();
            ObjectMapper mapper = new ObjectMapper();



            AuthOTPReq authOTPReq = new AuthOTPReq();
            authOTPReq.setOtp(authenticateCardPaymentOtpRequest.getOtp());
            authOTPReq.setPaymentId(transaction.getTransactionRef());

            ObjectWriter ow = new ObjectMapper().writer();
            String strJson = ow.writeValueAsString(authOTPReq);

            HttpEntity<String> entity = new HttpEntity<>(strJson, headers);
            logger.info("entity.... {}", strJson);

            ResponseEntity<AuthOTPResponse> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, entity, AuthOTPResponse.class);

            AuthOTPResponse authOTPResponse = responseEntity.getBody();
            logger.info("authOTPResponse...{}", authOTPResponse);
            return authOTPResponse;

        }
        catch(HttpServerErrorException e)
        {
            return null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public ConfirmTransactionStatusResponse confirmTransactionStatus(
            Transaction transaction,
             ISWAuthTokenResponse iswAuthTokenResponse,
             PaymentRequestService paymentRequestService) throws ConfirmTransactionStatusException, JsonProcessingException {
        String orderRef = payAccessPaymentPrefix.concat(transaction.getOrderRef());
        String payAccessMerchantCode = transaction.getMerchantCode();
        String switchAuthToken = iswAuthTokenResponse.getAccess_token();

        String uri = UriComponentsBuilder
                .fromUriString(confirmTransactionStatusUrl)
                .queryParam("transactionReference", orderRef)
                .queryParam("merchantCode", iswMerchantClientId)
                .build()
                .toString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + switchAuthToken);
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        logger.info("switchAuthToken .. {}", uri);


//        ISWAuthTokenRequest iswAuthTokenRequest = new ISWAuthTokenRequest();
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        try
        {

            PaymentRequest paymentRequest = paymentRequestService.createPaymentRequest(payAccessMerchantCode,
                    transaction.getTerminalCode(),
                    transaction.getOrderRef(),
                    PaymentRequestType.CONFIRM_TRANSACTION_STATUS,
                    (new ObjectMapper()).writeValueAsString(authOTPApiUrl.toString())
            );
            paymentRequest = paymentRequestService.updatePaymentRequest(paymentRequest);


            ResponseEntity responseEntity = restTemplate
                    //.getForEntity(uri, ConfirmTransactionStatusResponse.class);
                    .exchange(uri, HttpMethod.GET, entity, ConfirmTransactionStatusResponse.class);


            HttpStatus responseStatus = responseEntity.getStatusCode();
            logger.info("responseStatus ... {}", responseStatus);
            logger.info("responseStatus ... {}", responseStatus.value());
            if(responseStatus.equals(HttpStatus.OK)) {
                ConfirmTransactionStatusResponse confirmTransactionStatusResponse = (ConfirmTransactionStatusResponse)responseEntity.getBody();
                logger.info("{}", new ObjectMapper().writeValueAsString(confirmTransactionStatusResponse));
                logger.info("confirmTransactionStatusResponse...{}", confirmTransactionStatusResponse);

                paymentRequest.setResponseBody(new ObjectMapper().writeValueAsString(confirmTransactionStatusResponse));
                paymentRequest = paymentRequestService.updatePaymentRequest(paymentRequest);

                return confirmTransactionStatusResponse;
            }
            else
            {

                ConfirmTransactionStatusErrorResponse responseBody = (ConfirmTransactionStatusErrorResponse)responseEntity.getBody();
                logger.info("....{}", responseBody.toString());
                paymentRequest.setResponseBody(new ObjectMapper().writeValueAsString(responseBody));
//                paymentRequest = paymentRequestService.updatePaymentRequest(paymentRequest);

                DecimalFormat df = new DecimalFormat("#,##0.00");
                String errorMessage = "Transaction Ref #".concat(transaction.getTransactionRef()).concat(" for the amount of ").
                        concat(transaction.getPayAccessCurrency().name()).concat(df.format(transaction.getAmount())).
                        concat(" is pending confirmation. Your payment card/account may have been debited");
                throw new ConfirmTransactionStatusException(responseBody.getDescription());
            }

        }
        catch(HttpServerErrorException e)
        {
            return null;
        } catch (HttpClientErrorException e) {
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setResponseBody(e.getMessage());
            //paymentRequest = paymentRequestService.updatePaymentRequest(paymentRequest);

            ConfirmTransactionStatusErrorResponse errorResponse = new ObjectMapper().readValue(e.getResponseBodyAsString(), ConfirmTransactionStatusErrorResponse.class);

            DecimalFormat df = new DecimalFormat("#,##0.00");
            String errorMessage = "Transaction Ref #".concat(transaction.getTransactionRef()).concat(" for the amount of ").
                    concat(transaction.getPayAccessCurrency().name()).concat(df.format(transaction.getAmount())).
                    concat(" is pending confirmation. Your payment card/account may have been debited");
            throw new ConfirmTransactionStatusException(errorResponse.getDescription());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
//PA-AAB78318
// https://qa.interswitchng.com/paymentgateway/api/v1/transactions?merchantCode=MX159525&pageNum=0&pageSize=10&startDate=01/01/2024&endDate=01/03/2024&transactionReference=PA-AAB78318


//    https://qa.interswitchng.com/paymentgateway/api/v1/virtualaccounts/transaction?transactionReference=PA-474583974&merchantCode=MX159525
//    https://qa.interswitchng.com/paymentgateway/api/v1/virtualaccounts/transaction?merchantCode=MX159525&transactionReference=PA-AAB78318
    @Getter
    @Setter
    class ISWReq implements Serializable {
        private String customerId;
        private String amount;
        private String transactionRef;
        private String currency;
        private String authData;


        @Override
        public String toString()
        {
            return "{\"amount\":\""+amount+"\"," +
                        "\"authData\":\""+ authData +"\",\"customerId\":\""+customerId+"\"," +
                        "\"transactionRef\":\""+"PA-"+transactionRef+"\"," +
                        "\"currency\":\""+currency+"\"}";
        }
    }

    @Getter
    @Setter
    class AuthOTPReq implements Serializable {
        private String paymentId;
        private String otp;


        @Override
        public String toString()
        {
            return "{\"paymentId\":\""+paymentId+"\"," +
                    "\"otp\":\""+ otp+"\"}";
        }
    }



    @Getter
    @Setter
    class AuthReq implements Serializable{
        private String grant_type;


        @Override
        public String toString()
        {
            return "{\"grant_type\":\""+grant_type+"\"}";
        }
    }
}
