package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.enums.*;
import com.jamub.payaccess.api.models.*;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.*;
import com.sun.mail.smtp.SMTPTransport;
import io.swagger.annotations.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/terminal")
@Api(produces = "application/json", value = "Operations pertaining to Terminal Management")
public class TerminalController {

    @Autowired
    private TerminalRequestService terminalRequestService;

    @Autowired
    private TerminalService terminalService;

    @Autowired
    EmailService emailService;

    @Autowired
    TokenService tokenService;
    @Autowired
    UserService userService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${default.page.size}")
    private Integer defaultPageSize;










    @CrossOrigin
    @RequestMapping(value = "/create-terminal-request", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    public ResponseEntity createTerminalRequest(@RequestBody @Valid TerminalOrderRequest terminalOrderRequest,
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return terminalRequestService.createTerminalRequest(terminalOrderRequest, authenticatedUser);

    }





    @CrossOrigin
    @RequestMapping(value = "/approve-terminal-request/{terminalRequestId}/{acquirerId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Approve Terminal Request", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity approveTerminalRequest(@PathVariable Long terminalRequestId,
                                             @PathVariable Long acquirerId,
                                                 @RequestBody @Valid ApproveTerminalRequest approveTerminalRequest,
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
        TerminalRequest terminalRequest = terminalRequestService.getTerminalRequest(terminalRequestId, null);

        logger.info("terminalRequest...{}", terminalRequest.getTerminalRequestStatus());

        if(terminalRequest!=null && terminalRequest.getTerminalRequestStatus().equals(TerminalRequestStatus.PENDING))
        {
            if(terminalRequest.getQuantity().intValue() < approveTerminalRequest.getSerialNumberList().size())
            {
                PayAccessResponse payAccessResponse = new PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
                payAccessResponse.setMessage("Terminal request could not be created. Terminals specified to be created must be equals or less than the requested terminals sent by the merchant");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
            }

            User terminalRequestOwner = userService.getUserById(terminalRequest.getCreatedByUserId());
            terminalRequest.setTerminalRequestStatus(TerminalRequestStatus.APPROVED);
            terminalRequest.setApprovedByUserId(authenticatedUser.getId());
            terminalRequest = terminalRequestService.updateTerminalRequest(terminalRequest);


            if(terminalRequest!=null) {

                List<Terminal> newTerminals = new ArrayList<Terminal>();
                logger.info("x<terminalRequest.getQuantity()...{}", terminalRequest.getQuantity());
                for(int x =0; x<approveTerminalRequest.getSerialNumberList().size(); x++)
                {
                    CreateTerminalRequest createTerminalRequest = new CreateTerminalRequest();
                    createTerminalRequest.setTerminalBrand(terminalRequest.getTerminalBrand().name());
                    createTerminalRequest.setTerminalType(terminalRequest.getTerminalType().name());
                    createTerminalRequest.setTerminalCode(RandomStringUtils.randomAlphanumeric(8).toUpperCase());
                    createTerminalRequest.setAcquirerId(acquirerId);
                    createTerminalRequest.setMerchantId(terminalRequest.getMerchantId());
                    createTerminalRequest.setTerminalRequestId(terminalRequest.getId());
                    createTerminalRequest.setTerminalSerialNo(approveTerminalRequest.getSerialNumberList().get(x));


                    Terminal terminal = terminalService.createTerminal(createTerminalRequest, authenticatedUser);
                    logger.info("terminal...{}", terminal.getId());
                    if(terminal!=null) {
                        newTerminals.add(terminal);
                    }
                }

//                List htmlSnippet = newTerminals.stream().map(t -> {
//
//                    return t.getTerminalCode();
//                }).collect(Collectors.toList());
                Map htmlSnippet = newTerminals.stream().collect(Collectors.toMap(Terminal::getTerminalCode, Terminal::getSerialNo));


                String htmlMessage = "<div style='background:#f5f5f5;background-color:#f5f5f5;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#f5f5f5;background-color:#f5f5f5;width:100%;'>	<tbody><tr>	<td><div style='Margin:0px auto;max-width:780px;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='width:100%;max-width:780px;background:#F5F5F5;background-color:#F5F5F5;'><tbody>	<tr><td style='direction:ltr;font-size:0px;padding-bottom:0px;text-align:center;vertical-align:top;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:transparent;background-color:transparent;width:100%;'><tbody>	<tr><td>	<div style='Margin:0px auto;max-width:620px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:30px;padding-bottom:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'>  </table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table><table align='center' style='width:100%;max-width:780px;background:#F5F5F5;background-color:#F5F5F5;' '=''>	<tbody><tr style='width:{headerImageWidth}px;'>	<td align='center' style='font-size:0px;padding:0px;word-break:break-word;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='border-collapse:collapse;border-spacing:0px;'>	<tbody><tr>	<td style='width:780px;padding:0pm 0px 0px 0px;padding-bottom:0px;'><img alt='Vend' height='auto' src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_header.png' style='border:0;display:block;outline:none;text-decoration:none;height:auto;width:100%;' width='{headerImageWidth}'> 	</td></tr>	</tbody></table>	</td></tr>	</tbody></table><div class='main-content' style='background:#fff;background-color:#fff;Margin:0px auto;max-width:780px;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#fff;background-color:#fff;width:100%'><tbody>	<tr><td colspan='3' style='height:30px'></td>	</tr>	<tr><td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;' class='mktoContainer' id='container'>	<table class='mktoModule' id='textSection' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable'><h1 style='text-align: center;'>Welcome to PayAccess! Let's get&nbsp;started.</h1><p style='padding-bottom: 20px;'>&nbsp;<br>Hello "+terminalRequestOwner.getFirstName()+"! Your request for a new terminal(s) has been approved. The terminal code for the terminal(s) are:<br>" + htmlSnippet +
                        ".<br><br>Proceed to enter the terminal code(s) into the terminals after their collection</p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='textSection4fc63ee1-701e-488b-a2a4-b1d5cdffd971' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable4fc63ee1-701e-488b-a2a4-b1d5cdffd971'><table width='100%' style='background-color: #e9f6e8;'>	<tbody><tr>	<td width='10%'>&nbsp;</td>	<td width='80%' align='center' style='text-align: center;'><h2 style='font-family: Helvetica, Arial, sans-serif; color: #626262; padding-top: 20px; padding-bottom: 10px;'>Your PayAccess Login Details</h2><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262; padding-bottom: 10px;'></p><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='border-collapse: separate; line-height: 100%;'>	<tbody><tr>	<td align='center' bgcolor='#41af4b' role='presentation' style='border: 2px solid transparent; border-radius: 0px; cursor: auto; padding: 14px 24px;' valign='middle'></td></tr>	</tbody></table><p>&nbsp;</p>	</td>	<td width='10%'>&nbsp;</td></tr>	</tbody></table><p>&nbsp;</p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='textSection4fc63ee1-701e-488b-a2a4-b1d5cdffd9718b5d4c4a-486c-40c3-9236-43cdf187ad89' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable4fc63ee1-701e-488b-a2a4-b1d5cdffd9718b5d4c4a-486c-40c3-9236-43cdf187ad89'><table width='100%' style='background-color: #f8f8f5;'>	<tbody><tr>	<td width='15%'>&nbsp;</td>	<td width='70%' align='center' style='text-align: center;'><h2 style='font-family: Helvetica, Arial, sans-serif; color: #626262; padding-top: 20px; padding-bottom: 10px;'>Set up PayAccess</h2><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>Get started with PayAccess by reading these four essential guides from our Help Centre and you'll be selling in no time!</p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_01.png' alt='V2439-Adoption-Onboarding-nurture-email-1_01.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Set up your outlets and registers</h3><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>Learn how to get PayAccess running on all of your registers and outlets.<br><a href='#'>Learn about registers</a></p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_02.png' alt='V2439-Adoption-Onboarding-nurture-email-1_02.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Organise your sales taxes</h3><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>Add any sales or value-added taxes (VAT) that are for your location&nbsp;or&nbsp;products.<br><a href='#'>Learn about taxes</a></p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_03.png' alt='V2439-Adoption-Onboarding-nurture-email-1_03.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Create different payment types</h3><p>Set up your payment terminal and registers so you can accept cash, cards and other&nbsp;payment&nbsp;types.<br><a href='#'>Learn about integrated payments</a></p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_04.png' alt='V2439-Adoption-Onboarding-nurture-email-1_04.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Add a product</h3><p>Start adding your products, stock levels and descriptions to&nbsp;your&nbsp;catalog.<br><a href='#'>Learn how to add products</a></p><p>&nbsp;</p>	</td>	<td width='15%'>&nbsp;</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='textSection2b16ab9a-73ae-43cf-8972-6db3159390c2' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable2b16ab9a-73ae-43cf-8972-6db3159390c2'><p>&nbsp;</p><h2>Your PayAccess to-do list</h2><p>Like to read ahead? Our <a href='#'>setup checklist</a> gives you a list of steps that you can check off at your own pace to get PayAccess set up.</p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='signoffWithoutProfilePhoto15f0ef74-c2b9-420f-a62b-2e96f521ed08' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;padding-top:30px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;'><p>Here's to your retail success, <br><br> <strong>Peters</strong> <br>Director of Adoption <br> </p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></td>	</tr>	<tr><td colspan='3' style='height:50px'></td>	</tr></tbody>	</table></div><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#f5f5f5;background-color:#f5f5f5;width:100%;'>	<tbody><tr>	<td><div style='Margin:0px auto;max-width:680px;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='width:100%;'><tbody>	<tr><td style='direction:ltr;font-size:0px;padding:5px;text-align:center;vertical-align:top;'>	<div style='background:#f5f5f5;background-color:#f5f5f5;Margin:0px auto;max-width:650px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#f5f5f5;background-color:#f5f5f5;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:15px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='center' style='font-size:0px;padding:0px;word-break:break-word;'>	<div class='mktoSnippet' id='unsubscribeFooter'><div style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>	PayAccess HQ, 2-36 Obalende Street, Abuja, Nigeria 	<br> 	<a style='color: #008cc5 !important; text-decoration: none !important;' href='#' target='_blank'>Unsubscribe</a> ∙ 	<a style='color: #008cc5 !important; text-decoration: none !important;' href='https://email.vendhq.com/Nzc2LVFGTy0zMzQAAAF_hWiDyzkDXU0MPpA_mZQkOV6uelqxQNlKl80Dp7nbfZsoBZZomppxXFRKN_z6O69Y_RlWN_c=' target='_blank'>Privacy Policy</a></div>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table></div>";


                String recipient = terminalRequestOwner.getEmailAddress();
                String subject = "PayAccess - Request for Terminal(s) Approved";
                CreateEmailDocumentRequest createEmailDocumentRequest = new CreateEmailDocumentRequest();
                createEmailDocumentRequest.setCreatedByUserId(authenticatedUser.getId());
                createEmailDocumentRequest.setSubject(subject);
                createEmailDocumentRequest.setRecipients(recipient);
                createEmailDocumentRequest.setAttachmentList(null);
                createEmailDocumentRequest.setHtmlMessage(htmlMessage);
                EmailDocument emailDocument = emailService.createEmailDocument(createEmailDocumentRequest, authenticatedUser, EmailDocumentPriorityLevel.LOW);


                if(!newTerminals.isEmpty()) {


                    try {
                        logger.info("=========================");
                        Properties props = System.getProperties();
                        props.put("mail.smtps.host", "smtp.mailgun.org");
                        props.put("mail.smtps.auth", "true");

                        Session session = Session.getInstance(props, null);
                        Message msg = new MimeMessage(session);
                        msg.setFrom(new InternetAddress("emailer@payaccess.com", "PayAccess"));

                        InternetAddress[] addrs = InternetAddress.parse(emailDocument.getRecipients(), false);
                        msg.setRecipients(Message.RecipientType.TO, addrs);

                        msg.setSubject("Welcome to PayAccess");
                        msg.setContent(createEmailDocumentRequest.getHtmlMessage(), "text/html; charset=utf-8");

                        //msg.setText("Copy the url and paste in your browser to activate your account - http://137.184.47.182:8081/payaccess/api/v1/user/activate-account/"+user.getEmailAddress()+"/" + verificationLink +" - providing the OTP: " + otp);

                        msg.setSentDate(new Date());

                        SMTPTransport t =
                                (SMTPTransport) session.getTransport("smtps");
                        t.connect("smtp.mailgun.org", "postmaster@mails.valuenaira.com", "k0l01qaz!QAZ");
                        t.sendMessage(msg, msg.getAllRecipients());

                        logger.info("Response: {}" , t.getLastServerResponse());

                        t.close();

                        emailDocument.setEmailDocumentStatus(EmailDocumentStatus.SENT);
                        emailService.updateEmailDocument(emailDocument);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                        logger.error("Error Sending Mail ...{}", e);


                    }



                    PayAccessResponse payAccessResponse = new PayAccessResponse();
                    payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
                    payAccessResponse.setMessage("Terminal request approved successfully. Terminal(s) requested for have also been created successfully.");
                    payAccessResponse.setResponseObject(htmlSnippet);
                    return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
                }


                PayAccessResponse payAccessResponse = new PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
                payAccessResponse.setMessage("Terminal request could not be created successfully");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
            }
        }


        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Terminal request approval was not successful");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);

    }


    @CrossOrigin
    @RequestMapping(value = "/delete-terminal-request", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Delete Terminal Request", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity deleteTerminalRequest(@RequestPart Long terminalRequestId,
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
        TerminalRequest terminalRequest = terminalRequestService.getTerminalRequest(terminalRequestId, null);

        if(terminalRequest!=null && terminalRequest.getCreatedByUserId().equals(authenticatedUser.getId()))
        {
            terminalRequest.setDeletedAt(LocalDateTime.now());
            terminalRequest.setTerminalRequestStatus(TerminalRequestStatus.DELETED);
            terminalRequest = terminalRequestService.updateTerminalRequest(terminalRequest);

            if(terminalRequest!=null) {
                PayAccessResponse payAccessResponse = new PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
                payAccessResponse.setMessage("Terminal request was successfully deleted");
                return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
            }
        }


        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Terminal request was not successfully deleted");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);

    }


    @CrossOrigin
    @RequestMapping(value = "/filter-terminal-request", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "List terminal Request using filters", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity filterTerminalRequest(@RequestBody @Valid TerminalRequestSearchFilterRequest terminalRequestSearchFilterRequest,
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
        List<TerminalRequest> terminalRequestList = terminalRequestService.getTerminalRequestsByFilter(terminalRequestSearchFilterRequest, authenticatedUser);

        if(terminalRequestList!=null) {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setResponseObject(terminalRequestList);
            payAccessResponse.setMessage("Terminal requests filtered successfully");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }


        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Terminal requests filtered were not successfully");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);

    }



    @CrossOrigin
    @RequestMapping(value = "/get-terminal-request/{terminalRequestId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Get Terminal Request details", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getTerminalRequest(@PathVariable Long terminalRequestId,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws JsonProcessingException {
        System.out.println("terminalRequestId");

        System.out.println("terminalRequestId..." + terminalRequestId);

        User authenticatedUser = tokenService.getUserFromToken(request);


        if(authenticatedUser==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
            payAccessResponse.setMessage("Authorization not granted. Token expired");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payAccessResponse);
        }
        TerminalRequest terminalRequest = terminalRequestService.getTerminalRequest(terminalRequestId, null);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setResponseObject(terminalRequest);
        if(terminalRequest!=null)
        {
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Terminal request details fetched successfully");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("Terminal request details fetch failed. Invalid request details provided");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
    }

    @CrossOrigin
    @RequestMapping(value = {"/get-terminal-requests", "/get-terminal-requests/{pageNumber}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "List terminal requests", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getTerminalRequests(@PathVariable(required = false) Integer pageNumber,
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
        return terminalRequestService.getTerminalRequests(pageNumber, defaultPageSize);
    }

    @CrossOrigin
    @RequestMapping(value = {"/get-terminals", "/get-terminals/{pageNumber}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "List terminals", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getTerminals(@PathVariable(required = false) Integer pageNumber,
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
        return terminalService.getTerminals(pageNumber, defaultPageSize);
    }



    @CrossOrigin
    @RequestMapping(value = {"/search-terminals"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Filter for terminals", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity getTerminals(TerminalSearchFilterRequest terminalSearchFilterRequest,
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

        if(!(terminalSearchFilterRequest.getPageSize()!=null && terminalSearchFilterRequest.getPageSize()>0))
        {
            terminalSearchFilterRequest.setPageSize(defaultPageSize);
        }
        List<Terminal> terminals = terminalService.getTerminalsByFilter(terminalSearchFilterRequest, authenticatedUser);

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Terminal fetch did not find any terminals matching");
        if(terminals!=null && !terminals.isEmpty())
        {
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Terminals matching search found");
            payAccessResponse.setResponseObject(terminals);
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }
        else if(terminals!=null && terminals.isEmpty()){
            payAccessResponse.setStatusCode(PayAccessStatusCode.LIST_EMPTY.label);
            payAccessResponse.setMessage("Search results yielded empty array");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);

    }



    @CrossOrigin
    @RequestMapping(value = "/disapprove-terminal-request/{terminalRequestId}/{acquirerId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Disapprove Terminal requests", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity disapproveTerminalRequest(@PathVariable Long terminalRequestId,
                                                 @PathVariable Long acquirerId,
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
        TerminalRequest terminalRequest = terminalRequestService.getTerminalRequest(terminalRequestId, null);

        logger.info("terminalRequest...{}", terminalRequest.getTerminalRequestStatus());

        if(terminalRequest!=null && terminalRequest.getTerminalRequestStatus().equals(TerminalRequestStatus.PENDING))
        {
            terminalRequest.setTerminalRequestStatus(TerminalRequestStatus.DECLINED);
            terminalRequest.setApprovedByUserId(authenticatedUser.getId());
            terminalRequest = terminalRequestService.updateTerminalRequest(terminalRequest);


            if(terminalRequest!=null)
            {
                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
                payAccessResponse.setMessage("Terminal request disapproved");
                return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
            }
        }


        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Terminal request approval was not successful");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);

    }


}
