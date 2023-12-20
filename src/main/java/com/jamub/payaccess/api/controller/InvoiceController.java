package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import com.jamub.payaccess.api.dao.util.UtilityHelper;
import com.jamub.payaccess.api.enums.DiscountType;
import com.jamub.payaccess.api.enums.InvoiceStatus;
import com.jamub.payaccess.api.enums.InvoiceType;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.*;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.InvoiceService;
import com.jamub.payaccess.api.services.MerchantService;
import com.jamub.payaccess.api.services.TokenService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/invoice")
@Api(produces = "application/json", value = "Operations pertaining to Invoices.")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    TokenService tokenService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${default.page.size}")
    private Integer defaultPageSize;

    @Value("${path.uploads.invoice_logos}")
    private String fileDestinationPath;



    @Value("${path.uploads.invoice_qr_path}")
    private String invoiceQRPath;

    String[] acceptableInvoiceStatus = new String[]{InvoiceStatus.DRAFT.name(), InvoiceStatus.PENDING.name()};









    @CrossOrigin
    //CREATE_INVOICE
    @RequestMapping(value = "/create-simple-invoice", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Create Simple Invoice", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity createSimpleInvoice(@Valid CreateSimpleInvoiceRequest createSimpleInvoiceRequest,
                                              BindingResult bindingResult,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) throws JsonProcessingException {


        if (bindingResult.hasErrors()) {
            List errorMessageList =  bindingResult.getFieldErrors().stream().map(fe -> {
                return new ErrorMessage(fe.getField(), fe.getDefaultMessage());
            }).collect(Collectors.toList());

            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setResponseObject(errorMessageList);
            payAccessResponse.setStatusCode(PayAccessStatusCode.VALIDATION_FAILED.label);
            payAccessResponse.setMessage("Request validation failed");
            return ResponseEntity.badRequest().body(payAccessResponse);
        }

        logger.info("invoiceStatus...{}", createSimpleInvoiceRequest.getInvoiceStatus());
        User authenticatedUser = tokenService.getUserFromToken(request);

        if(authenticatedUser!=null)
        {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            if(!Arrays.asList(acceptableInvoiceStatus).contains(createSimpleInvoiceRequest.getInvoiceStatus()))
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.INVALID_PARAMETER.label);
                payAccessResponse.setMessage("Parameter mismatch. Invalid invoice status");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
            }

            MultipartFile businessLogoFile = createSimpleInvoiceRequest.getBusinessLogo();


            if(businessLogoFile==null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.INVALID_FILE_TYPE.label);
                payAccessResponse.setMessage("Ensure you select a business logo to upload");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
            }

            if(!businessLogoFile.isEmpty())
            {
                try {
                    if(UtilityHelper.checkIfImage(businessLogoFile)==false)
                    {
                        PayAccessResponse payAccessResponse = new  PayAccessResponse();
                        payAccessResponse.setStatusCode(PayAccessStatusCode.INVALID_FILE_TYPE.label);
                        payAccessResponse.setMessage("Ensure you select a valid image file as your business logo");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
                    }
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
                    createNewInvoiceRequest.setInvoiceStatus(createSimpleInvoiceRequest.getInvoiceStatus());

                    System.out.println("................................");
                    Merchant merchant = merchantService.getMerchantById(createNewInvoiceRequest.getMerchantId());
                    return invoiceService.createSimpleInvoice(createNewInvoiceRequest, authenticatedUser, createSimpleInvoiceRequest.getInvoiceStatus(), merchant, invoiceQRPath);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                    PayAccessResponse payAccessResponse = new PayAccessResponse();
                    payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
                    payAccessResponse.setMessage("Invoice could not be created. Resource denial error");
                    payAccessResponse.setResponseObject(e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    PayAccessResponse payAccessResponse = new PayAccessResponse();
                    payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
                    payAccessResponse.setMessage("Invoice could not be created. Resource generation error");
                    payAccessResponse.setResponseObject(e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
                } catch (WriterException e) {
                    e.printStackTrace();
                    PayAccessResponse payAccessResponse = new PayAccessResponse();
                    payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
                    payAccessResponse.setMessage("Invoice could not be created. Resource write denial error");
                    payAccessResponse.setResponseObject(e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
                }

            }
        }


        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);

    }



    @CrossOrigin
    //CREATE_INVOICE
    @RequestMapping(value = "/create-standard-invoice", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Create Standard Invoice", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity createStandardInvoice(@RequestBody @Valid StandardInvoiceRequest standardInvoiceRequest,
                                                BindingResult bindingResult,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws JsonProcessingException {



        if (bindingResult.hasErrors()) {
            List errorMessageList =  bindingResult.getFieldErrors().stream().map(fe -> {
                return new ErrorMessage(fe.getField(), fe.getDefaultMessage());
            }).collect(Collectors.toList());

            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setResponseObject(errorMessageList);
            payAccessResponse.setStatusCode(PayAccessStatusCode.VALIDATION_FAILED.label);
            payAccessResponse.setMessage("Request validation failed");
            return ResponseEntity.badRequest().body(payAccessResponse);
        }

        User authenticatedUser = tokenService.getUserFromToken(request);


        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. Token expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }


        if(!Arrays.asList(acceptableInvoiceStatus).contains(standardInvoiceRequest.getInvoiceStatus()))
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.INVALID_PARAMETER.label);
            payAccessResponse.setMessage("Parameter mismatch. Invalid invoice status");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
        }

        try
        {
            Merchant merchant = merchantService.getMerchantById(standardInvoiceRequest.getMerchantId());
            return invoiceService.createStandardInvoice(standardInvoiceRequest, authenticatedUser, merchant, invoiceQRPath);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Invoice could not be created. Resource denial error");
            payAccessResponse.setResponseObject(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Invoice could not be created. Resource generation error");
            payAccessResponse.setResponseObject(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        } catch (WriterException e) {
            e.printStackTrace();
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Invoice could not be created. Resource write denial error");
            payAccessResponse.setResponseObject(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
        }



    }





    @CrossOrigin
    //RESEND_INVOICE_EMAIL
    @RequestMapping(value = "/resend-invoice-email/{invoiceId}/{merchantId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Resend Invoice Email for Payment", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity resendInvoiceEmail(@PathVariable Long invoiceId,
                                                @PathVariable Long merchantId,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);

        if(authenticatedUser!=null)
        {
            try {

                Invoice invoice = invoiceService.getInvoice(invoiceId, merchantId);
                Merchant merchant = merchantService.getMerchantById(merchantId);

                if(merchant!=null && invoice!=null && merchant.getUserId().equals(authenticatedUser.getId()))
                {
                    return invoiceService.resendInvoiceEmail(invoice, merchant);
                }

                if(!merchant.getUserId().equals(authenticatedUser.getId()))
                {
                    PayAccessResponse payAccessResponse = new  PayAccessResponse();
                    payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                    payAccessResponse.setMessage("You are not authorized to access this Invoice.");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
                }
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
                payAccessResponse.setMessage("Invalid parameters provided. Invoice, merchant could not be found.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
            }
            catch(Exception e)
            {
                e.printStackTrace();

                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Resending of the invoice was not successful.");
                payAccessResponse.setResponseObject(e.getMessage());
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(payAccessResponse);
            }
        }


        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);

    }



    @CrossOrigin
    //MARK_INVOICE_AS_PAID
    @RequestMapping(value = "/mark-invoice-paid/{invoiceId}/{merchantId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Mark Invoice As Paid", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity markInvoicePaid(@PathVariable Long invoiceId,
                                             @PathVariable Long merchantId,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);
        if(authenticatedUser!=null)
        {
            Invoice invoice = invoiceService.getInvoice(invoiceId, merchantId);

            if(invoice!=null && invoice.getCreatedByUserId().equals(authenticatedUser.getId()))
            {
                logger.info("{}....{}", authenticatedUser.getId(), invoice.getCreatedByUserId());

                invoice.setInvoiceStatus(InvoiceStatus.PAID);
                invoice = invoiceService.updateInvoice(invoice);

                if(invoice!=null) {
                    PayAccessResponse payAccessResponse = new PayAccessResponse();
                    payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
                    payAccessResponse.setMessage("Invoice update was successful");
                    return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
                }
            }

            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
            payAccessResponse.setMessage("Invalid action. Ensure this invoice is still available to be deleted and belongs to you");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
        }



        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);


    }


    @CrossOrigin
    //DELETE_INVOICE
    @RequestMapping(value = "/delete-invoice/{invoiceId}/{merchantId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Delete Invoice", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity deleteInvoice(@PathVariable Long invoiceId,
                                           @PathVariable Long merchantId,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);

        if(authenticatedUser!=null)
        {
            Invoice invoice = invoiceService.getInvoice(invoiceId, merchantId);

            if(invoice!=null && invoice.getCreatedByUserId().equals(authenticatedUser.getId()))
            {
                invoice.setInvoiceStatus(InvoiceStatus.DELETED);
                invoice.setDeletedAt(LocalDateTime.now());
                Integer successCheck = invoiceService.deleteInvoice(invoice);

                if(successCheck==1) {
                    PayAccessResponse payAccessResponse = new PayAccessResponse();
                    payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
                    payAccessResponse.setMessage("Invoice deletion was successful");
                    return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
                }
                PayAccessResponse payAccessResponse = new PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
                payAccessResponse.setMessage("Invoice deletion was not successful");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
            }
        }



        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);


    }


    @CrossOrigin
    //VIEW_INVOICES
    @RequestMapping(value = "/filter-invoice", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "List Invoices Using Filter", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity filterInvoice(@RequestBody @Valid InvoiceSearchFilterRequest invoiceSearchFilterRequest,
                                        BindingResult bindingResult,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws JsonProcessingException {


        if (bindingResult.hasErrors()) {
            List errorMessageList =  bindingResult.getFieldErrors().stream().map(fe -> {
                return new ErrorMessage(fe.getField(), fe.getDefaultMessage());
            }).collect(Collectors.toList());

            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setResponseObject(errorMessageList);
            payAccessResponse.setStatusCode(PayAccessStatusCode.VALIDATION_FAILED.label);
            payAccessResponse.setMessage("Request validation failed");
            return ResponseEntity.badRequest().body(payAccessResponse);
        }
        User authenticatedUser = tokenService.getUserFromToken(request);

        if(authenticatedUser!=null)
        {
            List<Invoice> invoiceList = invoiceService.getInvoiceByFilter(invoiceSearchFilterRequest, authenticatedUser);

            if(invoiceList!=null) {
                PayAccessResponse payAccessResponse = new PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
                payAccessResponse.setResponseObject(invoiceList);
                payAccessResponse.setMessage("Invoices filtered successfully");
                return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
            }
        }


        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Authorization not granted. Token expired");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);


    }



    @CrossOrigin
    //VIEW_INVOICES
    @RequestMapping(value = "/get-invoice-details/{invoiceId}/{merchantId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Get Invoice Details", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getInvoiceDetails(@PathVariable Long invoiceId,
                                                @PathVariable Long merchantId,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws JsonProcessingException {
        System.out.println("merchantId...." + merchantId);

        System.out.println("invoiceId..." + invoiceId);

        User authenticatedUser = tokenService.getUserFromToken(request);


        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. Token expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }
        Invoice invoice = invoiceService.getInvoice(invoiceId, merchantId);

        Map<String, Object> generatedValues = new HashMap<String, Object>();

        if(invoice.getInvoiceType().equals(InvoiceType.STANDARD))
        {
            BigDecimal discountValue = BigDecimal.ZERO;
            BigDecimal taxValue = BigDecimal.ZERO;
            BigDecimal shippingFeeValue = BigDecimal.ZERO;
            BigDecimal totalValue = invoice.getAmount();
            if(invoice.getDiscount()!=null && invoice.getDiscountType().equals(DiscountType.PERCENTAGE))
            {
                discountValue = invoice.getAmount().multiply(invoice.getDiscount()).divide(BigDecimal.valueOf(100));
                totalValue = totalValue.subtract(discountValue);
            }
            else if(invoice.getDiscount()!=null && invoice.getDiscountType().equals(DiscountType.VALUE))
            {
                discountValue = invoice.getDiscount();
                totalValue = totalValue.subtract(discountValue);
            }

            if(invoice.getTaxAmount()!=null) {
                taxValue = totalValue.multiply(invoice.getTaxAmount()).divide(BigDecimal.valueOf(100.00));
                totalValue = totalValue.add(taxValue);
            }

            if(invoice.getShippingFee()!=null) {
                shippingFeeValue = invoice.getShippingFee();
                totalValue = totalValue.add(shippingFeeValue);
            }

//            totalValue = invoice.getAmount().add(taxValue).add(shippingFeeValue).subtract(discountValue);

            generatedValues.put("discountValue", discountValue);
            generatedValues.put("taxValue", taxValue);
            generatedValues.put("shippingFeeValue", shippingFeeValue);
            generatedValues.put("totalValue", totalValue);
        }

        generatedValues.put("invoiceDetails", invoice);

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setResponseObject(generatedValues);
        if(invoice!=null)
        {
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Invoice details fetched successfully");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("Invoice details listing fetch failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
    }






    @CrossOrigin
    @RequestMapping(value = "/get-invoice-details-for-guest/{invoiceNumber}/{merchantCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Get Invoice Details for Guests", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getInvoiceDetailsForGuest(@PathVariable String invoiceNumber,
                                                @PathVariable String merchantCode,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws JsonProcessingException {
        System.out.println("invoiceId");

        System.out.println("invoiceId..." + invoiceNumber);


        Invoice invoice = invoiceService.getInvoice(invoiceNumber, merchantCode);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setResponseObject(invoice);
        if(invoice!=null)
        {
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Invoice details fetched successfully");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("Invoice details listing fetch failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
    }

    @CrossOrigin
    //VIEW_INVOICES
    @RequestMapping(value = {"/get-invoices/{merchantId}/{rowCount}", "/get-invoices/{merchantId}/{rowCount}/{pageNumber}"}, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Get List of Invoices", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getInvoices(@PathVariable(required = true) Long merchantId,
                                         @PathVariable(required = true) Integer rowCount,
                                         @PathVariable(required = false) Integer pageNumber,
                                         @RequestBody GetInvoiceFilterRequest getInvoiceFilterRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);


        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. Token expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }
        return invoiceService.getInvoices(getInvoiceFilterRequest, pageNumber, rowCount, merchantId);
    }



    @CrossOrigin
    //VIEW_INVOICES
    @RequestMapping(value = "/get-invoice-breakdown/{merchantId}/{invoiceId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Get Breakdown of Invoice Items", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getInvoiceBreakdown(@PathVariable Long merchantId,
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
            payAccessResponse.setMessage("Authorization not granted. Token expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }
        List<InvoiceBreakdown> invoiceBreakdownList = invoiceService.getInvoiceBreakdownByInvoiceId(invoiceId, merchantId);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setResponseObject(invoiceBreakdownList);
        if(invoiceBreakdownList!=null)
        {
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Invoice breakdown details fetched successfully");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("Invoice breakdown details listing fetch failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
    }


}
