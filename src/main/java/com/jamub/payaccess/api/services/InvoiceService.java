package com.jamub.payaccess.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamub.payaccess.api.dao.InvoiceDao;
import com.jamub.payaccess.api.dao.MerchantDao;
import com.jamub.payaccess.api.dao.UserDao;
import com.jamub.payaccess.api.dto.MerchantDTO;
import com.jamub.payaccess.api.dto.UserDTO;
import com.jamub.payaccess.api.enums.InvoiceType;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.Invoice;
import com.jamub.payaccess.api.models.InvoiceBreakdown;
import com.jamub.payaccess.api.models.Merchant;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class InvoiceService {

    private InvoiceDao invoiceDao;
    private UserDao userDao;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public InvoiceService(InvoiceDao invoiceDao, UserDao userDao){

        this.invoiceDao = invoiceDao;
        this.userDao = userDao;
    }



    public Invoice getInvoice(Long invoiceId, Long merchantId) {

        Optional<Invoice> optionalInvoice = invoiceDao.get(invoiceId, merchantId);
        Invoice invoice = null;
        if(optionalInvoice.isPresent())
            invoice = optionalInvoice.get();

        return invoice;

    }

    public Invoice updateInvoice(Invoice invoice)
    {
        return invoiceDao.update(invoice);
    }

    public PayAccessResponse getInvoices(Integer pageNumber, Integer pageSize) {
        if(pageNumber==null)
            pageNumber = 0;

        List<Invoice> queryResponse = invoiceDao.getAll(pageNumber, pageSize);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setResponseObject(queryResponse);
        if(queryResponse!=null)
        {
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Invoices fetched successfully");
            return payAccessResponse;
        }
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("Invoices listing fetch failed");
        return payAccessResponse;

    }

    public PayAccessResponse createSimpleInvoice(CreateNewInvoiceRequest createNewInvoiceRequest, User authenticatedUser) {
        invoiceDao.saveInvoice(createNewInvoiceRequest.getInvoiceType(), createNewInvoiceRequest.getCustomerName(),
                createNewInvoiceRequest.getCustomerEmail(), createNewInvoiceRequest.getAdditionalCustomerEmailAddress(),
                createNewInvoiceRequest.getDueDate(), createNewInvoiceRequest.getAmount(), createNewInvoiceRequest.getInvoiceNote(),
                createNewInvoiceRequest.getBusinessLogo(), createNewInvoiceRequest.getMerchantId(), authenticatedUser);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
        payAccessResponse.setMessage("Invoice created successfully");
        return payAccessResponse;
    }

    public PayAccessResponse createStandardInvoice(StandardInvoiceRequest standardInvoiceRequest, User authenticatedUser) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


        LocalDate dueDate = LocalDate.parse(standardInvoiceRequest.getDueDate(), formatter);

        Invoice invoice = invoiceDao.saveInvoice(InvoiceType.STANDARD.name(), standardInvoiceRequest.getCustomerName(),
                standardInvoiceRequest.getCustomerEmail(), standardInvoiceRequest.getAdditionalCustomerEmailAddress(),
                dueDate, BigDecimal.valueOf(standardInvoiceRequest.getAmount()), standardInvoiceRequest.getInvoiceNote(),
                null, standardInvoiceRequest.getMerchantId(), authenticatedUser);
        if(invoice!=null)
        {
            standardInvoiceRequest.getInvoiceBreakdownList().forEach(ibl -> {
                ibl.setInvoiceId(invoice.getId());
                invoiceDao.saveInvoiceBreakDown(
                        ibl.getInvoiceId(),
                        ibl.getInvoiceItem(),
                        ibl.getQuantity(),
                        ibl.getCostPerUnit()
                );
            });

        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
        payAccessResponse.setMessage("Invoice created successfully");
        return payAccessResponse;
    }

    public List<Invoice> getInvoiceByFilter(InvoiceSearchFilterRequest invoiceSearchFilterRequest, User authenticatedUser) {
        List<Invoice> queryResponse = invoiceDao.getInvoiceByFilter(
                invoiceSearchFilterRequest.getInvoiceStatus(),
                invoiceSearchFilterRequest.getEmailAddress(),
                invoiceSearchFilterRequest.getStartDate(),
                invoiceSearchFilterRequest.getEndDate(),
                authenticatedUser.getId(),
                invoiceSearchFilterRequest.getMerchantId()
        );
        return queryResponse;
    }

    public List<InvoiceBreakdown> getInvoiceBreakdownByInvoiceId(Long invoiceId, Long merchantId) {
        List<InvoiceBreakdown> queryResponse = invoiceDao.getInvoiceBreakdownByInvoiceId(invoiceId, merchantId);
        return queryResponse;
    }
}
