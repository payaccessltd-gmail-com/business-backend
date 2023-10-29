package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.dao.util.UtilityHelper;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.MerchantService;
import com.jamub.payaccess.api.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
@RequestMapping("/api/v1/support")
public class SupportController {


    @Autowired
    TokenService tokenService;

    @Autowired
    MerchantService merchantService;

    @Value("${path.uploads.identification_documents}")
    private String fileDestinationPath;


    @CrossOrigin
    @RequestMapping(value = "/create-contact-us-message", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse createContactUsMessage(CreateContactUsRequest createContactUsRequest,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) throws JsonProcessingException {

        User authenticatedUser = tokenService.getUserFromToken(request);

        MultipartFile businessLogoFile = createContactUsRequest.getBusinessLogoFile();
        if(!businessLogoFile.isEmpty())
        {
            try {
                String newFileName = UtilityHelper.uploadFile(businessLogoFile, fileDestinationPath);

                PayAccessResponse payAccessResponse = merchantService.createContactUsMessage(createContactUsRequest.getEmailAddress(),
                        createContactUsRequest.getSubject(),
                        createContactUsRequest.getProductCategory(),
                        createContactUsRequest.getDescription(),
                        createContactUsRequest.getUrgency(),
                        newFileName, authenticatedUser);

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
    @RequestMapping(value = "/create-feedback-message", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PayAccessResponse createFeedbackMessage(@RequestBody CreateFeedbackRequest createFeedbackRequest,
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

        PayAccessResponse payAccessResponse = merchantService.createFeedbackMessage(createFeedbackRequest.getEmailAddress(),
                createFeedbackRequest.getTitle(),
                createFeedbackRequest.getProductCategory(),
                createFeedbackRequest.getDescription(),
                createFeedbackRequest.getUrgency(), authenticatedUser);

        return payAccessResponse;

    }
}
