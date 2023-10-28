package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
import com.jamub.payaccess.api.dao.util.RowMapper;
import com.jamub.payaccess.api.dao.util.UtilityHelper;
import com.jamub.payaccess.api.enums.CustomerStatus;
import com.jamub.payaccess.api.enums.MerchantStatus;
import com.jamub.payaccess.api.enums.UserStatus;
import com.jamub.payaccess.api.models.Customer;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private SimpleJdbcCall handleSaveUser;
    private SimpleJdbcCall handleActivateAccount;
    private SimpleJdbcCall handleGetUsers;
    private SimpleJdbcCall handleUpdateUserOtp;
    private SimpleJdbcCall handleUpdateUserForgotPasswordLink;
    private SimpleJdbcCall handleUpdateUserPassword;
    private SimpleJdbcCall handleRecoverUserPassword;
    private SimpleJdbcCall handleUpdateUser;

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
        handleSaveUser = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SaveUser")
                .returningResultSet("#result-set-1",
                        RowMapper.newInstance(User.class));
        handleUpdateUserOtp = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateUserOtp")
                .returningResultSet("#result-set-1",
                        RowMapper.newInstance(User.class));
        handleActivateAccount = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("ActivateAccount")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(User.class));
        handleGetUsers = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetUsers")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(User.class));
        handleUpdateUserForgotPasswordLink = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateUserForgotPasswordLink")
                .returningResultSet("#result-set-1",
                        RowMapper.newInstance(User.class));
        handleRecoverUserPassword = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("RecoverUserPassword")
                .returningResultSet("#result-set-1",
                        RowMapper.newInstance(User.class));
        handleUpdateUserPassword = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateUserPassword")
                .returningResultSet("#result-set-1",
                        RowMapper.newInstance(User.class));
        handleUpdateUser = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateUser")
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
    public User update(User user) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("userId", user.getId())
                .addValue("emailAddress", user.getEmailAddress())
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("password", user.getPassword())
                .addValue("verificationLink", user.getVerificationLink())
                .addValue("userStatus", user.getUserStatus())
                .addValue("primaryBusinessName", user.getPrimaryBusinessName())
                .addValue("otp", user.getOtp())
                .addValue("otpExpiryDate", user.getOtpExpiryDate())
                .addValue("country", user.getCountry())
                .addValue("dateOfBirth", user.getDateOfBirth())
                .addValue("gender", user.getGender())
                .addValue("forgotPasswordLink", user.getForgotPasswordLink())
                .addValue("mobileNumber", user.getMobileNumber())
                .addValue("softwareDeveloper", user.getSoftwareDeveloper())
                .addValue("identificationDocument", user.getIdentificationDocument())
                .addValue("identificationDocumentPath", user.getIdentificationDocumentPath())
                .addValue("identificationNumber", user.getIdentificationNumber())
                .addValue("primaryMerchantId", user.getPrimaryMerchantId());

        Map<String, Object> m = handleUpdateUser.execute(in);
        logger.info("{}", m);
        List<User> result = (List<User>) m.get("#result-set-1");
        return result.get(0);
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






    public User save(UserSignUpRequest userSignUpRequest, String otp, String verificationLink, LocalDateTime otpExpiryDate,
                     String secretKey, String publicKey, String secretKeyLive, String publicKeyLive, String merchantCode) {
        String bcryptPassword = UtilityHelper.generateBCryptPassword(userSignUpRequest.getPassword());
//        logger.info("merchantSignUpRequest.isSoftwareDeveloper()...{}", merchantSignUpRequest.isSoftwareDeveloper());
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailAddress", userSignUpRequest.getEmailAddress())
                .addValue("firstName", userSignUpRequest.getFirstName())
                .addValue("lastName", userSignUpRequest.getLastName())
                .addValue("password", bcryptPassword)
                .addValue("verificationLink", verificationLink)
                .addValue("userStatus", UserStatus.NOT_ACTIVATED.name())
                .addValue("businessName", userSignUpRequest.getBusinessName())
                .addValue("otp", otp)
                .addValue("otpExpiryDate", otpExpiryDate)
                .addValue("secretKey", secretKey)
                .addValue("publicKey", publicKey)
                .addValue("secretKeyLive", secretKeyLive)
                .addValue("publicKeyLive", publicKeyLive)
                .addValue("merchantCode", merchantCode);

        Map<String, Object> m = handleSaveUser.execute(in);
        logger.info("{}", m);
        List<User> result = (List<User>) m.get("#result-set-1");
        return result.get(0);
    }


    public Map<String, Object> activateAccount(String emailAddress, String verificationLink, String otp) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("verificationLink", verificationLink)
                .addValue("emailAddress", emailAddress)
                .addValue("otp", otp);
        Map<String, Object> m = handleActivateAccount.execute(in);

        return m;
    }


    public List<User> getUsers(int pageNumber, int maxSize)
    {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber", pageNumber*maxSize, Types.INTEGER)
                .addValue("pageSize", maxSize, Types.INTEGER);
        logger.info("{}", in.getValues());
        logger.info("pageNumber..{}", pageNumber);
        logger.info("maxSize..{}", maxSize);
        Map<String, Object> m = handleGetUsers.execute(in);
        logger.info("{}", m);
        List<User> userList = (ArrayList<User>)( m.get("#result-set-1"));
        return userList;
    }





    public User updateUserForgotPasswordLink(String emailAddress, String forgotPasswordLink) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailAddress", emailAddress)
                .addValue("forgotPasswordLink", forgotPasswordLink);
        Map<String, Object> m = handleUpdateUserForgotPasswordLink.execute(in);
        List<User> result = (List<User>) m.get("#result-set-1");
        User user = result!=null && !result.isEmpty() ? result.get(0) : null;
        return user;
    }


    public User handleRecoverUserPassword(String emailAddress, String forgotPasswordLink, String newPassword) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailAddress", emailAddress)
                .addValue("newPassword", newPassword)
                .addValue("forgotPasswordLink", forgotPasswordLink);
        Map<String, Object> m = handleRecoverUserPassword.execute(in);
        List<User> result = (List<User>) m.get("#result-set-1");
        User user = result!=null && !result.isEmpty() ? result.get(0) : null;
        return user;
    }

    public User updateUserPassword(String password, String newPassword, Long userId) {
        newPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("newPassword", newPassword);
        Map<String, Object> m = handleUpdateUserPassword.execute(in);
        List<User> result = (List<User>) m.get("#result-set-1");
        User user = result!=null && !result.isEmpty() ? result.get(0) : null;
        return user;
    }
}
