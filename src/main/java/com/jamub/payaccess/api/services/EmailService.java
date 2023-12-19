package com.jamub.payaccess.api.services;

import com.jamub.payaccess.api.dao.EmailDao;
import com.jamub.payaccess.api.dao.TerminalDao;
import com.jamub.payaccess.api.enums.EmailDocumentPriorityLevel;
import com.jamub.payaccess.api.enums.EmailDocumentStatus;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.EmailDocument;
import com.jamub.payaccess.api.models.Terminal;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.CreateEmailDocumentRequest;
import com.jamub.payaccess.api.models.request.CreateTerminalRequest;
import com.jamub.payaccess.api.models.request.TerminalSearchFilterRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.sun.mail.smtp.SMTPTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

@Service
public class EmailService {

    private EmailDao emailDao;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public EmailService(EmailDao emailDao){

        this.emailDao = emailDao;
    }


    public EmailDocument createEmailDocument(CreateEmailDocumentRequest createEmailDocumentRequest, User authenticatedUser, EmailDocumentPriorityLevel emailDocumentPriorityLevel) {
        EmailDocument emailDocument = emailDao.saveEmailDocument(createEmailDocumentRequest.getHtmlMessage(),
                createEmailDocumentRequest.getRecipients(),
                createEmailDocumentRequest.getAttachmentList(),
                createEmailDocumentRequest.getSubject(),
                authenticatedUser,
                emailDocumentPriorityLevel);
        return emailDocument;
    }


    public EmailDocument getEmailDocument(Long emailDocumentId, Long merchantId) {

        Optional<EmailDocument> optionalTerminal = emailDao.get(emailDocumentId, merchantId);
        EmailDocument emailDocument = null;
        if(optionalTerminal.isPresent())
            emailDocument = optionalTerminal.get();

        return emailDocument;

    }

    public EmailDocument updateEmailDocument(EmailDocument emailDocument)
    {
        return emailDao.update(emailDocument);
    }

    public ResponseEntity getEmailDocuments(Integer pageNumber, Integer pageSize) {
        if(pageNumber==null)
            pageNumber = 0;

        Map queryResponse = emailDao.getAll(pageNumber, pageSize, null);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setResponseObject(queryResponse);
        if(queryResponse!=null)
        {
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Email Documents fetched successfully");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("Email Documents listing fetch failed");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);

    }


    public ResponseEntity getEmailDocumentsByPriorityLevel(Integer pageNumber, Integer pageSize, EmailDocumentPriorityLevel emailDocumentPriorityLevel) {
        if(pageNumber==null)
            pageNumber = 0;

        Map queryResponse = emailDao.getAllByPriorityLevel(pageNumber, pageSize, null, emailDocumentPriorityLevel);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setResponseObject(queryResponse);
        if(queryResponse!=null)
        {
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setResponseObject(queryResponse);
            payAccessResponse.setMessage("Email Documents fetched successfully");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("Email Documents listing fetch failed");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);

    }




//    @Scheduled(fixedDelayString = "${email.sender.lower:30000}")
    public void sendEmailsAtLowLevel() {
        ResponseEntity responseEntity = this.getEmailDocumentsByPriorityLevel(0, 50, EmailDocumentPriorityLevel.LOW);
        PayAccessResponse payAccessResponse = (PayAccessResponse)responseEntity.getBody();
        logger.info("...{}...{}", payAccessResponse.getResponseObject(), payAccessResponse.getStatusCode());
        if(payAccessResponse.getStatusCode().equals(PayAccessStatusCode.SUCCESS.label))
        {
            logger.info("...xyz");
            Map responseObject = (Map)payAccessResponse.getResponseObject();
            List<EmailDocument> docList = (List<EmailDocument>)responseObject.get("list");
            logger.info("...{}.....{}", docList, docList.size());
            docList.stream().map((EmailDocument d) -> {
                logger.info("{}", d.getId());
                try {
                    logger.info("=========================");
                    Properties props = System.getProperties();
                    props.put("mail.smtps.host", "smtp.mailgun.org");
                    props.put("mail.smtps.auth", "true");

                    Session session = Session.getInstance(props, null);
                    Message msg = new MimeMessage(session);
                    msg.setFrom(new InternetAddress("emailer@payaccess.com", "PayAccess"));

                    InternetAddress[] addrs = InternetAddress.parse(d.getRecipients(), false);
                    msg.setRecipients(Message.RecipientType.TO, addrs);

                    msg.setSubject("Welcome to PayAccess");
                    msg.setContent(d.getHtmlMessage(), "text/html; charset=utf-8");

                    //msg.setText("Copy the url and paste in your browser to activate your account - http://137.184.47.182:8081/payaccess/api/v1/user/activate-account/"+user.getEmailAddress()+"/" + verificationLink +" - providing the OTP: " + otp);

                    msg.setSentDate(new Date());

                    SMTPTransport t =
                            (SMTPTransport) session.getTransport("smtps");
                    t.connect("smtp.mailgun.org", "postmaster@mails.valuenaira.com", "k0l01qaz!QAZ");
                    t.sendMessage(msg, msg.getAllRecipients());

                    logger.info("Response: {}" , t.getLastServerResponse());

                    t.close();

                    d.setEmailDocumentStatus(EmailDocumentStatus.SENT);
                    this.updateEmailDocument(d);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    logger.error("Error Sending Mail ...{}", e);
                }

                return null;
            });
        }
    }


}
