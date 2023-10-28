package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
import com.jamub.payaccess.api.enums.AccountType;
import com.jamub.payaccess.api.models.AccountPackage;
import com.jamub.payaccess.api.models.Account;
import com.jamub.payaccess.api.models.Customer;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.CustomerPinUpdateRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Repository
public class AccountDao  implements Dao<Account>{
    JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall createNewCustomerAccount;
    private SimpleJdbcCall getAccountPackageByPackageCode;
    private SimpleJdbcCall handleCreateAccountPin;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public void setDataSource(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);

        createNewCustomerAccount = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("CreateNewCustomerAccount")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Account.class));

        getAccountPackageByPackageCode = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetAccountPackageByPackageCode")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(AccountPackage.class));

        handleCreateAccountPin = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("CreateAccountPin")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Account.class));
    }


    @Override
    public Optional<Account> get(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Account> getAll() {
        return null;
    }

    @Override
    public Account update(Account account) {
        return null;
    }

    @Override
    public void delete(Account account) {

    }

    public Account createNewCustomerWallet(String customerName, Customer customer, String identifier1, String currencyCode, Long accountPackageId, int isLive, String pin) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(pin.getBytes(StandardCharsets.UTF_8));
        String hashedPin = Base64.getEncoder().encodeToString(hash);
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("customerId", customer.getId())
                .addValue("accountType", AccountType.CUSTOMER.name())
                .addValue("walletNumber", identifier1)
                .addValue("currencyCode", currencyCode)
                .addValue("isLive", isLive)
                .addValue("accountName", customerName)
                .addValue("accountPackageId", accountPackageId)
                .addValue("hashedPin", hashedPin);

        Map<String, Object> m = createNewCustomerAccount.execute(in);
        logger.info("{}", m);
        List<Account> result = (List<Account>) m.get("#result-set-1");
        return result.get(0);
    }

    public AccountPackage getAccountPackageByPackageCode(String accountPackageName) {

        String otp = RandomStringUtils.randomNumeric(4);
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("accountPackageName", accountPackageName);

        Map<String, Object> m = getAccountPackageByPackageCode.execute(in);
        logger.info("{}", m);
        List<AccountPackage> result = (List<AccountPackage>) m.get("#result-set-1");
        return result.get(0);
    }

    public Account createAccountPin(Account account, CustomerPinUpdateRequest customerPinUpdateRequest, User authenticatedUser) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("accountId", account.getId())
                .addValue("epinHash", customerPinUpdateRequest.getPin());
        Map<String, Object> m = handleCreateAccountPin.execute(in);
        List<Account> result = (List<Account>) m.get("#result-set-1");
        account = result!=null && !result.isEmpty() ? result.get(0) : null;
        return account;
    }
}
