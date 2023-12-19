package com.jamub.payaccess.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamub.payaccess.api.dao.util.UtilityHelper;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.ErrorMessage;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.*;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.MerchantService;
import com.jamub.payaccess.api.services.TokenService;
import io.swagger.annotations.*;
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
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/support")
@Api(produces = "application/json", value = "Operations pertaining to Support")
public class SupportController {


    @Autowired
    TokenService tokenService;

    @Autowired
    MerchantService merchantService;

    @Value("${path.uploads.identification_documents}")
    private String fileDestinationPath;


    @CrossOrigin
    //CREATE_CONTACT_US_MESSAGE
    @RequestMapping(value = "/create-contact-us-message", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Create contact us messages", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity createContactUsMessage(CreateContactUsRequest createContactUsRequest,
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


        MultipartFile businessLogoFile = createContactUsRequest.getBusinessLogoFile();
        if(businessLogoFile==null)
        {
            PayAccessResponse payAccessResponse = new  PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
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
                    return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);
                }
                String newFileName = UtilityHelper.uploadFile(businessLogoFile, fileDestinationPath);

                return merchantService.createContactUsMessage(createContactUsRequest.getEmailAddress(),
                        createContactUsRequest.getSubject(),
                        createContactUsRequest.getProductCategory(),
                        createContactUsRequest.getDescription(),
                        createContactUsRequest.getUrgency(),
                        newFileName, authenticatedUser);
            }
            catch(IOException e)
            {
                e.printStackTrace();

                PayAccessResponse payAccessResponse = new  PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
                payAccessResponse.setMessage("Message could not be sent. Resource access denied");
                payAccessResponse.setResponseObject(e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payAccessResponse);
            }

        }

        PayAccessResponse payAccessResponse = new  PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.AUTHORIZATION_FAILED.label);
        payAccessResponse.setMessage("Message could not be sent. Ensure you select a valid file");
        return ResponseEntity.status(HttpStatus.OK).body(payAccessResponse);




    }


    @CrossOrigin
    //CREATE_FEEDBACK_MESSAGE
    @RequestMapping(value = "/create-feedback-message", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", required = true, paramType = "header", dataTypeClass = String.class, example = "Bearer <Token>")
    @ApiOperation(value = "Create Feedback messages", response = ResponseEntity.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 400, message = "Validation of request parameters failed"),
            @ApiResponse(code = 403, message = "Access to API denied due to invalid token"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public ResponseEntity createFeedbackMessage(@RequestBody @Valid CreateFeedbackRequest createFeedbackRequest,
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

        return merchantService.createFeedbackMessage(createFeedbackRequest.getEmailAddress(),
                createFeedbackRequest.getTitle(),
                createFeedbackRequest.getProductCategory(),
                createFeedbackRequest.getDescription(),
                createFeedbackRequest.getUrgency(), authenticatedUser);

    }



}
