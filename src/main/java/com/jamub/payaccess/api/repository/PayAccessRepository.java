package com.jamub.payaccess.api.repository;

import com.jamub.payaccess.api.models.User;

import java.util.List;

public interface PayAccessRepository<T, Z> {


    T save(Z z);

    T update(Z z);

    T findById(Long id);

    void deleteById(Long id);

    List<T> findAll();

    List<User> getUserByEmailAddress(Z z);
}
