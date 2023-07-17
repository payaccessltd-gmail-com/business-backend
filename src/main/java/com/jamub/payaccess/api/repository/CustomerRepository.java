package com.jamub.payaccess.api.repository;

import com.jamub.payaccess.api.enums.CustomerStatus;
import com.jamub.payaccess.api.models.Customer;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.CustomerSignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;


@Repository
public class CustomerRepository implements PayAccessRepository<Customer, CustomerSignUpRequest>{

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public Customer save(CustomerSignUpRequest o) {
        jdbcTemplate.update("INSERT INTO customers (mobile_number, otp, customer_status, user_id) VALUES  (?, ?, ?, ?)",
                new Object[] { o.getMobileNumber(), "1234", CustomerStatus.IN_PROGRESS.name(), 1L });
        return null;
    }

    @Override
    public Customer update(CustomerSignUpRequest o) {
        return null;
    }

    @Override
    public Customer findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
    }

    @Override
    public List findAll() {
        return null;
    }

    @Override
    public List<User> getUserByEmailAddress(CustomerSignUpRequest customerSignUpRequest) {
        try {
            User user = jdbcTemplate.queryForObject("SELECT * FROM users WHERE email_address=?",
                    BeanPropertyRowMapper.newInstance(User.class), customerSignUpRequest.getEmailAddress());

            return Arrays.asList(user);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }
}
