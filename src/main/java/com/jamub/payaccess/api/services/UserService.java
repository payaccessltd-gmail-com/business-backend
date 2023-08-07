package com.jamub.payaccess.api.services;

import com.jamub.payaccess.api.dao.TransactionDao;
import com.jamub.payaccess.api.dao.UserDao;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.models.Transaction;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.DisabledException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserDao userDao;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserService(UserDao userDao){
        this.userDao = userDao;
//        this.authenticationManager = authenticationManager;
    }


    public User getUserById(Long userId) {

        Optional<User> userOptional = userDao.get(userId);

        if(userOptional.isPresent())
            return userOptional.get();

        return null;
    }

//    private void authenticate(String username, String password) throws Exception {
//        try {
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//        } catch (DisabledException e) {
//            throw new Exception("USER_DISABLED", e);
//        } catch (BadCredentialsException e) {
//            throw new Exception("INVALID_CREDENTIALS", e);
//        }
//    }

}
