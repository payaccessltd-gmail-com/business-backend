package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
import com.jamub.payaccess.api.dao.util.RowMapper;
import com.jamub.payaccess.api.dao.util.UtilityHelper;
import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.enums.CustomerStatus;
import com.jamub.payaccess.api.enums.MerchantStatus;
import com.jamub.payaccess.api.enums.UserStatus;
import com.jamub.payaccess.api.models.Customer;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.UserRolePermission;
import com.jamub.payaccess.api.models.request.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class UserDao implements Dao<User>{

    JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall getUserByEmailAddress;

    private SimpleJdbcCall getUserById;
    private SimpleJdbcCall getCustomers;
    private SimpleJdbcCall handleUpdateUserPin;
    private SimpleJdbcCall getUserByUsernameAndPassword;
    private SimpleJdbcCall handleSaveUser;
    private SimpleJdbcCall handleSaveAdminUser;
    private SimpleJdbcCall handleUpdateAdminUser;

    private SimpleJdbcCall handleActivateAccount;
    private SimpleJdbcCall handleGetUsers;
    private SimpleJdbcCall handleUpdateUserOtp;
    private SimpleJdbcCall handleUpdateUserForgotPasswordLink;
    private SimpleJdbcCall handleUpdateUserPassword;
    private SimpleJdbcCall handleRecoverUserPassword;
    private SimpleJdbcCall handleRecoverUserPasswordAdmin;
    private SimpleJdbcCall handleSetUserPassword;
    private SimpleJdbcCall handleUpdateUser;
    private SimpleJdbcCall handleUpdateUserStatus;
    private SimpleJdbcCall getPermissionsByRole;

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
        handleSaveAdminUser = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SaveAdminUser")
                .returningResultSet("#result-set-1",
                        RowMapper.newInstance(User.class));
        handleUpdateAdminUser = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateAdminUser")
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
                        MerchantRowMapper.newInstance(User.class))
                .returningResultSet("#result-set-2", new BeanPropertyRowMapper<Integer>()
                {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        return rs.getInt("count");
                    }
                });
        handleUpdateUserForgotPasswordLink = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateUserForgotPasswordLink")
                .returningResultSet("#result-set-1",
                        RowMapper.newInstance(User.class));
        handleRecoverUserPassword = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("RecoverUserPassword")
                .returningResultSet("#result-set-1",
                        RowMapper.newInstance(User.class));
        handleRecoverUserPasswordAdmin = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("RecoverUserPasswordAdmin")
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
        handleSetUserPassword = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SetUserPassword")
                .returningResultSet("#result-set-1",
                        RowMapper.newInstance(User.class));
        handleUpdateUserStatus = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateUserStatus")
                .returningResultSet("#result-set-1",
                        RowMapper.newInstance(User.class));

        getPermissionsByRole = new SimpleJdbcCall(jdbcTemplate)
