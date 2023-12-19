package com.jamub.payaccess.api.services;

import com.jamub.payaccess.api.dao.AuditTrailDao;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.AuditTrail;
import com.jamub.payaccess.api.models.AuditTrail;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.AuditTrailFilterRequest;
import com.jamub.payaccess.api.models.request.GetMerchantFilterRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AuditTrailService {

    private AuditTrailDao auditTrailDao;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AuditTrailService(AuditTrailDao auditTrailDao){

        this.auditTrailDao = auditTrailDao;
    }


    public AuditTrail getAuditTrail(Long auditTrailId) {

        Optional<AuditTrail> optionalAuditTrail = auditTrailDao.get(auditTrailId);
        AuditTrail auditTrail = null;
        if(optionalAuditTrail.isPresent())
            auditTrail = optionalAuditTrail.get();

        return auditTrail;

    }
    
    public ResponseEntity getAuditTrails(Integer pageNumber, Integer pageSize, AuditTrailFilterRequest auditTrailFilterRequest) {
        if(pageNumber==null)
            pageNumber = 0;

        Map queryResponse = auditTrailDao.getAll(pageNumber, pageSize, auditTrailFilterRequest);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setResponseObject(queryResponse);
        if(queryResponse!=null)
        {
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("AuditTrails fetched successfully");
            return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
        }
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("AuditTrail listing fetch failed");
        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);

    }

}
