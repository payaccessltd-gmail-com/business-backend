package com.jamub.payaccess.api.controller;


import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.request.LoginRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.jamub.payaccess.api.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.DisabledException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {



    @Autowired
    private UserService userService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

//    @RequestMapping(value="/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)@RequestBody LoginRequest loginRequest
    @GetMapping("/login")
    public PayAccessResponse createAuthenticationToken() throws Exception {


//        authenticate(loginRequest.getUsername(), loginRequest.getPassword());
//
//        final UserDetails userDetails = userService
//                .loadUserByUsername(loginRequest.getUsername());
//
//        final String token = jwtTokenUtil.generateToken(userDetails);
//
//        PayAccessResponse payAccessResponse = new PayAccessResponse();
//        payAccessResponse.setMessage(token);
//        payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
//
//        return payAccessResponse;
        return null;
    }

    private void authenticate(String username, String password) throws Exception {
//        try {
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//        } catch (DisabledException e) {
//            throw new Exception("USER_DISABLED", e);
//        } catch (BadCredentialsException e) {
//            throw new Exception("INVALID_CREDENTIALS", e);
//        }
    }


}
