package com.jamub.payaccess.api.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
import com.jamub.payaccess.api.dao.util.RowMapper;
import com.jamub.payaccess.api.dao.util.UtilityHelper;
import com.jamub.payaccess.api.enums.*;
import com.jamub.payaccess.api.models.*;
import com.jamub.payaccess.api.models.request.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private SimpleJdbcCall handleDeactivateMerchant;
    private SimpleJdbcCall handleReviewMerchant;

    private  SimpleJdbcCall handleRequestMerchantUpdate;


    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private SimpleJdbcCall handleUpdateMerchantBioData;
    private SimpleJdbcCall handleUpdateMerchantBusinessData;
    private SimpleJdbcCall handleUpdateMerchantBusinessBankAccountData;
    private SimpleJdbcCall getMerchantUserByEmailAddress;
    private SimpleJdbcCall handleGetMerchantByMerchantCode;
    private SimpleJdbcCall handleApproveMerchant;
    private SimpleJdbcCall handleDisApproveMerchant;
    private SimpleJdbcCall handleGetMerchants;
    private SimpleJdbcCall handleUpdateMerchantAboutBusiness;

    private SimpleJdbcCall handleUpdatePayAccessUsage;

    private SimpleJdbcCall handleUpdateMerchantCountry;
    private SimpleJdbcCall handleUpdateMerchantTransactionFeePayer;
    private SimpleJdbcCall handleUpdateMerchantEarningsOption;
    private SimpleJdbcCall handleUpdateMerchantBusinessType;
    private SimpleJdbcCall handleCreateNewContactMessage;
    private SimpleJdbcCall handleCreateNewFeedbackMessage;
    private SimpleJdbcCall handleUpdateMerchantNotificationSettings;
    private SimpleJdbcCall handleUpdateMerchantSecurity;
    private SimpleJdbcCall handleUpdateMerchantPaymentSetting;
    private SimpleJdbcCall handleGetMerchantSetting;
    private SimpleJdbcCall handleGetMerchantKeys;
    private SimpleJdbcCall handleUpdateMerchantLiveKeys;
    private SimpleJdbcCall handleUpdateMerchantTestKeys;
    private SimpleJdbcCall handleUpdateMerchantCallbackWebhook;
    private SimpleJdbcCall handleAddNewMerchantToExistingUser;
    private SimpleJdbcCall handleUpdateMerchantKYCDocuments;
    private SimpleJdbcCall handleRequestMerchantApproval;

    private SimpleJdbcCall handleUpdateApiMode;
    private SimpleJdbcCall handleGetMerchantApprovalByMerchant;
    private SimpleJdbcCall handleUpdateMerchantStatus;
    private SimpleJdbcCall handleGetMerchantApproval;
    private SimpleJdbcCall handleCreateMakerChecker;

    private SimpleJdbcCall getMakerCheckerList;

    private SimpleJdbcCall getMakerCheckerByUser;

    private SimpleJdbcCall getStateByCountry;


    @Autowired
    public void setDataSource(DataSource ds)
    {
        this.jdbcTemplate = new JdbcTemplate(ds);
        getMerchant = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetMerchantById")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));

        getAllMerchants = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetMerchants")
                .returningResultSet("#result-set-1", MerchantRowMapper.newInstance(Merchant.class))
                .returningResultSet("#result-set-2", new BeanPropertyRowMapper<Integer>()
                {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        return rs.getInt("count");
                    }
                });


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

        handleDeactivateMerchant = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("DeactivateMerchant")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class))
                .returningResultSet("#result-set-2",
                        MerchantRowMapper.newInstance(User.class));

        handleReviewMerchant = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("ReviewMerchant")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class))
                .returningResultSet("#result-set-2",
                        MerchantRowMapper.newInstance(User.class));

        handleRequestMerchantUpdate = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("RequestMerchantUpdate")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class))
                .returningResultSet("#result-set-2",
                        MerchantRowMapper.newInstance(User.class));

        handleUpdateMerchantBioData = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateMerchantBioData")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));

        handleUpdateMerchantBusinessData = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateMerchantBusinessData")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));

        handleUpdateMerchantBusinessBankAccountData = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateMerchantBusinessBankAccountData")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));

        handleGetMerchantByMerchantCode = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetMerchantByMerchantCode")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class))
                .returningResultSet("#result-set-2",
                        MerchantRowMapper.newInstance(User.class));

        handleApproveMerchant = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("ApproveMerchant")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));

        handleDisApproveMerchant = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("DisapproveMerchant")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));

        handleGetMerchants = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetMerchants")
                .returningResultSet("#result-set-1", new BeanPropertyRowMapper<Merchant>()
                {
                    @Override
                    public Merchant mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        Merchant merchant = new Merchant();
                        merchant.setId(rs.getInt("id"));
                        merchant.setBusinessAccountName(rs.getString("business_account_name"));
                        merchant.setBusinessAccountNumber(rs.getString("business_account_number"));
                        merchant.setBusinessBankName(rs.getString("business_bank_name"));
                        merchant.setBusinessBvn(rs.getString("business_bvn"));
                        merchant.setBusinessCategory(rs.getString("business_category").isEmpty() ? null : BusinessCategory.valueOfLabel(rs.getString("business_category")));
                        merchant.setBusinessCity(rs.getString("business_city"));
                        merchant.setBusinessDescription(rs.getString("business_description"));
                        merchant.setBusinessEmail(rs.getString("business_email"));
                        merchant.setBusinessLogo(rs.getString("business_logo"));
                        merchant.setBusinessName(rs.getString("business_name"));
                        merchant.setBusinessState(rs.getString("business_state"));
                        merchant.setBusinessType(rs.getString("business_type").isEmpty() ? null : BusinessType.valueOfLabel(rs.getString("business_type")));
                        merchant.setBusinessWebsite(rs.getString("business_website"));
                        merchant.setMerchantStatus(rs.getString("merchant_status").isEmpty() ? null : MerchantStatus.valueOfLabel(rs.getString("merchant_status")));
                        merchant.setPrimaryMobile(rs.getString("primary_mobile"));
                        merchant.setSupportContact(rs.getString("support_contact"));
//                        merchant.setBusinessType(rs.getString("merchant_status").isEmpty() ? null : BusinessType.valueOfLabel(rs.getString("merchant_status")));
                        merchant.setMerchantCode(rs.getString("merchant_code"));
                        merchant.setApiMode(APIMode.valueOf(rs.getString("api_mode")));
                        merchant.setSecretKey(rs.getString("secret_key"));
                        merchant.setPublicKey(rs.getString("public_key"));
                        merchant.setUserId(rs.getLong("user_id"));
                        merchant.setCallbackUrl(rs.getString("callback_url"));
                        merchant.setWebhookUrl(rs.getString("webhook_url"));
                        merchant.setPayAccessUsage(rs.getString("pay_access_usage"));
                        merchant.setBusinessOwnersDocumentFileName(rs.getString("business_owners_document_file_name"));
                        merchant.setDirectorsProofOfIdentityFileName(rs.getString("directors_proof_of_identity_file_name"));
                        merchant.setGovernmentApprovedDocumentFileName(rs.getString("government_approved_document_file_name"));
                        merchant.setShareholdersDocumentFileName(rs.getString("shareholders_document_file_name"));
                        merchant.setCreatedAt(rs.getTimestamp("created_at")==null ? null : rs.getTimestamp("created_at").toLocalDateTime());
                        merchant.setDeletedAt(rs.getTimestamp("deleted_at")==null ? null : rs.getTimestamp("deleted_at").toLocalDateTime());
                        merchant.setUpdatedAt(rs.getTimestamp("updated_at")==null ? null : rs.getTimestamp("updated_at").toLocalDateTime());
                        merchant.setAccountInfoSet(rs.getBoolean("account_info_set"));
                        merchant.setBusinessCertificateFile(rs.getString("business_certificate_file"));
                        merchant.setBusinessInfoSet(rs.getBoolean("business_info_set"));
                        merchant.setKycSet(rs.getBoolean("kyc_set"));
                        merchant.setPersonalInfoSet(rs.getBoolean("personal_info_set"));
                        return merchant;
                    }
                });


        handleUpdateMerchantAboutBusiness = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("UpdateMerchantAboutBusiness")
            .returningResultSet("#result-set-1",
                    MerchantRowMapper.newInstance(Merchant.class));


        handleUpdatePayAccessUsage = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdatePayAccessUsage")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));

        handleUpdateMerchantCountry = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateMerchantCountry")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));


        handleUpdateMerchantTransactionFeePayer = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateMerchantTransactionFeePayer")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(MerchantSetting.class));


        handleUpdateMerchantEarningsOption = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateMerchantEarningsOption")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));


        handleUpdateMerchantBusinessType = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateMerchantBusinessType")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));


        handleCreateNewContactMessage = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("CreateNewContactMessage")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(ContactUs.class));


        handleCreateNewFeedbackMessage = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("CreateNewFeedbackMessage")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Feedback.class));

        handleUpdateMerchantNotificationSettings = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateMerchantNotificationSettings")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(MerchantSetting.class));

        handleUpdateMerchantSecurity = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateMerchantSecurity")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(MerchantSetting.class));

        handleUpdateMerchantPaymentSetting = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateMerchantPaymentSetting")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(MerchantSetting.class));

        handleGetMerchantSetting = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetMerchantSetting")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(MerchantSetting.class));

        handleGetMerchantKeys = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetMerchantKeys")
                .returningResultSet("#result-set-1",
                        BeanPropertyRowMapper.newInstance(MerchantCredential.class));



        handleUpdateMerchantLiveKeys = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateMerchantLiveKeys")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));

        handleUpdateMerchantTestKeys = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateMerchantTestKeys")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));

        handleUpdateMerchantCallbackWebhook = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateMerchantCallbackWebhook")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));

        handleAddNewMerchantToExistingUser = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("AddNewMerchantToExistingUser")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));

        handleUpdateMerchantKYCDocuments = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateMerchantKYCDocuments")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));

        handleRequestMerchantApproval = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("RequestMerchantApproval")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));

        handleUpdateApiMode = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateApiMode")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));


        handleGetMerchantApprovalByMerchant = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetMerchantApprovalByMerchant")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(MerchantApproval.class));

        handleUpdateMerchantStatus = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateMerchantStatus")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Merchant.class));

        handleGetMerchantApproval = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetMerchantApproval")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(MerchantApproval.class));

        handleCreateMakerChecker = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("CreateMakerChecker")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(MakerChecker.class));

        getMakerCheckerList = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetMakerCheckerList")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(MakerCheckerUser.class))
                .returningResultSet("#result-set-2", new BeanPropertyRowMapper<Integer>()
                {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        return rs.getInt("count");
                    }
                });

        getMakerCheckerByUser = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetMakerCheckerByUser")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(MakerChecker.class));

        getStateByCountry = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetStateByCountry")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(CountryState.class));

    }

    @Override
    public Optional<Merchant> get(Long merchantId) {

        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantId", merchantId);
        Map<String, Object> m = getMerchant.execute(in);

        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");

        if(result.isEmpty())
            return Optional.empty();

        return Optional.of(result.get(0));
    }

    @Override
    public Map getAll() {
        MapSqlParameterSource in = new MapSqlParameterSource();
        Map<String, Object> m = getAllMerchants.execute(in);

        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");
        List<Integer> resultCount = (List<Integer>) m.get("#result-set-2");

        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", resultCount.get(0));
        return returnList;
    }

