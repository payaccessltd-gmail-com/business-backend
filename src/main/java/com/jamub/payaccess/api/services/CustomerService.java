package com.jamub.payaccess.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamub.payaccess.api.dao.AccountDao;
import com.jamub.payaccess.api.dao.CustomerDao;
import com.jamub.payaccess.api.dao.MerchantDao;
import com.jamub.payaccess.api.dao.UserDao;
import com.jamub.payaccess.api.dto.MerchantDTO;
import com.jamub.payaccess.api.dto.UserDTO;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.Account;
import com.jamub.payaccess.api.models.Customer;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.Merchant;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.models.response.ValidateAccountResponse;
import com.jamub.payaccess.api.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CustomerService {

    private CustomerDao customerDao;
    private UserDao userDao;
    private AccountDao accountDao;

    @Autowired
    RestTemplate restTemplate;


    @Autowired
    public CustomerService(CustomerDao customerDao, UserDao userDao, AccountDao accountDao){
        this.customerDao = customerDao;
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    public List<Customer> getAllCustomers(){
        return customerDao.getAll();
    }

    public PayAccessResponse createNewCustomer(CustomerSignUpRequest customerSignUpRequest) {
        List<User> existingCustomerUser = userDao.getUserByEmailAddress(customerSignUpRequest.getEmailAddress());
        if(existingCustomerUser!=null && !existingCustomerUser.isEmpty())
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Customer sign up was not successful. Customer email address is already signed up");
            return payAccessResponse;
        }
        Customer customer = customerDao.save(customerSignUpRequest);
        if(customer!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("A One-Time code has been sent to your email - '"+customerSignUpRequest.getEmailAddress().toLowerCase()+"'. Please provide the One-Time code to continue your registration");
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Customer sign up was successful. Please try again");
        return payAccessResponse;
    }



    public PayAccessResponse getHoliday() {
        String uri = "";
        ResponseEntity<ValidateAccountResponse> responseEntity = restTemplate.getForObject(uri, ResponseEntity.class);
        ValidateAccountResponse validateAccountResponse = responseEntity.getBody();
        HttpStatus httpStatus = responseEntity.getStatusCode();
        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        return payAccessResponse;
    }



    public PayAccessResponse updateCustomerBioData(CustomerBioDataUpdateRequest customerBioDataUpdateRequest, User authenticatedUser, AccountService accountService) {

        Customer customer = customerDao.updateCustomerBioData(customerBioDataUpdateRequest, authenticatedUser);
        if(customer!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Customer Bio-Data updated successfully");
            return payAccessResponse;
        }


        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Customer Bio-Data update was not successful. Please try again");
        return payAccessResponse;
    }

    public PayAccessResponse updateUserPin(CustomerPinUpdateRequest customerPinUpdateRequest, User authenticatedUser) {

        User user = userDao.updateUserPin(customerPinUpdateRequest, authenticatedUser);
        if(user!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("User pin updated successfully");
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("User pin update was not successful. Please try again");
        return payAccessResponse;
    }


//    public PayAccessResponse createCustomerAccountPin(Account account, AccountService accountService,
//                                                      CustomerPinUpdateRequest customerPinUpdateRequest, User authenticatedUser) {
//
//        User user = accountDao.createAccountPin(account, customerPinUpdateRequest, authenticatedUser);
//        if(user!=null)
//        {
//            PayAccessResponse payAccessResponse = new PayAccessResponse();
//            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
//            payAccessResponse.setMessage("User pin updated successfully");
//            return payAccessResponse;
//        }
//
//        PayAccessResponse payAccessResponse = new PayAccessResponse();
//        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
//        payAccessResponse.setMessage("User pin update was not successful. Please try again");
//        return payAccessResponse;
//    }

    public Customer getCustomerByUserId(Long customerId) {
        Customer customer = customerDao.getCustomerByUserId(customerId);
        return customer;
    }
}
