package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.models.MerchantSignUpRequest;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface Dao<T> {

    Optional<T> get(int id);
    List<T> getAll();
    T save(MerchantSignUpRequest t);
    void update(T t);
    void delete(T t);
}
