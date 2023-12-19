package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.models.request.BaseRequest;
import com.jamub.payaccess.api.models.request.MerchantSignUpRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Dao<T> {

    Optional<T> get(Long id);
    Map getAll();
//    T save(MerchantSignUpRequest t);
    T update(T t);
    void delete(T t);
}
