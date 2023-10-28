package com.jamub.payaccess.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamub.payaccess.api.dao.MerchantDao;
import com.jamub.payaccess.api.dao.UserDao;
import com.jamub.payaccess.api.dto.MerchantDTO;
import com.jamub.payaccess.api.dto.UserDTO;
import com.jamub.payaccess.api.enums.APIMode;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.enums.Urgency;
import com.jamub.payaccess.api.models.Merchant;
import com.jamub.payaccess.api.models.MerchantCredential;
import com.jamub.payaccess.api.models.MerchantSetting;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.sun.mail.smtp.SMTPTransport;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.mail.Authenticator;
import javax.mail.Message;
//import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.*;

@Service
public class MerchantService {

    private MerchantDao merchantDao;
    private UserDao userDao;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public MerchantService(MerchantDao merchantDao, UserDao userDao){

        this.merchantDao = merchantDao;
        this.userDao = userDao;
    }

    public List<Merchant> getAllMerchants(){
        return merchantDao.getAll();
    }


    public Merchant getMerchantById(Long merchantId){

        Optional<Merchant> merchantOptional =  merchantDao.get(merchantId);
        if(merchantOptional.isPresent())
        {
            Merchant merchant = merchantOptional.get();
            return merchant;
        }

        return null;
    }

//    public PayAccessResponse createNewMerchant(MerchantSignUpRequest merchantSignUpRequest) {
//        System.out.println(merchantSignUpRequest.getEmailAddress());
//        List<User> existingMerchantUsers = userDao.getUserByEmailAddress(merchantSignUpRequest.getEmailAddress());
//        logger.info("{}", existingMerchantUsers);
//        if(existingMerchantUsers!=null && !existingMerchantUsers.isEmpty())
//        {
//            PayAccessResponse payAccessResponse = new PayAccessResponse();
//            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
//            payAccessResponse.setMessage("Merchant sign up was not successful. A merchant with the email address already is already signed up");
//            return payAccessResponse;
//        }
//
//        merchantSignUpRequest.setVerificationLink(RandomStringUtils.randomAlphanumeric(128));
//        Merchant merchant = merchantDao.save(merchantSignUpRequest);
//        if(merchant!=null)
//        {
//            PayAccessResponse payAccessResponse = new PayAccessResponse();
//            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
//            payAccessResponse.setMessage("A link has been sent to your email address '"+merchantSignUpRequest.getEmailAddress().toLowerCase()+"'. Please click on the link " +
//                    "in the email to verify your merchant account");
//            payAccessResponse.setResponseObject(merchantSignUpRequest.getVerificationLink());
//
//
//            try {
//                logger.info("=========================");
//                Properties props = System.getProperties();
//                props.put("mail.smtps.host", "smtp.mailgun.org");
//                props.put("mail.smtps.auth", "true");
//
//                Session session = Session.getInstance(props, null);
//                Message msg = new MimeMessage(session);
//                msg.setFrom(new InternetAddress("test@mails.valuenaira.com"));
//
//                InternetAddress[] addrs = InternetAddress.parse(merchantSignUpRequest.getEmailAddress(), false);
//                msg.setRecipients(Message.RecipientType.TO, addrs);
//
//                msg.setSubject("Hello");
//                msg.setText("Copy the url and paste in your browser to activate your account - http://137.184.47.182:8081/payaccess/api/v1/merchant/activate-account/"+merchantSignUpRequest.getEmailAddress()+"/" + merchantSignUpRequest.getVerificationLink());
//
//                msg.setSentDate(new Date());
//
//                SMTPTransport t =
//                        (SMTPTransport) session.getTransport("smtps");
//                t.connect("smtp.mailgun.org", "postmaster@mails.valuenaira.com", "k0l01qaz!QAZ");
//                t.sendMessage(msg, msg.getAllRecipients());
//
//                logger.info("Response: {}" , t.getLastServerResponse());
//
//                t.close();
//            }
//            catch(Exception e)
//            {
//                e.printStackTrace();
//                logger.error("Error Sending Mail ...{}", e);
//            }
//
//
//            return payAccessResponse;
//        }
//
//        PayAccessResponse payAccessResponse = new PayAccessResponse();
//        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
//        payAccessResponse.setMessage("Merchant sign up was not successful. Please try again");
//        HashMap<String, String> responseObject = new HashMap<>();
//        responseObject.put("verificationLink", merchantSignUpRequest.getVerificationLink());
//        payAccessResponse.setResponseObject(responseObject);
//
//        logger.info("{}", payAccessResponse);
//
//
//
//
//        return payAccessResponse;
//    }

