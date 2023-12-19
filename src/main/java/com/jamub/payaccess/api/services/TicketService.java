package com.jamub.payaccess.api.services;

import com.jamub.payaccess.api.dao.BankDao;
import com.jamub.payaccess.api.dao.TicketDao;
import com.jamub.payaccess.api.dao.util.UtilityHelper;
import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.enums.InvoiceType;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.Bank;
import com.jamub.payaccess.api.models.Merchant;
import com.jamub.payaccess.api.models.TransactionTicket;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.AssignTransactionTicketRequest;
import com.jamub.payaccess.api.models.request.CloseTransactionTicketRequest;
import com.jamub.payaccess.api.models.request.CreateNewInvoiceRequest;
import com.jamub.payaccess.api.models.request.CreateTransactionTicketRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;


@Service
public class TicketService {


    @Autowired
    private TicketDao ticketDao;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${is.live}")
    private int isLive;

    @Value("${path.uploads.invoice_logos}")
    private String fileDestinationPath;




    @Autowired
    public TicketService(TicketDao ticketDao){
        this.ticketDao = ticketDao;
    }


    public ResponseEntity createTransactionTicket(CreateTransactionTicketRequest createTransactionTicketRequest, User authenticatedUser,
                                                  Long actorId, String ipAddress, String description,
                                        ApplicationAction userAction, String carriedOutByUserFullName, String objectClassReference,
                                        Long objectIdReference) {

        MultipartFile attachmentImage = createTransactionTicketRequest.getAttachmentImage();
        String newFileName = null;
        if(!attachmentImage.isEmpty())
        {
            try {
                if(UtilityHelper.checkIfImage(attachmentImage)==false)
                {
                    PayAccessResponse payAccessResponse = new  PayAccessResponse();
                    payAccessResponse.setStatusCode(PayAccessStatusCode.INVALID_FILE_TYPE.label);
                    payAccessResponse.setMessage("Ensure you select a valid image file as your business logo");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
                }
                newFileName = UtilityHelper.uploadFile(attachmentImage, fileDestinationPath);


            }
            catch(IOException e)
            {
                e.printStackTrace();
                PayAccessResponse payAccessResponse = new PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
                payAccessResponse.setMessage("Invoice could not be created. Resource denial error");
                payAccessResponse.setResponseObject(e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
            }

        }

//        Transaction transaction =
        TransactionTicket transactionTicket = this.ticketDao.createNewTransactionTicket(
                createTransactionTicketRequest.getIssueCategory(),
                createTransactionTicketRequest.getOrderRef(),
                createTransactionTicketRequest.getTicketMessage(),
                newFileName, actorId, ipAddress, description,
                userAction, carriedOutByUserFullName, objectClassReference,
                objectIdReference);

        if(transactionTicket!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setResponseObject(transactionTicket);
            payAccessResponse.setMessage("A ticket has been raised for the transaction");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("A ticket could not be raised for the transaction");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);


    }


    public TransactionTicket getTransactionTicketByTicketNumber(String ticketNumber) {
        return this.ticketDao.getTransactionTicketByTicketNumber(ticketNumber);
    }


    public Map getTransactionTicketByPagination(Integer pageNumber, Integer pageSize, Long merchantId) {
        return this.ticketDao.getTransactionTicketByPagination(pageNumber, pageSize, merchantId);
    }

    public ResponseEntity assignTransactionTicket(User userAssigned, AssignTransactionTicketRequest assignTransactionTicketRequest,
                                                  User authenticatedUser, Long actorId, String ipAddress, String description, ApplicationAction userAction,
                                                  String carriedOutByUserFullName, String objectClassReference, long objectIdReference) {

        TransactionTicket transactionTicket = this.ticketDao.assignTransactionTicket(
                assignTransactionTicketRequest.getTicketNumber(),
                assignTransactionTicketRequest.getAssignToUserId(),
                actorId, ipAddress, description,
                userAction, carriedOutByUserFullName, objectClassReference,
                objectIdReference);

        if(transactionTicket!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setResponseObject(transactionTicket);
            payAccessResponse.setMessage("A ticket has been assigned to the selected administrator");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("A ticket could not be assigned to the selected administrator");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
    }


    public ResponseEntity closeTransactionTicket(User ticketCloserUser, CloseTransactionTicketRequest closeTransactionTicketRequest,
                                                 Long actorId, String ipAddress, String description, ApplicationAction userAction,
                                                 String carriedOutByUserFullName, String objectClassReference, long objectIdReference) {
        TransactionTicket transactionTicket = this.ticketDao.closeTransactionTicket(
                closeTransactionTicketRequest.getTicketNumber(),
                ticketCloserUser.getId(),
                actorId, ipAddress, description,
                userAction, carriedOutByUserFullName, objectClassReference,
                objectIdReference);

        if(transactionTicket!=null)
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setResponseObject(transactionTicket);
            payAccessResponse.setMessage("Ticket has been closed");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("Ticket could not be closed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payAccessResponse);
    }
}
