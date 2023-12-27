package com.jamub.payaccess.api.services;

import com.jamub.payaccess.api.models.User;

import java.util.List;


public interface LoginService {
    User save(User user);
    List<User> findAll();
    User findOne(String username);
}
