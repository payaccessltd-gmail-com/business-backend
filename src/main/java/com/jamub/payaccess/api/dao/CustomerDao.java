package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
import com.jamub.payaccess.api.dao.util.RowMapper;
import com.jamub.payaccess.api.enums.CustomerStatus;
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
public class CustomerDao implements Dao<Customer>{

    JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall saveCustomer;
    private SimpleJdbcCall handleActivateAccount;
    private SimpleJdbcCall getCustomers;
    private SimpleJdbcCall handleUpdateCustomerBioData;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public void setDataSource(DataSource ds)
    {
        this.jdbcTemplate = new JdbcTemplate(ds);

        saveCustomer = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("CreateNewCustomer")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Customer.class));

        handleActivateAccount = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("ActivateAccountForCustomer")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Customer.class))
                .returningResultSet("#result-set-2",
                        MerchantRowMapper.newInstance(User.class));
        getCustomers = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetCustomers")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Customer.class));
        handleUpdateCustomerBioData = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateCustomerBioData")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Customer.class));
    }

    @Override
    public Optional<Customer> get(int id) {
        return Optional.empty();
    }

    @Override
    public List<Customer> getAll() {
        MapSqlParameterSource in = new MapSqlParameterSource();
        Map<String, Object> m = getCustomers.execute(in);

        List<Customer> result = (List<Customer>) m.get("#result-set-1");
        return result;
    }

    @Override
    public void update(Customer customer) {

    }

    @Override
    public void delete(Customer customer) {

    }

    //    @Override
    public Customer save(CustomerSignUpRequest customerSignUpRequest) {

//        logger.info("merchantSignUpRequest.isSoftwareDeveloper()...{}", merchantSignUpRequest.isSoftwareDeveloper());
        String otp = RandomStringUtils.randomNumeric(4);
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("mobileNumber", customerSignUpRequest.getMobileNumber())
                .addValue("emailAddress", customerSignUpRequest.getEmailAddress())
                .addValue("password", customerSignUpRequest.getPassword())
                .addValue("otp", otp)
                .addValue("userStatus", UserStatus.NOT_ACTIVATED.name())
                .addValue("customerStatus", CustomerStatus.IN_PROGRESS.name());

        Map<String, Object> m = saveCustomer.execute(in);
        logger.info("{}", m);
        List<Customer> result = (List<Customer>) m.get("#result-set-1");
        return result.get(0);
    }


//    @Override
    public void update(Merchant merchant) {

    }

//    @Override
    public void delete(Merchant merchant) {

    }


    public Map<String, Object> activateAccount(String emailAddress, String verificationLink) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("verificationLink", verificationLink)
                .addValue("emailAddress", emailAddress);
        Map<String, Object> m = handleActivateAccount.execute(in);

        return m;
    }


    public Customer updateCustomerBioData(CustomerBioDataUpdateRequest customerBioDataUpdateRequest, User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailAddress", authenticatedUser.getEmailAddress())
                .addValue("firstName", customerBioDataUpdateRequest.getFirstName())
                .addValue("lastName", customerBioDataUpdateRequest.getLastName())
                .addValue("gender", customerBioDataUpdateRequest.getGender())
                .addValue("dateOfBirth", customerBioDataUpdateRequest.getDateOfBirth())
                .addValue("country", customerBioDataUpdateRequest.getCountry())
                .addValue("state", customerBioDataUpdateRequest.getState())
                .addValue("city", customerBioDataUpdateRequest.getCity())
                .addValue("address", customerBioDataUpdateRequest.getAddress());
        Map<String, Object> m = handleUpdateCustomerBioData.execute(in);
        List<Customer> result = (List<Customer>) m.get("#result-set-1");
        Customer customer = result!=null && !result.isEmpty() ? result.get(0) : null;
        return customer;
    }

}
