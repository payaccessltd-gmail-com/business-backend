package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
import com.jamub.payaccess.api.dao.util.RowMapper;
import com.jamub.payaccess.api.dao.util.UtilityHelper;
import com.jamub.payaccess.api.enums.MerchantStatus;
import com.jamub.payaccess.api.enums.UserStatus;
import com.jamub.payaccess.api.models.Merchant;
import com.jamub.payaccess.api.models.request.MerchantSignUpRequest;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.MerchantBusinessBankAccountDataUpdateRequest;
import com.jamub.payaccess.api.models.request.MerchantBusinessDataUpdateRequest;
import com.jamub.payaccess.api.models.request.MerchantUserBioDataUpdateRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.SQLType;
import java.sql.Types;
import java.util.*;

@Repository
public class MerchantDao implements Dao<Merchant>{

    JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall getMerchant;
    private SimpleJdbcCall getAllMerchants;
    private SimpleJdbcCall saveMerchant;
    private SimpleJdbcCall updateMerchant;
    private SimpleJdbcCall deleteMerchant;
    private SimpleJdbcCall handleActivateAccount;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private SimpleJdbcCall handleUpdateMerchantBioData;
    private SimpleJdbcCall handleUpdateMerchantBusinessData;
    private SimpleJdbcCall handleUpdateMerchantBusinessBankAccountData;
    private SimpleJdbcCall getMerchantUserByEmailAddress;


    @Autowired
    public void setDataSource(DataSource ds)
    {
        this.jdbcTemplate = new JdbcTemplate(ds);
        getMerchant = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetMerchants")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));

        getMerchantUserByEmailAddress = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetMerchantUserByEmailAddress")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));

        saveMerchant = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("CreateNewMerchant")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));

        handleActivateAccount = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("ActivateAccount")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class))
                .returningResultSet("#result-set-2",
                        MerchantRowMapper.newInstance(User.class));

        handleUpdateMerchantBioData = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateMerchantBioData")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(User.class));

        handleUpdateMerchantBusinessData = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateMerchantBusinessData")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));

        handleUpdateMerchantBusinessBankAccountData = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateMerchantBusinessBankAccountData")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));
    }

    @Override
    public Optional<Merchant> get(int id) {
        return Optional.empty();
    }

    @Override
    public List<Merchant> getAll() {
        MapSqlParameterSource in = new MapSqlParameterSource();
        Map<String, Object> m = getMerchant.execute(in);

        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");
        return result;
    }

