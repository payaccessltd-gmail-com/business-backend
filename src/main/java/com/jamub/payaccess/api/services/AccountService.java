package com.jamub.payaccess.api.services;

import com.jamub.payaccess.api.dao.AccountDao;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.AccountPackage;
import com.jamub.payaccess.api.models.Account;
import com.jamub.payaccess.api.models.Customer;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.ValidateAccountRequest;
import com.jamub.payaccess.api.models.request.ValidateOtpRequest;
import com.jamub.payaccess.api.models.response.AccountBalanceResponse;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.models.response.TokenResponse;
import com.jamub.payaccess.api.models.response.ValidateAccountResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;



@Service
public class AccountService {


    @Autowired
    private AccountDao accountDao;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${is.live}")
    private int isLive;




    @Autowired
    public AccountService(AccountDao accountDao){
        this.accountDao = accountDao;
    }

    public Account createNewCustomerWallet(String customerName, Customer customer, String cbnBankCode, String accountPackageCode, String pin) throws NoSuchAlgorithmException {

        Account account = new Account();
        AccountPackage accountPackage = this.getAccountPackageByPackageCode(accountPackageCode);
        String identifier1 = cbnBankCode + (accountPackage.getId()) + "" +
                RandomStringUtils.randomNumeric(accountPackage.getId()<10 ? 6 : 5);
        return this.accountDao.createNewCustomerWallet(customerName, customer, identifier1, accountPackage.getPayAccessCurrency().name(), accountPackage.getId(), isLive, pin);

    }


    public AccountPackage getAccountPackageByPackageCode(String packageCode)
    {
        return this.accountDao.getAccountPackageByPackageCode(packageCode);
    }

    public AccountBalanceResponse getAccountBalance(Long accountId)
    {
        Account account = null;
        Optional<Account> accountOptional = this.accountDao.get(accountId);
        if(accountOptional.isPresent())
            account = accountOptional.get();
        else
            return null;

        AccountBalanceResponse accountBalanceResponse = new AccountBalanceResponse(account.getAccountBalance(), account.getFloatingBalance());
        return accountBalanceResponse;
    }

    public PayAccessResponse validateAccountRecipient(RestTemplate restTemplate, ValidateAccountRequest validateAccountRequest, String endpointUrl,
                                                      String authorizationString, String signature, String clientId1, String secretKey1,
                                                      String merchantClientId, String merchantSecretKey) throws UnsupportedEncodingException, NoSuchAlgorithmException {



        String uri = UriComponentsBuilder
                .fromUriString(endpointUrl)
//                .path(String.format("%s%s%s", "/api/v1/inquiry/bank-code", "/" + validateAccountRequest.getBankCode(), "/account/"+validateAccountRequest.getAccountNumber()))
                .path("/api/v1/nameenquiry/banks/accounts/names")
                .build()
                .toString();
        log.info("url....{}", uri);

        String nonce = RandomStringUtils.randomAlphanumeric(30);
        nonce = "aa0e516a32daf5be424c4b1c1d43e6c3";
        log.info("nonce....{}", nonce);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String currentTimeStamp = sdf.format(new Date());
        Long currentTimeStamp = (new Date().getTime());
        currentTimeStamp = Instant.now().getEpochSecond();
        currentTimeStamp = 1692355402L;
        log.info("currentTimeStamp....{}", currentTimeStamp);

        signature = "GET&" +
                URLEncoder.encode(uri, StandardCharsets.UTF_8.name()) + "&" + currentTimeStamp + "&" + nonce +
                "&" + merchantClientId + "&" + merchantSecretKey;

        log.info("Signature....{}", signature);
        log.info("currentTimeStamp....{}", currentTimeStamp);
        log.info("clientId....{}", merchantClientId);
        log.info("secretKey....{}", merchantSecretKey);
//        signature = Base64.getEncoder().encodeToString(signature.getBytes());

        signature = new String(
                Base64.getEncoder().encode(MessageDigest.getInstance("SHA-512").digest(signature.getBytes(StandardCharsets.UTF_8))));
        log.info("Signature....{}", signature);
        log.info("Authorization.... Basic {}", authorizationString);

        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", "InterswitchAuth SUtJQTgzQkREMEI2NTlFMzUzQTI4OUQ1QUQ1QUQ5NzkzNjYwOERENzUwNzI=");
        headers.set("Signature", signature);
        headers.set("Timestamp", currentTimeStamp.toString());
        headers.set("Nonce", nonce);
        headers.set("SignatureMethod", "SHA512");
        headers.set("TerminalID", validateAccountRequest.getTerminalId());
        headers.set("bankCode", validateAccountRequest.getBankCode());
        headers.set("accountId", validateAccountRequest.getAccountNumber());


        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ValidateOtpRequest> validateOtpRequestEntity = new HttpEntity<>(headers);
        try
        {

            ResponseEntity<ValidateAccountResponse> responseEntity = restTemplate.getForObject(uri, ResponseEntity.class);
            ValidateAccountResponse validateAccountResponse = responseEntity.getBody();
            HttpStatus httpStatus = responseEntity.getStatusCode();
            PayAccessResponse payAccessResponse = new  PayAccessResponse();


            if(validateAccountResponse.getAccountName()!=null)
            {
                payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
                payAccessResponse.setMessage("Validation Successful");
                payAccessResponse.setResponseObject(validateAccountResponse);
                return payAccessResponse;
            }

            payAccessResponse.setStatusCode(PayAccessStatusCode.VALIDATION_FAILED.label);
            payAccessResponse.setMessage("Validation Failed");
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
    }


    public PayAccessResponse sendFundsToBankAccount()
    {
        return null;
    }
}
