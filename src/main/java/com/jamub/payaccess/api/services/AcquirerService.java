package com.jamub.payaccess.api.services;

import com.jamub.payaccess.api.dao.AccountDao;
import com.jamub.payaccess.api.dao.AcquirerDao;
import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.*;
import com.jamub.payaccess.api.models.request.ValidateAccountRequest;
import com.jamub.payaccess.api.models.request.ValidateOtpRequest;
import com.jamub.payaccess.api.models.response.AccountBalanceResponse;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
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
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;


@Service
public class AcquirerService {


    @Autowired
    private AcquirerDao acquirerDao;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${is.live}")
    private int isLive;




    @Autowired
    public AcquirerService(AcquirerDao acquirerDao){
        this.acquirerDao = acquirerDao;
    }


    public ResponseEntity createNewAcquirer(String acquirerCode, String acquirerName, Bank bank, User authenticatedUser, Boolean isBank,
                                            Long actorId, String ipAddress, String description,
                                            ApplicationAction userAction, String carriedOutByUserFullName, String objectClassReference,
                                            Long objectIdReference) {

        Acquirer existingAcquirer = this.acquirerDao.getAcquirerByAcquirerCode(acquirerCode);
        if(existingAcquirer!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Acquirer matching the acquirer code already exists");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }

        existingAcquirer = this.acquirerDao.getAcquirerByAcquirerName(acquirerName);
        if(existingAcquirer!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Acquirer matching the acquirer name already exists");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }

        Acquirer acquirer = this.acquirerDao.createNewAcquirer(acquirerName, acquirerCode, bank, isBank,
                authenticatedUser,  actorId, ipAddress, description,
                userAction, carriedOutByUserFullName, objectClassReference,
                objectIdReference);

        if(acquirer!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setResponseObject(acquirer);
            payAccessResponse.setMessage("Acquirer creation was successful");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }


        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Acquirer creation was not successful");
        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
    }






    public ResponseEntity getAcquirers(User authenticatedUser, Integer pageNumber, Integer pageSize) {

        Map acquirersList = this.acquirerDao.getAcquirersByPage(pageNumber, pageSize);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
        payAccessResponse.setMessage("Acquirer listing");
        payAccessResponse.setResponseObject(acquirersList);
        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
    }
}
