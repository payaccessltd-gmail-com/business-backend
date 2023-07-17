package com.jamub.payaccess.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamub.payaccess.api.dao.CustomerDao;
import com.jamub.payaccess.api.dao.MerchantDao;
import com.jamub.payaccess.api.dao.UserDao;
import com.jamub.payaccess.api.dto.MerchantDTO;
import com.jamub.payaccess.api.dto.UserDTO;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.Customer;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.Merchant;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CustomerService {

    private CustomerDao customerDao;
    private UserDao userDao;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerDao customerDao, UserDao userDao){
        this.customerDao = customerDao;
        this.userDao = userDao;
    }

    public List<Customer> getAllCustomers(){
        return customerDao.getAll();
    }

    public PayAccessResponse createNewCustomer(CustomerSignUpRequest customerSignUpRequest) {
        List<User> existingCustomerUser = customerRepository.getUserByEmailAddress(customerSignUpRequest);
        if(existingCustomerUser!=null && !existingCustomerUser.isEmpty())
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Customer sign up was not successful. Customer email address is already signed up");
            return payAccessResponse;
        }
        Customer customer = customerRepository.save(customerSignUpRequest);
        if(customer!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("A One-Time code has been sent to your email - '"+customerSignUpRequest.getEmailAddress().toLowerCase()+"'. Please provide the One-Time code to continue your registration");
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Customer sign up was not successful. Please try again");
        return payAccessResponse;
    }

    public PayAccessResponse activateAccount(String emailAddress, String otp) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        Map<String, Object> m = customerDao.activateAccount(emailAddress, otp);

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

    public PayAccessResponse updateCustomerBioData(CustomerBioDataUpdateRequest customerBioDataUpdateRequest) {

        Customer customer = customerDao.updateCustomerBioData(customerBioDataUpdateRequest);
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

    public PayAccessResponse updateUserPin(CustomerPinUpdateRequest customerPinUpdateRequest) {

        User user = userDao.updateUserPin(customerPinUpdateRequest);
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
}