//    @Override
    public Merchant save(MerchantSignUpRequest merchantSignUpRequest) {

//        logger.info("merchantSignUpRequest.isSoftwareDeveloper()...{}", merchantSignUpRequest.isSoftwareDeveloper());
        String bcryptPassword = UtilityHelper.generateBCryptPassword(merchantSignUpRequest.getPassword());
        logger.info("bcryptPassword = {}", bcryptPassword);
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("country", merchantSignUpRequest.getCountry())
                .addValue("firstName", merchantSignUpRequest.getFirstName())
                .addValue("lastName", merchantSignUpRequest.getLastName())
                .addValue("emailAddress", merchantSignUpRequest.getEmailAddress())
                .addValue("password", bcryptPassword)
                .addValue("businessName", merchantSignUpRequest.getBusinessName())
                .addValue("businessCategory", merchantSignUpRequest.getBusinessCategory())
                .addValue("businessType", merchantSignUpRequest.getBusinessType())
                .addValue("isSoftwareDeveloper", merchantSignUpRequest.getIsSoftwareDeveloper().equals("1") ? true : false)
                .addValue("verificationLink", merchantSignUpRequest.getVerificationLink())
                .addValue("userStatus", UserStatus.NOT_ACTIVATED.name())
                .addValue("merchantStatus", MerchantStatus.IN_PROGRESS.name());

        Map<String, Object> m = saveMerchant.execute(in);
        logger.info("{}", m);
        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");
        return result.get(0);
    }


    @Override
    public void update(Merchant merchant) {

    }

    @Override
    public void delete(Merchant merchant) {

    }


    public Map<String, Object> activateAccount(String emailAddress, String verificationLink) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("verificationLink", verificationLink)
                .addValue("emailAddress", emailAddress);
        Map<String, Object> m = handleActivateAccount.execute(in);

        return m;
    }

    public User updateMerchantBioData(MerchantUserBioDataUpdateRequest merchantUserBioDataUpdateRequest, User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailAddress", authenticatedUser.getEmailAddress())
                .addValue("firstName", merchantUserBioDataUpdateRequest.getFirstName())
                .addValue("lastName", merchantUserBioDataUpdateRequest.getLastName())
                .addValue("gender", merchantUserBioDataUpdateRequest.getGender())
                .addValue("dateOfBirth", merchantUserBioDataUpdateRequest.getDateOfBirth())
                .addValue("identificationDocument", merchantUserBioDataUpdateRequest.getIdentificationDocument())
                .addValue("identificationNumber", merchantUserBioDataUpdateRequest.getIdentificationNumber())
                .addValue("identificationDocumentPath", merchantUserBioDataUpdateRequest.getIdentificationDocumentPath());
        Map<String, Object> m = handleUpdateMerchantBioData.execute(in);
        List<User> result = (List<User>) m.get("#result-set-1");
        User user = result!=null && !result.isEmpty() ? result.get(0) : null;
        return user;
    }

    public Merchant updateMerchantBusinessData(MerchantBusinessDataUpdateRequest merchantBusinessDataUpdateRequest, User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailAddress", authenticatedUser.getEmailAddress())
                .addValue("businessDescription", merchantBusinessDataUpdateRequest.getBusinessDescription())
                .addValue("businessEmail", merchantBusinessDataUpdateRequest.getBusinessEmail())
                .addValue("primaryMobile", merchantBusinessDataUpdateRequest.getPrimaryMobile())
                .addValue("supportContact", merchantBusinessDataUpdateRequest.getSupportContact())
                .addValue("businessCity", merchantBusinessDataUpdateRequest.getBusinessCity())
                .addValue("businessState", merchantBusinessDataUpdateRequest.getBusinessState())
                .addValue("businessWebsite", merchantBusinessDataUpdateRequest.getBusinessWebsite())
                .addValue("businessLogo", merchantBusinessDataUpdateRequest.getBusinessLogo())
                .addValue("businessCertificate", merchantBusinessDataUpdateRequest.getBusinessCertificate());
        Map<String, Object> m = handleUpdateMerchantBusinessData.execute(in);
        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");
        Merchant merchant = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchant;
    }

    public Merchant updateMerchantBusinessBankAccountData(MerchantBusinessBankAccountDataUpdateRequest merchantBusinessBankAccountDataUpdateRequest,
                                                          User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailAddress", authenticatedUser.getEmailAddress())
                .addValue("businessBvn", merchantBusinessBankAccountDataUpdateRequest.getBusinessBvn())
                .addValue("businessBankName", merchantBusinessBankAccountDataUpdateRequest.getBusinessBankName())
                .addValue("businessAccountNumber", merchantBusinessBankAccountDataUpdateRequest.getBusinessAccountNumber())
                .addValue("businessAccountName", merchantBusinessBankAccountDataUpdateRequest.getBusinessAccountName());
        Map<String, Object> m = handleUpdateMerchantBusinessBankAccountData.execute(in);
        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");
        Merchant merchant = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchant;
    }



    public List<Merchant> getMerchantUserByEmailAddress(String emailAddress) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailAddress", emailAddress, Types.VARCHAR);
        logger.info("{}", in.getValues());
        Map<String, Object> m = getMerchantUserByEmailAddress.execute(in);
        logger.info("{}", m);
        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");

        return result;
    }
}