    public PayAccessResponse activateAccount(String emailAddress, String verificationLink) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        Map<String, Object> m = merchantDao.activateAccount(emailAddress, verificationLink);

        List<Merchant> merchantList = (List<Merchant>) m.get("#result-set-1");
        List<User> userList = (List<User>) m.get("#result-set-2");

        Merchant merchant = merchantList.get(0);
        User user = userList.get(0);

        if(merchant==null)
        {
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Merchant profile activation was not successful. Please try again");
            return payAccessResponse;
        }
        payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
        payAccessResponse.setMessage("Merchant profile has been activated successfully");
        String merchantToString = objectMapper.writeValueAsString(merchant);
        String userToString = objectMapper.writeValueAsString(user);
        MerchantDTO merchantDto = objectMapper.readValue(merchantToString, MerchantDTO.class);
        UserDTO userDto = objectMapper.readValue(userToString, UserDTO.class);
        ArrayList arrayList = new ArrayList();
        arrayList.add(merchantDto);
        arrayList.add(userDto);
        payAccessResponse.setResponseObject(arrayList);
        return payAccessResponse;
    }

    public PayAccessResponse updateMerchantBioData(MerchantUserBioDataUpdateRequest merchantUserBioDataUpdateRequest, User authenticatedUser) {

        Merchant merchant = merchantDao.updateMerchantBioData(merchantUserBioDataUpdateRequest, authenticatedUser);
        if(merchant!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Merchant Bio-Data updated successfully");
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Merchant Bio-Data update was not successful. Please try again");
        return payAccessResponse;
    }

    public PayAccessResponse updateMerchantBusinessData(MerchantBusinessDataUpdateRequest merchantBusinessDataUpdateRequest, Long merchantId, User authenticatedUser) {
        Merchant merchant = merchantDao.updateMerchantBusinessData(merchantBusinessDataUpdateRequest, merchantId, authenticatedUser);
        if(merchant!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setResponseObject(merchant);
            payAccessResponse.setMessage("Merchant Business data updated successfully");
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Merchant Business data update was not successful. Please try again");
        return payAccessResponse;
    }

    public PayAccessResponse updateMerchantBusinessBankAccountData(MerchantBusinessBankAccountDataUpdateRequest merchantBusinessBankAccountDataUpdateRequest,
                                                                   User authenticatedUser) {
        Merchant merchant = merchantDao.updateMerchantBusinessBankAccountData(merchantBusinessBankAccountDataUpdateRequest, authenticatedUser);
        if(merchant!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Merchant bank account details updated successfully");
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Merchant  bank account details update was not successful. Please try again");
        return payAccessResponse;
    }


    public List<?> getMerchantDetails(String merchantCode)
    {
        List<?> queryResponse = merchantDao.getMerchantByMerchantCode(merchantCode);
        return queryResponse;
//        PayAccessResponse payAccessResponse = new PayAccessResponse();
//        payAccessResponse.setResponseObject(queryResponse);
//        payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
//        payAccessResponse.setMessage("Merchant details fetched successfully");
//        return payAccessResponse;
//        return null;
    }


    public PayAccessResponse approveMerchant(String merchantCode)
    {
        Merchant queryResponse = merchantDao.approveMerchant(merchantCode);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setResponseObject(queryResponse);
        if(queryResponse!=null)
        {
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Merchant approved successfully");
            return payAccessResponse;
        }
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("Merchant approval failed. Invalid merchant code provided");
        return payAccessResponse;
//        return null;
    }

    public PayAccessResponse getMerchants(Integer pageNumber, Integer pageSize) {
        if(pageNumber==null)
            pageNumber = 0;

        List<Merchant> queryResponse = merchantDao.getMerchants(pageNumber, pageSize);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setResponseObject(queryResponse);
        if(queryResponse!=null)
        {
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Merchants fetched successfully");
            return payAccessResponse;
        }
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("Merchant fetch failed");
        return payAccessResponse;

    }

    public PayAccessResponse updateMerchantAboutBusiness(MerchantSignUpRequest merchantSignUpRequest,
                                                         User authenticatedUser) {
        Merchant merchant = merchantDao.updateMerchantAboutBusiness(merchantSignUpRequest, authenticatedUser);
        if(merchant!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Merchant's details about their business updated successfully");
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Merchant's details about their business was not updated successful. Please try again");
        return payAccessResponse;
    }


    public PayAccessResponse updateMerchantTransactionFeePayer(Boolean merchantMustPayTransactionFee, Long merchantId, User authenticatedUser) {
        MerchantSetting merchantSetting = merchantDao.updateMerchantTransactionFeePayer(merchantMustPayTransactionFee, merchantId, authenticatedUser);
        if(merchantSetting!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setResponseObject(merchantSetting);
            payAccessResponse.setMessage("Transaction fee payer has been updated successfully");
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Transaction fee payer could not be updated successfully. Please try again");
        return payAccessResponse;
    }

    public PayAccessResponse updateMerchantReceiveEarnings(String receiveEarningsOption, Long merchantId, User authenticatedUser) {
        Merchant merchant = merchantDao.updateMerchantReceiveEarnings(receiveEarningsOption, merchantId, authenticatedUser);
        if(merchant!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setResponseObject(merchant);
            payAccessResponse.setMessage("Merchants preference to receive earnings has been updated successfully");
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Merchants preference to receive earnings could not be updated successfully. Please try again");
        return payAccessResponse;
    }

    public PayAccessResponse updateMerchantBusinessType(String businessType, Long merchantIdL, User authenticatedUser) {
        Merchant merchant = merchantDao.updateMerchantBusinessType(businessType, merchantIdL, authenticatedUser);
        if(merchant!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setResponseObject(merchant);
            payAccessResponse.setMessage("Merchants business type updated successfully");
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Merchants business type not be updated successfully. Please try again");
        return payAccessResponse;
    }

    public PayAccessResponse createContactUsMessage(String emailAddress, String subject, String productCategory, String description, Urgency urgency, String newFileName, User authenticatedUser) {
        merchantDao.createContactUsMessage(emailAddress, subject, productCategory,
                description, urgency, newFileName, authenticatedUser);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
        payAccessResponse.setMessage("Message created successfully");
        return payAccessResponse;
    }

    public PayAccessResponse createFeedbackMessage(String emailAddress, String title, String productCategory, String description, Urgency urgency, User authenticatedUser) {
        merchantDao.createFeedbackMessage(emailAddress, title, productCategory,
                description, urgency, authenticatedUser);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
        payAccessResponse.setMessage("Feedback message created successfully");
        return payAccessResponse;
    }

    public PayAccessResponse updateMerchantNotifications(NotificationSettingRequest notificationSettingRequest, User authenticatedUser) {
        MerchantSetting merchantSetting = merchantDao.updateMerchantNotifications(notificationSettingRequest, authenticatedUser);
        if(merchantSetting!=null) {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Settings updated successfully");
            return payAccessResponse;
        }
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Settings update was not successfully");
        return payAccessResponse;
    }

    public PayAccessResponse updateMerchantSecurity(MerchantSecuritySettingRequest merchantSecuritySettingRequest, User authenticatedUser) {
        MerchantSetting merchantSetting = merchantDao.updateMerchantSecurity(merchantSecuritySettingRequest, authenticatedUser);
        if(merchantSetting!=null) {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Security Settings updated successfully");
            return payAccessResponse;
        }
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Security Settings update was not successfully");
        return payAccessResponse;
    }

    public PayAccessResponse updateMerchantPaymentSetting(MerchantPaymentSettingRequest merchantPaymentSettingRequest, User authenticatedUser) {
        MerchantSetting merchantSetting = merchantDao.updateMerchantPaymentSetting(merchantPaymentSettingRequest, authenticatedUser);
        if(merchantSetting!=null) {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Payment preference Settings updated successfully");
            return payAccessResponse;
        }
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Payment preference Settings update was not successfully");
        return payAccessResponse;
    }

    public PayAccessResponse getMerchantSettings(Long merchantId, User authenticatedUser) {
        MerchantSetting merchantSetting = merchantDao.getMerchantSettings(merchantId, authenticatedUser);
        if(merchantSetting!=null) {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setResponseObject(merchantSetting);
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Merchant Settings fetched successfully");
            return payAccessResponse;
        }
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Merchant Settings was not fetched successfully");
        return payAccessResponse;
    }

    public Merchant generateNewMerchantKeys(APIMode apiMode, Long merchantId, User authenticatedUser) {
        if(apiMode.equals(APIMode.LIVE))
        {
            String secretKeyLive = "sk_live_"+RandomStringUtils.randomAlphanumeric(40).toLowerCase();
            String publicKeyLive = "pk_live_"+RandomStringUtils.randomAlphanumeric(40).toLowerCase();
            return merchantDao.updateLiveKeys(merchantId, authenticatedUser, secretKeyLive, publicKeyLive);
        }
        else if(apiMode.equals(APIMode.TEST))
        {
            String secretKey = "sk_test_"+RandomStringUtils.randomAlphanumeric(40).toLowerCase();
            String publicKey = "pk_test_"+RandomStringUtils.randomAlphanumeric(40).toLowerCase();
            return merchantDao.updateTestKeys(merchantId, authenticatedUser, secretKey, publicKey);
        }

        return null;
    }

    public MerchantCredential getMerchantKeys(Long merchantId, User authenticatedUser) {
        return merchantDao.getMerchantKeys(merchantId, authenticatedUser);
    }

    public PayAccessResponse updateMerchantCallbackWebhook(UpdateMerchantCallbackRequest updateMerchantCallbackRequest, User authenticatedUser) {
        Merchant merchant = merchantDao.updateMerchantCallbackWebhook(updateMerchantCallbackRequest, authenticatedUser);
        if(merchant!=null) {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setResponseObject(merchant);
            payAccessResponse.setMessage("Merchant webhook and callback url updated successfully");
            return payAccessResponse;
        }
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Merchant webhook and callback url update was not successfully");
        return payAccessResponse;
    }

    public PayAccessResponse addNewMerchantToExistingUser(AddMerchantRequest addMerchantRequest, User authenticatedUser) {
        String secretKey = "sk_test_"+RandomStringUtils.randomAlphanumeric(40).toLowerCase();
        String publicKey = "pk_test_"+RandomStringUtils.randomAlphanumeric(40).toLowerCase();
        String secretKeyLive = "sk_live_"+RandomStringUtils.randomAlphanumeric(40).toLowerCase();
        String publicKeyLive = "pk_live_"+RandomStringUtils.randomAlphanumeric(40).toLowerCase();
        String merchantCode = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
        Merchant merchant = merchantDao.addNewMerchantToExistingUser(addMerchantRequest, authenticatedUser, secretKey, publicKey,
                secretKeyLive, publicKeyLive, merchantCode);
        if(merchant!=null) {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setResponseObject(merchant);
            payAccessResponse.setMessage("New merchant added successfully. Please proceed to provide other details of the merchant successfully");
            return payAccessResponse;
        }
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("New merchant could not be added successfully");
        return payAccessResponse;
    }
}
