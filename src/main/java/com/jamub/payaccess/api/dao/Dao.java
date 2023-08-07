package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.models.request.BaseRequest;
import com.jamub.payaccess.api.models.request.MerchantSignUpRequest;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {

    Optional<T> get(Long id);
    List<T> getAll();
//    T save(MerchantSignUpRequest t);
    void update(T t);
    void delete(T t);
}
