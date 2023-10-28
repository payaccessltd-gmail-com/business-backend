package com.jamub.payaccess.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamub.payaccess.api.dao.TransactionDao;
import com.jamub.payaccess.api.dao.UserDao;
import com.jamub.payaccess.api.dao.util.UtilityHelper;
import com.jamub.payaccess.api.enums.APIMode;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.enums.TransactionStatus;
import com.jamub.payaccess.api.models.Terminal;
import com.jamub.payaccess.api.models.response.AuthOTPResponse;
import com.jamub.payaccess.api.models.response.ISWCardPaymentResponse;
import com.jamub.payaccess.api.models.Merchant;
import com.jamub.payaccess.api.models.Transaction;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.ISWAuthTokenResponse;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

@Service
public class TransactionService {

    private TransactionDao transactionDao;
    private UserDao userDao;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public TransactionService(TransactionDao transactionDao, UserDao userDao){

        this.transactionDao = transactionDao;
        this.userDao = userDao;
    }

    public PayAccessResponse getTransactions(TransactionFilterRequest transactionFilterRequest){

        List<Transaction> allTransactions = transactionDao.getAll(transactionFilterRequest);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setResponseObject(allTransactions);
        payAccessResponse.setMessage("Transactions");
        payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);

        return payAccessResponse;
    }

    public PayAccessResponse debitCard(ISWService iswService, Merchant merchant, Terminal terminal, InitiateTransactionRequest initiateTransactionRequest,
                                       String authorizationToken, String deviceAuthorizationToken,
                                       ISWAuthTokenResponse iswAuthTokenResponse) {

        String merchantCode = merchant.getMerchantCode();
        String merchantSecretKey = merchant.getApiMode().equals(APIMode.TEST) ? merchant.getSecretKey() : merchant.getSecretKeyLive();

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        String toHash = merchantCode.concat(":").concat(merchantSecretKey);
        Transaction transaction = null;
        try {
            String transactionValidHash = UtilityHelper.get_SHA_512_SecurePassword(toHash, salt);

//            if(!transactionValidHash.equals(authorizationToken))
//            {
//                PayAccessResponse payAccessResponse = new PayAccessResponse();
//                payAccessResponse.setMessage("Hash Security compare failed");
//                payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
//
//                return payAccessResponse;
//            }

//            if(initiateTransactionRequest.getChannel().equals(Channel.POS))
//            {
//                if(!transactionValidHash.equals(authorizationToken))
//                {
//                    PayAccessResponse payAccessResponse = new PayAccessResponse();
//                    payAccessResponse.setMessage("Hash Security compare failed");
//                    payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
//
//                    return payAccessResponse;
//                }
//            }

            JSONObject jsonObjectReq = new JSONObject();
            ObjectMapper objectMapper = new ObjectMapper();
            String messageRequest = null;
            try {
                jsonObjectReq.put("initializeTransactionReq", objectMapper.writeValueAsString(initiateTransactionRequest));
                messageRequest = objectMapper.writeValueAsString(jsonObjectReq);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            transaction = transactionDao.createNewTransaction(initiateTransactionRequest, merchant, terminal, messageRequest);

            if(transaction==null)
            {
                PayAccessResponse payAccessResponse = new PayAccessResponse();
                payAccessResponse.setMessage("Transaction could not be initiated. Please try again");
                payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            }

            ISWCardPaymentResponse iswCardPaymentResponse = iswService.handleCardPurchase(iswAuthTokenResponse, initiateTransactionRequest, merchant);
            String messageResponse = null;
            JSONObject jsonObjectRes = new JSONObject();
            try {
                jsonObjectReq.put("initializeTransactionRes", objectMapper.writeValueAsString(iswCardPaymentResponse));
                messageRequest = objectMapper.writeValueAsString(jsonObjectReq);
                transaction.setMessageResponse(messageResponse);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            if(iswCardPaymentResponse.getResponseCode().equalsIgnoreCase("T0")) {

                transaction.setTransactionStatus(TransactionStatus.AWAITING_OTP_VALIDATION);
                transaction.setTransactionRef(iswCardPaymentResponse.getPaymentId());
                transaction = transactionDao.updateTransaction(transaction);
                PayAccessResponse payAccessResponse = new PayAccessResponse();
                payAccessResponse.setResponseObject(iswCardPaymentResponse);
                payAccessResponse.setMessage(iswCardPaymentResponse.getMessage().concat(". ").concat(iswCardPaymentResponse.getPlainTextSupportMessage()));
                payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);

                return payAccessResponse;
            }



            transaction.setTransactionStatus(TransactionStatus.FAIL);
            transaction = transactionDao.updateTransaction(transaction);
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setResponseObject(iswCardPaymentResponse);
            payAccessResponse.setMessage("Error experienced initiating transaction");
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);

            return payAccessResponse;


        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash Security compare failed");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public String getAuthorization(HttpServletRequest request, String key1) {
        String token = null;
        Enumeration<String> headers = request.getHeaderNames();
        while(headers.hasMoreElements()) {
            String key = headers.nextElement();
            if(key.trim().equalsIgnoreCase(key)) {
                String authorizationHeader = request.getHeader(key1);
                if(!authorizationHeader.isEmpty()) {
                    String[] tokenData = authorizationHeader.split(" ");
                    if(tokenData.length == 2 && tokenData[0].trim().equalsIgnoreCase("Basic")) {
                        token = tokenData[1];
                        logger.info("Received token: " + token);
                        break;
                    }
                }
            }
        }
        return token;
    }

    public PayAccessResponse authenticateCardPaymentOtp(ISWService iswService, Merchant merchant,
                            AuthenticateCardPaymentOtpRequest authenticateCardPaymentOtpRequest, String authorizationToken,
                            String deviceAuthorizationToken, ISWAuthTokenResponse iswAuthTokenResponse, Transaction transaction) {
        String merchantCode = merchant.getMerchantCode();
        String merchantSecretKey = merchant.getApiMode().equals(APIMode.TEST) ? merchant.getSecretKey() : merchant.getSecretKeyLive();

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        String toHash = merchantCode.concat(":").concat(merchantSecretKey);
        try {
            String transactionValidHash = UtilityHelper.get_SHA_512_SecurePassword(toHash, salt);


            AuthOTPResponse authOTPResponse = iswService.authenticateCardPaymentOtp(iswAuthTokenResponse, authenticateCardPaymentOtpRequest, merchant, transaction);
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            if(authOTPResponse!=null && authOTPResponse.getResponseCode().equals("00"))
            {
                transaction.setSwitchTransactionRef(authOTPResponse.getTransactionIdentifier());
                transaction.setTransactionStatus(TransactionStatus.SUCCESS);
                transaction = transactionDao.updateTransaction(transaction);


                payAccessResponse.setResponseObject(transaction);
                payAccessResponse.setMessage("Payment was successful");
                payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            }
            else
            {
                String messageResponse = transaction.getMessageResponse();
                JSONParser jsonParser = new JSONParser();
                JSONObject messageResponseJSON = (JSONObject) jsonParser.parse(messageResponse);

                messageResponseJSON.put("authenticateOTPResp", authOTPResponse);
                transaction.setMessageResponse(messageResponseJSON.toJSONString());
                transaction.setSwitchTransactionRef(authOTPResponse.getTransactionIdentifier());
                transaction.setTransactionStatus(TransactionStatus.SUCCESS);
                transaction = transactionDao.updateTransaction(transaction);

                payAccessResponse.setResponseObject(authOTPResponse);
                payAccessResponse.setMessage("Payment was not successful. Reason: ".concat(payAccessResponse.getMessage()));
                payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            }


            return payAccessResponse;


        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash Security compare failed");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Transaction getTransactionByOrderRef(String orderRef, String merchantCode) {
        Transaction transaction = transactionDao.getTransactionByOrderRef(orderRef, merchantCode);
        return transaction;
    }
}