//                .withFunctionName("GetUserByEmailAddress")
                .withProcedureName("GetUserRolePermissionByRole")
                .returningResultSet("#result-set-1",
                        RowMapper.newInstance(UserRolePermission.class));
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
    public Map getAll() {
        MapSqlParameterSource in = new MapSqlParameterSource();
        Map<String, Object> m = getCustomers.execute(in);

        List<User> result = (List<User>) m.get("#result-set-1");
        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", 100);
        return returnList;
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
        logger.info("{}", emailAddress);
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




    public User saveAdminUser(UserCreateRequest userCreateRequest, String password,
                              String carriedOutByUserFullName, Long actorId, ApplicationAction userAction,
                              String description, String ipAddress, String objectClassReference) {
        String bcryptPassword = UtilityHelper.generateBCryptPassword(password);
//        logger.info("merchantSignUpRequest.isSoftwareDeveloper()...{}", merchantSignUpRequest.isSoftwareDeveloper());
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailAddress", userCreateRequest.getEmailAddress())
                .addValue("firstName", userCreateRequest.getFirstName())
                .addValue("lastName", userCreateRequest.getLastName())
                .addValue("userRole", userCreateRequest.getUserRole().name())
                .addValue("password", bcryptPassword)
                .addValue("userStatus", UserStatus.ACTIVE.name())
                .addValue("carriedOutByUserFullName", carriedOutByUserFullName)
                .addValue("carriedOutByUserId", actorId)
                .addValue("userAction", userAction.name())
                .addValue("description", description)
                .addValue("ipAddress", ipAddress)
                .addValue("objectClassReference", objectClassReference);

        Map<String, Object> m = handleSaveAdminUser.execute(in);
        logger.info("{}", m);
        List<User> result = (List<User>) m.get("#result-set-1");
        return result.get(0);
    }




    public User updateAdminUser(UserCreateRequest userCreateRequest,
                                String carriedOutByUserFullName, Long actorId, ApplicationAction userAction,
                              String description, String ipAddress, String objectClassReference) {
//        logger.info("merchantSignUpRequest.isSoftwareDeveloper()...{}", merchantSignUpRequest.isSoftwareDeveloper());
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailAddress", userCreateRequest.getEmailAddress())
                .addValue("firstName", userCreateRequest.getFirstName())
                .addValue("lastName", userCreateRequest.getLastName())
                .addValue("userRole", userCreateRequest.getUserRole().name())
                .addValue("userId", userCreateRequest.getUserId())
                .addValue("userStatus", UserStatus.ACTIVE.name())
                .addValue("carriedOutByUserFullName", carriedOutByUserFullName)
                .addValue("carriedOutByUserId", actorId)
                .addValue("userAction", userAction.name())
                .addValue("description", description)
                .addValue("ipAddress", ipAddress)
                .addValue("objectClassReference", objectClassReference);
        logger.info("{}", in.getValues());

        Map<String, Object> m = handleUpdateAdminUser.execute(in);
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


    public Map getUsers(int pageNumber, int maxSize, FilterUserRequest filterUserRequest)
    {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber", pageNumber*maxSize, Types.INTEGER)
                .addValue("pageSize", maxSize, Types.INTEGER)
                .addValue("userRole", filterUserRequest.getUserRole())
                .addValue("userStatus", filterUserRequest.getUserStatus())
                .addValue("startDate", filterUserRequest.getStartDate())
                .addValue("endDate", filterUserRequest.getEndDate());
        logger.info("{}", in.getValues());
        logger.info("pageNumber..{}", pageNumber);
        logger.info("maxSize..{}", maxSize);
        Map<String, Object> m = handleGetUsers.execute(in);
        logger.info("{}", m);
        List<User> userList = (ArrayList<User>)( m.get("#result-set-1"));
        List<Integer> userListCount = (List<Integer>) m.get("#result-set-2");

        Map returnList = new HashMap();
        returnList.put("list", userList);
        returnList.put("totalCount", userListCount.get(0));
        return returnList;
    }





    public User updateUserForgotPasswordLink(String emailAddress, String forgotPasswordLink, String otp, LocalDateTime otpExpiryDate) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailAddress", emailAddress)
                .addValue("otp", otp)
                .addValue("otpExpiryDate", otpExpiryDate)
                .addValue("forgotPasswordLink", forgotPasswordLink);
        Map<String, Object> m = handleUpdateUserForgotPasswordLink.execute(in);
        List<User> result = (List<User>) m.get("#result-set-1");
        User user = result!=null && !result.isEmpty() ? result.get(0) : null;
        return user;
    }


    public User handleRecoverUserPassword(String emailAddress, String forgotPasswordLink, String otp) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailAddress", emailAddress)
                .addValue("otp", otp)
                .addValue("forgotPasswordLink", forgotPasswordLink);
        Map<String, Object> m = handleRecoverUserPassword.execute(in);
        List<User> result = (List<User>) m.get("#result-set-1");
        User user = result!=null && !result.isEmpty() ? result.get(0) : null;
        return user;
    }


    public User handleRecoverUserPasswordAdmin(String emailAddress, String forgotPasswordLink, String password) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailAddress", emailAddress)
                .addValue("password", password)
                .addValue("forgotPasswordLink", forgotPasswordLink);
        Map<String, Object> m = handleRecoverUserPasswordAdmin.execute(in);
        List<User> result = (List<User>) m.get("#result-set-1");
        User user = result!=null && !result.isEmpty() ? result.get(0) : null;
        return user;
    }


    public User handleSetUserPassword(String emailAddress, String forgotPasswordLink, String newPassword) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailAddress", emailAddress)
                .addValue("newPassword", newPassword)
                .addValue("forgotPasswordLink", forgotPasswordLink);
        Map<String, Object> m = handleSetUserPassword.execute(in);
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

    public User updateUserStatus(Long userId, UserStatus userStatus, Long actorId, String ipAddress, String description,
                                 ApplicationAction userAction, String carriedOutByUserFullName, String objectClassReference,
                                 Long objectIdReference) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("carriedOutByUserFullName", carriedOutByUserFullName)
                .addValue("userAction", userAction.name())
                .addValue("description", description)
                .addValue("ipAddress", ipAddress)
                .addValue("userId", userId)
                .addValue("objectClassReference", objectClassReference)
                .addValue("objectIdReference", objectIdReference)
                .addValue("userStatus", userStatus.name())
                .addValue("carriedOutByUserId", actorId);
        Map<String, Object> m = handleUpdateUserStatus.execute(in);
        List<User> result = (List<User>) m.get("#result-set-1");
        User user = result!=null && !result.isEmpty() ? result.get(0) : null;
        return user;
    }

    public User createNewUserV2(User loginUser, String bcryptPassword) {
//        String bcryptPassword = UtilityHelper.generateBCryptPassword(password);
//        logger.info("merchantSignUpRequest.isSoftwareDeveloper()...{}", merchantSignUpRequest.isSoftwareDeveloper());
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailAddress", loginUser.getEmailAddress())
                .addValue("firstName", loginUser.getFirstName())
                .addValue("lastName", loginUser.getLastName())
                .addValue("userRole", loginUser.getUserRole().name())
                .addValue("password", bcryptPassword)
                .addValue("userStatus", UserStatus.ACTIVE.name())
                .addValue("carriedOutByUserFullName", "John Doe")
                .addValue("carriedOutByUserId", 1)
                .addValue("userAction", ApplicationAction.CREATE_NEW_ADMIN_USER)
                .addValue("description", "Test")
                .addValue("ipAddress", "ttt")
                .addValue("objectClassReference", User.class.getCanonicalName());

        Map<String, Object> m = handleSaveAdminUser.execute(in);
        logger.info("{}", m);
        List<User> result = (List<User>) m.get("#result-set-1");
        return result.get(0);
    }






    public List<UserRolePermission> getPermissionsByRole(String userRole) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("userRole", userRole);
        Map<String, Object> m = getPermissionsByRole.execute(in);
        logger.info("{}", m);
        List<UserRolePermission> result = (List<UserRolePermission>) m.get("#result-set-1");
        return result;
    }
}