//    @Override
//    public Merchant save(MerchantSignUpRequest merchantSignUpRequest) {
//
////        logger.info("merchantSignUpRequest.isSoftwareDeveloper()...{}", merchantSignUpRequest.isSoftwareDeveloper());
//        String bcryptPassword = UtilityHelper.generateBCryptPassword(merchantSignUpRequest.getPassword());
//        logger.info("bcryptPassword = {}", bcryptPassword);
//        MapSqlParameterSource in = new MapSqlParameterSource()
//                .addValue("country", merchantSignUpRequest.getCountry())
//                .addValue("firstName", merchantSignUpRequest.getFirstName())
//                .addValue("lastName", merchantSignUpRequest.getLastName())
//                .addValue("emailAddress", merchantSignUpRequest.getEmailAddress())
//                .addValue("password", bcryptPassword)
//                .addValue("businessName", merchantSignUpRequest.getBusinessName())
//                .addValue("businessCategory", merchantSignUpRequest.getBusinessCategory())
//                .addValue("businessType", merchantSignUpRequest.getBusinessType())
//                .addValue("isSoftwareDeveloper", merchantSignUpRequest.getIsSoftwareDeveloper().equals("1") ? true : false)
//                .addValue("verificationLink", merchantSignUpRequest.getVerificationLink())
//                .addValue("userStatus", UserStatus.NOT_ACTIVATED.name())
//                .addValue("merchantStatus", MerchantStatus.IN_PROGRESS.name())
//                .addValue("merchantCode", RandomStringUtils.randomAlphanumeric(16).toString().toUpperCase());
//
//        Map<String, Object> m = saveMerchant.execute(in);
//        logger.info("{}", m);
//        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");
//        return result.get(0);
//    }


    @Override
    public Merchant update(Merchant merchant) {
        return null;
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

    public Merchant updateMerchantBioData(MerchantUserBioDataUpdateRequest merchantUserBioDataUpdateRequest, User authenticatedUser) {

        logger.info("{}, {}", merchantUserBioDataUpdateRequest.getMerchantId(), authenticatedUser.getId());
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantId", merchantUserBioDataUpdateRequest.getMerchantId())
                .addValue("userId", authenticatedUser.getId())
                .addValue("gender", merchantUserBioDataUpdateRequest.getGender())
                .addValue("dateOfBirth", merchantUserBioDataUpdateRequest.getDateOfBirth())
                .addValue("identificationDocumentPath", merchantUserBioDataUpdateRequest.getIdentificationDocumentPath())
                .addValue("identificationDocument", merchantUserBioDataUpdateRequest.getIdentificationDocument())
                .addValue("identificationNumber", merchantUserBioDataUpdateRequest.getIdentificationNumber());
        Map<String, Object> m = handleUpdateMerchantBioData.execute(in);
        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");
        Merchant merchant = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchant;
    }

    public Merchant updateMerchantBusinessData(MerchantBusinessDataUpdateRequest merchantBusinessDataUpdateRequest, Long merchantId, User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantId", merchantId)
                .addValue("userId", authenticatedUser.getId())
                .addValue("businessDescription", merchantBusinessDataUpdateRequest.getBusinessDescription())
                .addValue("businessEmail", merchantBusinessDataUpdateRequest.getBusinessEmail())
                .addValue("primaryMobile", merchantBusinessDataUpdateRequest.getPrimaryMobile())
                .addValue("supportContact", merchantBusinessDataUpdateRequest.getSupportContact())
                .addValue("businessCity", merchantBusinessDataUpdateRequest.getBusinessCity())
                .addValue("businessState", merchantBusinessDataUpdateRequest.getBusinessState())
                .addValue("businessWebsite", merchantBusinessDataUpdateRequest.getBusinessWebsite())
                .addValue("businessLogo", merchantBusinessDataUpdateRequest.getBusinessLogo())
                .addValue("businessCertificateFile", merchantBusinessDataUpdateRequest.getBusinessCertificateFile());
        Map<String, Object> m = handleUpdateMerchantBusinessData.execute(in);
        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");
        Merchant merchant = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchant;
    }

    public Merchant updateMerchantBusinessBankAccountData(MerchantBusinessBankAccountDataUpdateRequest merchantBusinessBankAccountDataUpdateRequest,
                                                          User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("userId", authenticatedUser.getId())
                .addValue("merchantId", merchantBusinessBankAccountDataUpdateRequest.getMerchantId())
                .addValue("businessBvn", merchantBusinessBankAccountDataUpdateRequest.getBusinessBvn())
                .addValue("businessBankName", merchantBusinessBankAccountDataUpdateRequest.getBusinessBankName())
                .addValue("businessAccountNumber", merchantBusinessBankAccountDataUpdateRequest.getBusinessAccountNumber())
                .addValue("businessAccountName", merchantBusinessBankAccountDataUpdateRequest.getBusinessAccountName());
        Map<String, Object> m = handleUpdateMerchantBusinessBankAccountData.execute(in);
        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");
        Merchant merchant = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchant;
    }


    public Merchant updateMerchantAboutBusiness(MerchantSignUpRequest merchantSignUpRequest,
                                                User authenticatedUser) {

        logger.info("{} {}", authenticatedUser.getId(), merchantSignUpRequest.getMerchantId());
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("userId", authenticatedUser.getId())
                .addValue("merchantId", merchantSignUpRequest.getMerchantId())
                .addValue("businessName", authenticatedUser.getPrimaryBusinessName())
                .addValue("businessCategory", merchantSignUpRequest.getBusinessCategory())
                .addValue("businessType", merchantSignUpRequest.getBusinessType())
                .addValue("softwareDeveloper", merchantSignUpRequest.getSoftwareDeveloper())
                //.addValue("country", merchantSignUpRequest.getCountry())
                .addValue("mobileNumber", merchantSignUpRequest.getMobileNumber());
        Map<String, Object> m = handleUpdateMerchantAboutBusiness.execute(in);
        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");
        Merchant merchant = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchant;
    }


    public Merchant updateMerchantPayAccessUsage(Merchant merchant,
                                                User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantId", merchant.getId())
                .addValue("payAccessUsage", merchant.getPayAccessUsage());
        Map<String, Object> m = handleUpdatePayAccessUsage.execute(in);
        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");
        merchant = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchant;
    }


    public void updateMerchantCountry(UpdateMerchantCountryRequest updateMerchantCountryRequest,
                                                User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("userId", authenticatedUser.getId())
                .addValue("country", updateMerchantCountryRequest.getCountry().toUpperCase());
        handleUpdateMerchantCountry.execute(in);
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


    public List<?> getMerchantByMerchantCode(String merchantCode)
    {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantCode", merchantCode, Types.VARCHAR);
        logger.info("{}", in.getValues());
        Map<String, Object> m = handleGetMerchantByMerchantCode.execute(in);
        logger.info("{}", m);
        List arrayList = new ArrayList();
        ArrayList merchantArrayList = (ArrayList) m.get("#result-set-1");
        Merchant merchant = null;
        User user = null;
        logger.info("merchantArrayList size == {}", merchantArrayList.size());
        if(merchantArrayList!=null && !merchantArrayList.isEmpty()) {
            merchant = (Merchant) (merchantArrayList).get(0);
            user = (User) ((ArrayList) m.get("#result-set-2")).get(0);
            arrayList.add(0, merchant);
            arrayList.add(1, user);
            return arrayList;
        }

        return null;
    }


    public List<Merchant> getMerchants(int pageNumber, int maxSize, GetMerchantFilterRequest getMerchantFilterRequest)
    {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber", pageNumber*maxSize, Types.INTEGER)
                    .addValue("pageSize", maxSize, Types.INTEGER)
                .addValue("merchantStatus", getMerchantFilterRequest.getMerchantStatus())
                .addValue("endDate", getMerchantFilterRequest.getEndDate())
                .addValue("startDate", getMerchantFilterRequest.getStartDate());
        logger.info("{}", in.getValues());
        logger.info("pageNumber..{}", pageNumber);
        logger.info("maxSize..{}", maxSize);
        Map<String, Object> m = handleGetMerchants.execute(in);
        logger.info("{}", m);
        List<Merchant> merchantList = (ArrayList<Merchant>)( m.get("#result-set-1"));
        return merchantList;
    }




    public List<MerchantApproval> getMerchantApproval(String merchantCode)
    {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantCode", merchantCode);
        logger.info("{}", in.getValues());
        Map<String, Object> m = handleGetMerchantApproval.execute(in);
        logger.info("{}", m);
        List<MerchantApproval> merchantApprovalList = (ArrayList<MerchantApproval>)( m.get("#result-set-1"));
        return merchantApprovalList;
    }


    public Merchant approveMerchant(String merchantCode)
    {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantCode", merchantCode, Types.VARCHAR);
        logger.info("{}", in.getValues());

        Map<String, Object> m = handleApproveMerchant.execute(in);
        logger.info("{}", m);
        Merchant approvedMerchant = null;
        approvedMerchant = (Merchant)((ArrayList) m.get("#result-set-1")).get(0);

        return approvedMerchant;
    }

    public Merchant disapproveMerchant(String merchantCode)
    {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantCode", merchantCode, Types.VARCHAR);
        logger.info("{}", in.getValues());

        Map<String, Object> m = handleDisApproveMerchant.execute(in);
        logger.info("{}", m);
        Merchant approvedMerchant = null;
        approvedMerchant = (Merchant)((ArrayList) m.get("#result-set-1")).get(0);

        return approvedMerchant;
    }

//    public Merchant deactivateMerchant(MerchantStatusUpdateRequest merchantStatusUpdateRequest, User authenticatedUser)
//    {
//        MapSqlParameterSource in = new MapSqlParameterSource()
//                .addValue("merchantCode", merchantStatusUpdateRequest.getMerchantCode(), Types.VARCHAR)
//                .addValue("details", merchantStatusUpdateRequest.getReason(), Types.VARCHAR)
//                .addValue("createdByUserId", authenticatedUser.getId());
//        logger.info("{}", in.getValues());
//
//        Map<String, Object> m = handleDeactivateMerchant.execute(in);
//        logger.info("{}", m);
//        Merchant deactivatedMerchant = null;
//        deactivatedMerchant = (Merchant)((ArrayList) m.get("#result-set-1")).get(0);
//
//        return deactivatedMerchant;
//    }

    public Merchant handleReviewMerchant(MerchantReviewUpdateStatusRequest merchantReviewUpdateStatusRequest, User authenticatedUser)
    {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantCode", merchantReviewUpdateStatusRequest.getMerchantCode(), Types.VARCHAR)
                .addValue("details", merchantReviewUpdateStatusRequest.getReason(), Types.VARCHAR)
                .addValue("merchantStage", merchantReviewUpdateStatusRequest.getMerchantStage(), Types.VARCHAR)
                .addValue("createdByUserId", authenticatedUser.getId())
                .addValue("merchantReviewStatus", merchantReviewUpdateStatusRequest.getMerchantReviewStatus());

        ObjectMapper obj = new ObjectMapper();

        try {
            logger.info(">>>>>>>>>>");
            logger.info("{} {}", in.getValues(), obj.writeValueAsString(merchantReviewUpdateStatusRequest));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> m = handleReviewMerchant.execute(in);
        logger.info("{}", m);
        Merchant deactivatedMerchant = null;
        deactivatedMerchant = (Merchant)((ArrayList) m.get("#result-set-1")).get(0);

        return deactivatedMerchant;
    }

//    public Merchant requestMerchantUpdate(MerchantStatusUpdateRequest merchantStatusUpdateRequest, User authenticatedUser)
//    {
//        MapSqlParameterSource in = new MapSqlParameterSource()
//                .addValue("merchantCode", merchantStatusUpdateRequest.getMerchantCode(), Types.VARCHAR)
//                .addValue("details", merchantStatusUpdateRequest.getReason(), Types.VARCHAR)
//                .addValue("merchantStage", merchantStatusUpdateRequest.getMerchantStage().name(), Types.VARCHAR)
//                .addValue("createdByUserId", authenticatedUser.getId())
//                .addValue("merchantReviewStatus", merchantStatusUpdateRequest.getMerchantReviewStatus().name());
//        logger.info("{}", in.getValues());
//
//        Map<String, Object> m = handleRequestMerchantUpdate.execute(in);
//        logger.info("{}", m);
//        Merchant merchant = null;
//        merchant = (Merchant)((ArrayList) m.get("#result-set-1")).get(0);
//
//        return merchant;
//    }


    public MerchantSetting updateMerchantTransactionFeePayer(Boolean merchantMustPayTransactionFee, Long merchantId, User authenticatedUser) {
        int merchantMustPayTransactionFeeInt = merchantMustPayTransactionFee!=null && merchantMustPayTransactionFee.equals(Boolean.TRUE) ? 1 : 0;
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantId", merchantId)
                .addValue("userId", authenticatedUser.getId())
                .addValue("merchantMustPayTransactionFee", merchantMustPayTransactionFeeInt);
        Map<String, Object> m = handleUpdateMerchantTransactionFeePayer.execute(in);
        List<MerchantSetting> result = (List<MerchantSetting>) m.get("#result-set-1");
        MerchantSetting merchantSetting = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchantSetting;
    }

    public Merchant updateMerchantReceiveEarnings(String receiveEarningsOption, Long merchantId, User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantId", merchantId)
                .addValue("userId", authenticatedUser.getId())
                .addValue("receiveEarningsOption", receiveEarningsOption);
        Map<String, Object> m = handleUpdateMerchantEarningsOption.execute(in);
        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");
        Merchant merchant = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchant;
    }

    public Merchant updateMerchantBusinessType(String businessType, Long merchantIdL, User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantId", merchantIdL)
                .addValue("userId", authenticatedUser.getId())
                .addValue("businessType", businessType);
        Map<String, Object> m = handleUpdateMerchantBusinessType.execute(in);
        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");
        Merchant merchant = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchant;
    }

    public void createContactUsMessage(String emailAddress, String subject, String productCategory, String description, String urgency, String newFileName, User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailAddress", emailAddress)
                .addValue("userId", authenticatedUser.getId())
                .addValue("subject", subject)
                .addValue("productCategory", productCategory)
                .addValue("description", description)
                .addValue("urgency", urgency)
                .addValue("attachment", newFileName);
        handleCreateNewContactMessage.execute(in);
    }

    public void createFeedbackMessage(String emailAddress, String title, String productCategory, String description, String urgency, User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailAddress", emailAddress)
                .addValue("userId", authenticatedUser.getId())
                .addValue("title", title)
                .addValue("productCategory", productCategory)
                .addValue("description", description)
                .addValue("urgency", urgency);
        handleCreateNewFeedbackMessage.execute(in);
    }


    public MerchantSetting updateMerchantNotifications(NotificationSettingRequest notificationSettingRequest, User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantId", notificationSettingRequest.getMerchantId())
                .addValue("merchantReceiveEarningsOption", notificationSettingRequest.getMerchantReceiveEarningsOption())
                .addValue("transactionNotificationByEmail", notificationSettingRequest.getTransactionNotificationByEmail())
                .addValue("customerNotificationByEmail", notificationSettingRequest.getCustomerNotificationByEmail())
                .addValue("transferNotificationByEmailForCredit", notificationSettingRequest.getTransferNotificationByEmailForCredit())
                .addValue("transferNotificationByEmailForDebit", notificationSettingRequest.getTransferNotificationByEmailForDebit())
                .addValue("enableNotificationForTransfer", notificationSettingRequest.getEnableNotificationForTransfer())
                .addValue("enableNotificationForInvoicing", notificationSettingRequest.getEnableNotificationForInvoicing())
                .addValue("enableNotificationForPaymentLink", notificationSettingRequest.getEnableNotificationForPaymentLink())
                .addValue("enableNotificationForSettlement", notificationSettingRequest.getEnableNotificationForSettlement());
        Map<String, Object> m = handleUpdateMerchantNotificationSettings.execute(in);
        List<MerchantSetting> result = (List<MerchantSetting>) m.get("#result-set-1");
        MerchantSetting merchantSetting = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchantSetting;
    }

    public MerchantSetting updateMerchantSecurity(MerchantSecuritySettingRequest merchantSecuritySettingRequest, User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("userId", authenticatedUser.getId())
                .addValue("merchantId", merchantSecuritySettingRequest.getMerchantId())
                .addValue("twoFactorAuthForPaymentAndTransfer", merchantSecuritySettingRequest.getTwoFactorAuthForPaymentAndTransfer())
                .addValue("twoFactorAuthForLogin", merchantSecuritySettingRequest.getTwoFactorAuthForLogin());
        Map<String, Object> m = handleUpdateMerchantSecurity.execute(in);
        List<MerchantSetting> result = (List<MerchantSetting>) m.get("#result-set-1");
        MerchantSetting merchantSetting = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchantSetting;
    }

    public MerchantSetting updateMerchantPaymentSetting(MerchantPaymentSettingRequest merchantPaymentSettingRequest, User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantId", merchantPaymentSettingRequest.getMerchantId())
                .addValue("enableAcceptPOSChannel", merchantPaymentSettingRequest.getEnableAcceptPOSChannel())
                .addValue("enableAcceptBankTransfers", merchantPaymentSettingRequest.getEnableAcceptBankTransfers())
                .addValue("enableAcceptCardPayment", merchantPaymentSettingRequest.getEnableAcceptCardPayment())
                .addValue("enableAcceptMobileMoneyTransfer", merchantPaymentSettingRequest.getEnableAcceptMobileMoneyTransfer())
                .addValue("enableUSSDTransfer", merchantPaymentSettingRequest.getEnableUSSDTransfer())
                .addValue("defaultCurrency", merchantPaymentSettingRequest.getDefaultCurrency());
        Map<String, Object> m = handleUpdateMerchantPaymentSetting.execute(in);
        List<MerchantSetting> result = (List<MerchantSetting>) m.get("#result-set-1");
        MerchantSetting merchantSetting = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchantSetting;
    }

    public MerchantSetting getMerchantSettings(Long merchantId, User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantId", merchantId);
        Map<String, Object> m = handleGetMerchantSetting.execute(in);
        List<MerchantSetting> result = (List<MerchantSetting>) m.get("#result-set-1");
        MerchantSetting merchantSetting = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchantSetting;
    }

    public Merchant updateLiveKeys(Long merchantId, User authenticatedUser, String secretKeyLive, String publicKeyLive) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantId", merchantId)
                .addValue("secretKeyLive", secretKeyLive)
                .addValue("publicKeyLive", publicKeyLive);
        Map<String, Object> m = handleUpdateMerchantLiveKeys.execute(in);
        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");
        Merchant merchant = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchant;
    }

    public Merchant updateTestKeys(Long merchantId, User authenticatedUser, String secretKey, String publicKey) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantId", merchantId)
                .addValue("secretKey", secretKey)
                .addValue("publicKey", publicKey);
        Map<String, Object> m = handleUpdateMerchantTestKeys.execute(in);
        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");
        Merchant merchant = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchant;
    }

    public MerchantCredential getMerchantKeys(Long merchantId, User authenticatedUser) {

        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantId", merchantId);
        Map<String, Object> m = handleGetMerchantKeys.execute(in);
        logger.info("{} {}", m, merchantId);
        List<MerchantCredential> result = (List<MerchantCredential>) m.get("#result-set-1");
        MerchantCredential merchantCredential = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchantCredential;
    }

    public Merchant updateMerchantCallbackWebhook(UpdateMerchantCallbackRequest updateMerchantCallbackRequest, User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantId", updateMerchantCallbackRequest.getMerchantId())
                .addValue("callbackUrl", updateMerchantCallbackRequest.getCallbackUrl())
                .addValue("webhookUrl", updateMerchantCallbackRequest.getWebhookUrl());
        Map<String, Object> m = handleUpdateMerchantCallbackWebhook.execute(in);
        logger.info("{} {}", m, updateMerchantCallbackRequest.getMerchantId());
        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");
        Merchant merchant = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchant;
    }

    public Merchant addNewMerchantToExistingUser(AddMerchantRequest addMerchantRequest, User authenticatedUser,
                                                 String secretKey, String publicKey,
                                                 String secretKeyLive, String publicKeyLive, String merchantCode) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("userId", authenticatedUser.getId())
                .addValue("businessType", addMerchantRequest.getBusinessType())
                .addValue("secretKey", secretKey)
                .addValue("publicKey", publicKey)
                .addValue("secretKeyLive", secretKeyLive)
                .addValue("publicKeyLive", publicKeyLive)
                .addValue("merchantCode", merchantCode)
                .addValue("businessName", addMerchantRequest.getBusinessName());
        Map<String, Object> m = handleAddNewMerchantToExistingUser.execute(in);
        logger.info("{} {}", m, addMerchantRequest);
        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");
        Merchant merchant = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchant;
    }

    public Merchant updateMerchantKYCDocuments(String governmentApprovedDocumentFileName,
                                               String directorsProofOfIdentityFileName,
                                               String businessOwnersDocumentFileName,
                                               String shareholdersDocumentFileName,
                                               Long merchantId, User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("userId", authenticatedUser.getId())
                .addValue("governmentApprovedDocumentFileName", governmentApprovedDocumentFileName)
                .addValue("directorsProofOfIdentityFileName", directorsProofOfIdentityFileName)
                .addValue("businessOwnersDocumentFileName", businessOwnersDocumentFileName)
                .addValue("shareholdersDocumentFileName", shareholdersDocumentFileName)
                .addValue("merchantId", merchantId);
        Map<String, Object> m = handleUpdateMerchantKYCDocuments.execute(in);
        logger.info("{} {}", m, merchantId);
        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");
        Merchant merchant = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchant;
    }

    public Merchant requestMerchantApproval(String merchantCode, User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("userId", authenticatedUser.getId())
                .addValue("merchantCode", merchantCode);
        Map<String, Object> m = handleRequestMerchantApproval.execute(in);
        logger.info("{} {}", m, merchantCode);
        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");
        Merchant merchant = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchant;
    }

    public Merchant updateApiMode(String merchantCode, Boolean isLive, User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("userId", authenticatedUser.getId())
                .addValue("apiMode", isLive!=null && isLive.equals(Boolean.TRUE) ? APIMode.LIVE : APIMode.TEST)
                .addValue("merchantCode", merchantCode);
        Map<String, Object> m = handleUpdateApiMode.execute(in);
        logger.info("{} {}", m, merchantCode);
        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");
        Merchant merchant = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchant;
    }

    public List<MerchantApproval> fetchMerchantApprovalListByMerchant(String merchantCode, User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("actedByUserId", authenticatedUser.getId())
                .addValue("merchantCode", merchantCode);
        Map<String, Object> m = handleGetMerchantApprovalByMerchant.execute(in);
        logger.info("{} {}", m, merchantCode, authenticatedUser.getId());
        List<MerchantApproval> result = (List<MerchantApproval>) m.get("#result-set-1");
        return result;
    }

    public List<MerchantApproval> updateMerchantStatus(String merchantCode, String merchantStatus, User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("actedByUserId", authenticatedUser.getId())
                .addValue("merchantStatus", merchantStatus)
                .addValue("merchantCode", merchantCode);
        Map<String, Object> m = handleUpdateMerchantStatus.execute(in);
        logger.info("{} {}", m, merchantCode, authenticatedUser.getId());
        List<MerchantApproval> result = (List<MerchantApproval>) m.get("#result-set-1");
        return result;
    }

    public MakerChecker createMerchantApprovalMakerChecker(String approverEmailAddress, Integer checkerLevel, String makerCheckerType,
                                                           Long actorId, String ipAddress, String description, ApplicationAction userAction,
                                                           String carriedOutByUserFullName, String objectClassReference, Long objectIdReference) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("approverEmailAddress", approverEmailAddress)
                .addValue("checkerLevel", checkerLevel)
                .addValue("makerCheckerType", makerCheckerType)
                .addValue("actorId", actorId)
                .addValue("ipAddress", ipAddress)
                .addValue("description", description)
                .addValue("userAction", userAction)
                .addValue("carriedOutByUserFullName", carriedOutByUserFullName)
                .addValue("objectClassReference", objectClassReference);
        Map<String, Object> m = handleCreateMakerChecker.execute(in);
        List<MakerChecker> result = (List<MakerChecker>) m.get("#result-set-1");
        return result!=null && !result.isEmpty() ? result.get(0) : null;
    }

    public Map getMakerCheckerList(String makerCheckerType, Integer rowCount, Integer pageNumber) {

        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("makerCheckerType", makerCheckerType)
                .addValue("pageSize", rowCount)
                .addValue("pageNumber", pageNumber);

        Map<String, Object> m = getMakerCheckerList.execute(in);
        logger.info("{}", m);
        List<TransactionTicket> result = (List<TransactionTicket>) m.get("#result-set-1");
        List<Integer> totalCount = (List<Integer>) m.get("#result-set-2");
        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", totalCount.get(0));
        return returnList;
    }


    public List<MakerChecker> getMakerCheckerByUser(String emailAddress, String makerCheckerType) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailAddress", emailAddress)
                .addValue("makerCheckerType", makerCheckerType);

        Map<String, Object> m = getMakerCheckerByUser.execute(in);
        logger.info("{}", m);
        List<MakerChecker> result = (List<MakerChecker>) m.get("#result-set-1");
        return result;
    }

    public List<CountryState> getStatesByCountry(String country) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("country", country);

        Map<String, Object> m = getStateByCountry.execute(in);
        logger.info("{}", m);
        List<CountryState> result = (List<CountryState>) m.get("#result-set-1");
        return result;
    }
}
