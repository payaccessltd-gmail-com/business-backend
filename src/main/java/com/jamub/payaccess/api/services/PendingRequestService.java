package com.jamub.payaccess.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.enums.PendingRequestModule;
import com.jamub.payaccess.api.enums.PendingRequestStatus;
import com.jamub.payaccess.api.enums.PendingRequestType;
import com.jamub.payaccess.api.models.PendingRequest;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.PortalUserDetail;
import com.jamub.payaccess.api.models.response.PendingRequestResponse;
import com.jamub.payaccess.api.repository.PendingRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
public class PendingRequestService {

    @Autowired
    private PendingRequestRepository pendingRequestRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public PendingRequestResponse saveRequest(Object request, PortalUserDetail param, PendingRequestModule module) {
        PendingRequestResponse response= new PendingRequestResponse(PayAccessStatusCode.GENERAL_ERROR.label,PayAccessStatusCode.GENERAL_ERROR.name());
        PendingRequest pr = new PendingRequest();

        ObjectMapper om = new ObjectMapper();
        String requestData = "";
        try {
            requestData = om.writeValueAsString(request);
            if(Objects.isNull(requestData)) {
                return response;
            }
            pr.setRequestDate(param.getRequestDate());
            pr.setRequestBy(param.getUser());
            pr.setAdditionalInfo(requestData);
            pr.setRequestType(param.getRequestType());
            pr.setDescription(param.getDescription());
            pr.setStatus(param.getStatus());
            pr.setModule(module);

            pendingRequestRepository.save(pr);
            logger.info("pending Request saved");

            return new PendingRequestResponse(PayAccessStatusCode.SUCCESS.label,PayAccessStatusCode.SUCCESS.name());
        } catch (Exception e) {
            e.printStackTrace();
            return response;
        }

    }

    public PendingRequestResponse approveRequest(long id, String requestType, User user) {
        PendingRequestResponse response= new PendingRequestResponse(PayAccessStatusCode.GENERAL_ERROR.label,PayAccessStatusCode.GENERAL_ERROR.name());

        try {
            Optional<PendingRequest> pendingRequestDb = pendingRequestRepository.findById(id);
            if(!pendingRequestDb.isPresent()) {
                return response;
            }
            PendingRequest pendingRequest = pendingRequestDb.get();
            if(!PendingRequestType.valueOf(requestType).equals(pendingRequest.getStatus())) {
                return new PendingRequestResponse(PayAccessStatusCode.GENERAL_ERROR.label, "Invalid request type");
            }
            if(!pendingRequest.getStatus().equals(PendingRequestStatus.PENDING)) {
                return new PendingRequestResponse(PayAccessStatusCode.GENERAL_ERROR.label, "Request has already been worked on");
            }

            pendingRequest.setStatus(PendingRequestStatus.APPROVED);
            pendingRequest.setActionOn(new Date());
            pendingRequest.setActionBy(user);
            pendingRequest.setAuthorizer(user);

            pendingRequestRepository.save(pendingRequest);
            logger.info("Pending Request Approved");

            return new PendingRequestResponse(PayAccessStatusCode.SUCCESS.label,PayAccessStatusCode.SUCCESS.name());
        } catch (Exception e) {
            e.printStackTrace();
            return response;
        }
    }

    public PendingRequestResponse declineRequest(long id, String requestType, User user) {
        PendingRequestResponse response= new PendingRequestResponse(PayAccessStatusCode.GENERAL_ERROR.label,PayAccessStatusCode.GENERAL_ERROR.name());

        try {
            Optional<PendingRequest> pendingRequestDb = pendingRequestRepository.findById(id);
            if(!pendingRequestDb.isPresent()) {
                return response;
            }
            PendingRequest pendingRequest = pendingRequestDb.get();
            if(!PendingRequestType.valueOf(requestType).equals(pendingRequest.getStatus())) {
                return new PendingRequestResponse(PayAccessStatusCode.GENERAL_ERROR.label, "Invalid request type");
            }
            if(!pendingRequest.getStatus().equals(PendingRequestStatus.PENDING)) {
                return new PendingRequestResponse(PayAccessStatusCode.GENERAL_ERROR.label, "Request has already been worked on");
            }

            pendingRequest.setStatus(PendingRequestStatus.REJECTED);
            pendingRequest.setActionOn(new Date());
            pendingRequest.setActionBy(user);
            pendingRequest.setAuthorizer(user);

            pendingRequestRepository.save(pendingRequest);
            logger.info("Pending Request Declined");

            return new PendingRequestResponse(PayAccessStatusCode.SUCCESS.label,PayAccessStatusCode.SUCCESS.name());
        } catch (Exception e) {
            e.printStackTrace();
            return response;
        }
    }
}
