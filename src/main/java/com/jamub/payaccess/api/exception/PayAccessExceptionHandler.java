package com.jamub.payaccess.api.exception;

import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@ResponseBody

/**
 * Class for formatting exceptions
 */
public class PayAccessExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
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
}
