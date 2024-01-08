package com.jamub.payaccess.api.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@ResponseBody

/**
 * Class for formatting exceptions
 */
public class PayAccessExceptionHandler implements AccessDeniedHandler{

    private Logger logger = LoggerFactory.getLogger(this.getClass());



    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<PayAccessResponse> resourceAccessException(Exception ex, WebRequest request) {
        ex.printStackTrace();
        logger.error("{}", ex.getMessage());
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("API error encountered.");
        payAccessResponse.setResponseObject(ex.getMessage());
        return new ResponseEntity<PayAccessResponse>(payAccessResponse, HttpStatus.REQUEST_TIMEOUT);
    }



    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<PayAccessResponse> timeoutException(Exception ex, WebRequest request) {
        ex.printStackTrace();
        logger.error("{}", ex.getMessage());
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage(ex.getMessage());
        payAccessResponse.setResponseObject(ex.getMessage());
        return new ResponseEntity<PayAccessResponse>(payAccessResponse, HttpStatus.GATEWAY_TIMEOUT);
    }


    @ExceptionHandler(Throwable.class)
    public ResponseEntity<PayAccessResponse> validationException(Exception ex, WebRequest request) {
        ex.printStackTrace();
        logger.error("{}", ex.getMessage());
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("API error encountered.");
        payAccessResponse.setResponseObject(ex.getMessage());
        return new ResponseEntity<PayAccessResponse>(payAccessResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    @ExceptionHandler(PayAccessAuthException.class)
    public ResponseEntity<PayAccessResponse> payAccessExceptionHandler(Exception ex, WebRequest request) {
        ex.printStackTrace();
        logger.error("{}", ex.getMessage());
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("API error encountered.");
        payAccessResponse.setResponseObject(ex.getMessage());
        return new ResponseEntity<PayAccessResponse>(payAccessResponse, HttpStatus.UNAUTHORIZED);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public PayAccessResponse handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.INCOMPLETE_REQUEST.label);
        payAccessResponse.setMessage("Request validation error encountered.");
        payAccessResponse.setResponseObject(errors);

        return payAccessResponse;
    }


    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public PayAccessResponse commence(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) throws IOException {
        Map<String, String> errors = new HashMap<>();


        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.ACCESS_LEVELS_INSUFFICIENT.label);
        payAccessResponse.setMessage("Access to resource denied.");
        payAccessResponse.setResponseObject(errors);

        return payAccessResponse;
    }


//
//
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    @ExceptionHandler(HttpClientErrorException.class)
//    public PayAccessResponse commence(HttpServletRequest request, HttpServletResponse response, HttpClientErrorException ex) throws IOException {
//        Map<String, String> errors = new HashMap<>();
//
//
//        PayAccessResponse payAccessResponse = new PayAccessResponse();
//        payAccessResponse.setStatusCode(PayAccessStatusCode.ACCESS_LEVELS_INSUFFICIENT.label);
//        payAccessResponse.setMessage(ex.getResponseBodyAsString());
//        payAccessResponse.setResponseObject(errors);
//
//        return payAccessResponse;
//    }



    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Map<String, String> errors = new HashMap<>();


        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.ACCESS_LEVELS_INSUFFICIENT.label);
        payAccessResponse.setMessage("Access to resource denied.");
        payAccessResponse.setResponseObject(errors);

        response.getWriter().write(new ObjectMapper().writeValueAsString(payAccessResponse));
    }



}
