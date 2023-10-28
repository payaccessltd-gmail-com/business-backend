package com.jamub.payaccess.api.dao;

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
    private SimpleJdbcCall handleGetMerchantByMerchantCode;
    private SimpleJdbcCall handleApproveMerchant;
    private SimpleJdbcCall handleGetMerchants;
    private SimpleJdbcCall handleUpdateMerchantAboutBusiness;
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


    @Autowired
    public void setDataSource(DataSource ds)
    {
        this.jdbcTemplate = new JdbcTemplate(ds);
        getMerchant = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetMerchantById")
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
                        merchant.setBusinessType(rs.getString("merchant_status").isEmpty() ? null : BusinessType.valueOfLabel(rs.getString("merchant_status")));
                        merchant.setMerchantCode(rs.getString("merchant_code"));
                        merchant.setApiMode(APIMode.valueOf(rs.getString("api_mode")));
                        merchant.setSecretKey(rs.getString("secret_key"));
                        merchant.setPublicKey(rs.getString("public_key"));
                        merchant.setUserId(rs.getLong("user_id"));
                        merchant.setMerchantCode(rs.getString("merchant_code"));
                        return merchant;
                    }
                });


        handleUpdateMerchantAboutBusiness = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("UpdateMerchantAboutBusiness")
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
    public List<Merchant> getAll() {
        MapSqlParameterSource in = new MapSqlParameterSource();
        Map<String, Object> m = getMerchant.execute(in);

        List<Merchant> result = (List<Merchant>) m.get("#result-set-1");
        return result;
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
                .addValue("businessLogo", merchantBusinessDataUpdateRequest.getBusinessLogo());
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
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("userId", authenticatedUser.getId())
                .addValue("merchantId", merchantSignUpRequest.getMerchantId())
                .addValue("businessName", authenticatedUser.getPrimaryBusinessName())
                .addValue("businessCategory", merchantSignUpRequest.getBusinessCategory())
                .addValue("businessType", merchantSignUpRequest.getBusinessType())
                .addValue("softwareDeveloper", merchantSignUpRequest.isSoftwareDeveloper())
                .addValue("country", merchantSignUpRequest.getCountry())
                .addValue("mobileNumber", merchantSignUpRequest.getMobileNumber());
        Map<String, Object> m = handleUpdateMerchantAboutBusiness.execute(in);
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


    public List<?> getMerchantByMerchantCode(String merchantCode)
    {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantCode", merchantCode, Types.VARCHAR);
        logger.info("{}", in.getValues());
        Map<String, Object> m = handleGetMerchantByMerchantCode.execute(in);
        logger.info("{}", m);
        Merchant merchant = (Merchant)((ArrayList) m.get("#result-set-1")).get(0);
        User user = (User)((ArrayList) m.get("#result-set-2")).get(0);

        List arrayList = new ArrayList();
        arrayList.add(0, merchant);
        arrayList.add(1, user);
        return arrayList;
    }


    public List<Merchant> getMerchants(int pageNumber, int maxSize)
    {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber", pageNumber*maxSize, Types.INTEGER)
                    .addValue("pageSize", maxSize, Types.INTEGER);
        logger.info("{}", in.getValues());
        logger.info("pageNumber..{}", pageNumber);
        logger.info("maxSize..{}", maxSize);
        Map<String, Object> m = handleGetMerchants.execute(in);
        logger.info("{}", m);
        List<Merchant> merchantList = (ArrayList<Merchant>)( m.get("#result-set-1"));
        return merchantList;
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

    public void createContactUsMessage(String emailAddress, String subject, String productCategory, String description, Urgency urgency, String newFileName, User authenticatedUser) {
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

    public void createFeedbackMessage(String emailAddress, String title, String productCategory, String description, Urgency urgency, User authenticatedUser) {
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
}
