package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
import com.jamub.payaccess.api.dao.util.RowMapper;
import com.jamub.payaccess.api.enums.MerchantStatus;
import com.jamub.payaccess.api.enums.UserStatus;
import com.jamub.payaccess.api.models.Customer;
import com.jamub.payaccess.api.models.Merchant;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserDao implements Dao<User>{

    JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall getUserByEmailAddress;

    private SimpleJdbcCall getUserById;
    private SimpleJdbcCall getCustomers;
    private SimpleJdbcCall handleUpdateUserPin;
    private SimpleJdbcCall getUserByUsernameAndPassword;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public void setDataSource(DataSource ds)
    {
        this.jdbcTemplate = new JdbcTemplate(ds);

        getUserByEmailAddress = new SimpleJdbcCall(jdbcTemplate)
//                .withFunctionName("GetUserByEmailAddress")
                .withProcedureName("GetUserByEmailAddress")
                .returningResultSet("#result-set-1",
                        RowMapper.newInstance(User.class));
        handleUpdateUserPin = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateUserPin")
                .returningResultSet("#result-set-1",
                        RowMapper.newInstance(User.class));
        getUserByUsernameAndPassword = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetUserByUsernameAndPassword")
                .returningResultSet("#result-set-1",
                        RowMapper.newInstance(User.class));
        getUserById = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetUserById")
                .returningResultSet("#result-set-1",
                        RowMapper.newInstance(User.class));
    }

    @Override
    public Optional<User> get(Long id) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("userId", id);
        Map<String, Object> m = getUserById.execute(in);
        logger.info("{}", m);
        List<User> result = (List<User>) m.get("#result-set-1");
        User us = result.size()>0 ? result.get(0): null;
        return Optional.of(us);
    }

    @Override
    public List<User> getAll() {
        MapSqlParameterSource in = new MapSqlParameterSource();
        Map<String, Object> m = getCustomers.execute(in);

        List<User> result = (List<User>) m.get("#result-set-1");
        return result;
    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(User user) {

    }


    public List<User> getUserByEmailAddress(String emailAddress) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailAddress", emailAddress)
                .addValue("emailAddress", emailAddress);
        Map<String, Object> m = getUserByEmailAddress.execute(in);
        logger.info("{}", m);
        List<User> result = (List<User>) m.get("#result-set-1");

        return result;
    }


    public List<User> getUserByUsernameAndPassword(String username)
//    , String password
    {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("username", username);
//                .addValue("password", password)
        Map<String, Object> m = getUserByUsernameAndPassword.execute(in);
        logger.info("{}", m);
        List<User> result = (List<User>) m.get("#result-set-1");

        return result;
    }

    public User updateUserPin(CustomerPinUpdateRequest customerBioDataUpdateRequest, User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailAddress", authenticatedUser.getEmailAddress())
                .addValue("epinHash", customerBioDataUpdateRequest.getPin());
        Map<String, Object> m = handleUpdateUserPin.execute(in);
        List<User> result = (List<User>) m.get("#result-set-1");
        User user = result!=null && !result.isEmpty() ? result.get(0) : null;
        return user;
    }
}
