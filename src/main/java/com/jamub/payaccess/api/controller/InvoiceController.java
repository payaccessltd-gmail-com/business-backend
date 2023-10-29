package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamub.payaccess.api.dao.util.UtilityHelper;
import com.jamub.payaccess.api.enums.InvoiceStatus;
import com.jamub.payaccess.api.enums.InvoiceType;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.Invoice;
import com.jamub.payaccess.api.models.InvoiceBreakdown;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.InvoiceService;
import com.jamub.payaccess.api.services.MerchantService;
import com.jamub.payaccess.api.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    TokenService tokenService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${default.page.size}")
    private Integer defaultPageSize;

    @Value("${path.uploads.invoice_logos}")
    private String fileDestinationPath;









    @CrossOrigin
    @RequestMapping(value = "/create-simple-invoice", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse createSimpleInvoice(CreateSimpleInvoiceRequest createSimpleInvoiceRequest,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        MultipartFile businessLogoFile = createSimpleInvoiceRequest.getBusinessLogo();


        if(!businessLogoFile.isEmpty())
        {
            try {
                String newFileName = UtilityHelper.uploadFile(businessLogoFile, fileDestinationPath);

                CreateNewInvoiceRequest createNewInvoiceRequest = new CreateNewInvoiceRequest();
                createNewInvoiceRequest.setInvoiceType(InvoiceType.SIMPLE.name());
                createNewInvoiceRequest.setCustomerName(createSimpleInvoiceRequest.getCustomerName());
                createNewInvoiceRequest.setCustomerEmail(createSimpleInvoiceRequest.getCustomerEmail());
                createNewInvoiceRequest.setAdditionalCustomerEmailAddress(createSimpleInvoiceRequest.getAdditionalCustomerEmailAddress());
                createNewInvoiceRequest.setDueDate(LocalDate.parse(createSimpleInvoiceRequest.getDueDate(), formatter));
//                createNewInvoiceRequest.setDueDate(createSimpleInvoiceRequest.getDueDate());
//                BigDecimal amountB = BigDecimal.valueOf();
                createNewInvoiceRequest.setAmount(createSimpleInvoiceRequest.getAmount());
                createNewInvoiceRequest.setInvoiceNote(createSimpleInvoiceRequest.getInvoiceNote());
                createNewInvoiceRequest.setBusinessLogo(newFileName);
                createNewInvoiceRequest.setMerchantId(createSimpleInvoiceRequest.getMerchantId());

                System.out.println("................................");
                PayAccessResponse payAccessResponse = invoiceService.createSimpleInvoice(createNewInvoiceRequest, authenticatedUser);

                return payAccessResponse;
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }

        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. OTP expired");
        return payAccessResponse;

    }



    @CrossOrigin
    @RequestMapping(value = "/create-standard-invoice", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse createStandardInvoice(@RequestBody StandardInvoiceRequest standardInvoiceRequest,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);


        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. OTP expired");
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = invoiceService.createStandardInvoice(standardInvoiceRequest, authenticatedUser);

        return payAccessResponse;

    }




    @CrossOrigin
    @RequestMapping(value = "/mark-invoice-paid/{invoiceId}/{merchantId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse markInvoicePaid(@PathVariable Long invoiceId,
                                             @PathVariable Long merchantId,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        Invoice invoice = invoiceService.getInvoice(invoiceId, merchantId);

        if(invoice!=null && invoice.getCreatedByUserId().equals(authenticatedUser.getId()))
        {
            invoice.setInvoiceStatus(InvoiceStatus.PAID);
            invoice = invoiceService.updateInvoice(invoice);

            if(invoice!=null) {
                PayAccessResponse payAccessResponse = new PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
                payAccessResponse.setMessage("Invoice update was successful");
                return payAccessResponse;
            }
        }


        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. OTP expired");
        return payAccessResponse;


    }


    @CrossOrigin
    @RequestMapping(value = "/delete-invoice/{invoiceId}/{merchantId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse deleteInvoice(@PathVariable Long invoiceId,
                                           @PathVariable Long merchantId,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        Invoice invoice = invoiceService.getInvoice(invoiceId, merchantId);

        if(invoice!=null && invoice.getCreatedByUserId().equals(authenticatedUser.getId()))
        {
            invoice.setInvoiceStatus(InvoiceStatus.DELETED);
            invoice = invoiceService.updateInvoice(invoice);

            if(invoice!=null) {
                PayAccessResponse payAccessResponse = new PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
                payAccessResponse.setMessage("Invoice deletion was successful");
                return payAccessResponse;
            }
        }


        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. OTP expired");
        return payAccessResponse;


    }


    @CrossOrigin
    @RequestMapping(value = "/filter-invoice", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse filterInvoice(@RequestBody InvoiceSearchFilterRequest invoiceSearchFilterRequest,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        List<Invoice> invoiceList = invoiceService.getInvoiceByFilter(invoiceSearchFilterRequest, authenticatedUser);

        if(invoiceList!=null) {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setResponseObject(invoiceList);
            payAccessResponse.setMessage("Invoices filtered successfully");
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. OTP expired");
        return payAccessResponse;


    }



    @CrossOrigin
    @RequestMapping(value = "/get-invoice-details/{invoiceId}/{merchantId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse getMerchantDetails(@PathVariable Long invoiceId,
                                                @PathVariable Long merchantId,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws JsonProcessingException {
        System.out.println("invoiceId");

        System.out.println("invoiceId..." + invoiceId);

        User authenticatedUser = tokenService.getUserFromToken(request);


        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. OTP expired");
            return payAccessResponse;
        }
        Invoice invoice = invoiceService.getInvoice(invoiceId, merchantId);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setResponseObject(invoice);
        if(invoice!=null)
        {
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Invoice details fetched successfully");
            return payAccessResponse;
        }
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("Invoice details listing fetch failed");
        return payAccessResponse;
    }

    @CrossOrigin
    @RequestMapping(value = {"/get-invoices", "/get-invoices/{pageNumber}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse getInvoices(@PathVariable(required = false) Integer pageNumber,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);


        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. OTP expired");
            return payAccessResponse;
        }
        PayAccessResponse payAccessResponse = invoiceService.getInvoices(pageNumber, defaultPageSize);


        return payAccessResponse;
    }



    @CrossOrigin
    @RequestMapping(value = "/get-invoice-breakdown/{merchantId}/{invoiceId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse getInvoiceBreakdown(@PathVariable Long merchantId,
                                                 @PathVariable Long invoiceId,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws JsonProcessingException {
        System.out.println("invoiceId");

        System.out.println("invoiceId..." + invoiceId);

        User authenticatedUser = tokenService.getUserFromToken(request);


        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. OTP expired");
            return payAccessResponse;
        }
        List<InvoiceBreakdown> invoiceBreakdownList = invoiceService.getInvoiceBreakdownByInvoiceId(invoiceId, merchantId);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setResponseObject(invoiceBreakdownList);
        if(invoiceBreakdownList!=null)
        {
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Invoice breakdown details fetched successfully");
            return payAccessResponse;
        }
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("Invoice breakdown details listing fetch failed");
        return payAccessResponse;
    }


}
