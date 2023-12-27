package com.jamub.payaccess.api.services;

import com.jamub.payaccess.api.dao.UserDao;
import com.jamub.payaccess.api.enums.UserRole;
import com.jamub.payaccess.api.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service(value = "loginService")
public class LoginServiceImpl implements UserDetailsService, LoginService{

    private UserDao userDao;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public LoginServiceImpl(UserDao userDao){
        this.userDao = userDao;
//        this.authenticationManager = authenticationManager;
    }


    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public User findOne(String username) {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        List<User> userList = userDao.getUserByEmailAddress(username);
        logger.info("{} size", userList.size());
        User user = userList!=null && !userList.isEmpty() ? userList.get(0) : null;
        logger.info("{}", user.getPassword());
        org.springframework.security.core.userdetails.User us = new org.springframework.security.core.userdetails.User(user.getEmailAddress(), "password", getAuthority(user));

        logger.info("{}", us.getAuthorities());
        return us;
    }


    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + UserRole.ADMINISTRATOR));
        return authorities;
    }
}
