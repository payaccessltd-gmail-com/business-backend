package com.jamub.payaccess.api.services;

import com.jamub.payaccess.api.dao.BankDao;
import com.jamub.payaccess.api.dao.PaymentRequestDao;
import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.enums.PaymentRequestType;
import com.jamub.payaccess.api.models.Bank;
import com.jamub.payaccess.api.models.PaymentRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class PaymentRequestService {


    @Autowired
    private PaymentRequestDao paymentRequestDao;

    private Logger log = LoggerFactory.getLogger(this.getClass());




    @Autowired
    public PaymentRequestService(PaymentRequestDao paymentRequestDao){
        this.paymentRequestDao = paymentRequestDao;
    }


    public PaymentRequest createPaymentRequest(String merchantCode, String terminalCode, String orderRef, PaymentRequestType paymentRequestType, String requestBody) {



        PaymentRequest paymentRequest = this.paymentRequestDao.createNewPaymentRequest
                (merchantCode, terminalCode, orderRef, paymentRequestType, requestBody);

        return paymentRequest;

    }


    public Map getPaymentRequestByPagination(Integer pageNumber, Integer pageSize) {
        return this.paymentRequestDao.getPaymentRequestsByPagination(pageNumber, pageSize);
    }


    public PaymentRequest getPaymentRequestById(Long paymentRequestId) {
        return this.paymentRequestDao.getPaymentRequestsById(paymentRequestId);
    }

    public PaymentRequest updatePaymentRequest(PaymentRequest paymentRequest) {
        return this.paymentRequestDao.update(paymentRequest);
    }
}
