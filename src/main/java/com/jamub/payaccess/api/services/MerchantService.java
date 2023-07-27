package com.jamub.payaccess.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamub.payaccess.api.dao.MerchantDao;
import com.jamub.payaccess.api.dao.UserDao;
import com.jamub.payaccess.api.dto.MerchantDTO;
import com.jamub.payaccess.api.dto.UserDTO;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.Merchant;
import com.jamub.payaccess.api.models.request.MerchantSignUpRequest;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.MerchantBusinessBankAccountDataUpdateRequest;
import com.jamub.payaccess.api.models.request.MerchantBusinessDataUpdateRequest;
import com.jamub.payaccess.api.models.request.MerchantUserBioDataUpdateRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public PayAccessResponse createNewMerchant(MerchantSignUpRequest merchantSignUpRequest) {
        System.out.println(merchantSignUpRequest.getEmailAddress());
        List<User> existingMerchantUsers = userDao.getUserByEmailAddress(merchantSignUpRequest.getEmailAddress());
        logger.info("{}", existingMerchantUsers);
        if(existingMerchantUsers!=null && !existingMerchantUsers.isEmpty())
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Merchant sign up was not successful. A merchant with the email address already is already signed up");
            return payAccessResponse;
        }

        merchantSignUpRequest.setVerificationLink(RandomStringUtils.randomAlphanumeric(128));
        Merchant merchant = merchantDao.save(merchantSignUpRequest);
        if(merchant!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("A link has been sent to your email address '"+merchantSignUpRequest.getEmailAddress().toLowerCase()+"'. Please click on the link " +
                    "in the email to verify your merchant account");
            payAccessResponse.setResponseObject(merchantSignUpRequest.getVerificationLink());
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Merchant sign up was not successful. Please try again");
        HashMap<String, String> responseObject = new HashMap<>();
        responseObject.put("verificationLink", merchantSignUpRequest.getVerificationLink());
        payAccessResponse.setResponseObject(responseObject);

        logger.info("{}", payAccessResponse);
        return payAccessResponse;
    }

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

        User user = merchantDao.updateMerchantBioData(merchantUserBioDataUpdateRequest, authenticatedUser);
        if(user!=null)
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

    public PayAccessResponse updateMerchantBusinessData(MerchantBusinessDataUpdateRequest merchantBusinessDataUpdateRequest, User authenticatedUser) {
        Merchant merchant = merchantDao.updateMerchantBusinessData(merchantBusinessDataUpdateRequest, authenticatedUser);
        if(merchant!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
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
}
