package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.models.Acquirer;
import com.jamub.payaccess.api.models.Bank;
import com.jamub.payaccess.api.models.Customer;
import com.jamub.payaccess.api.models.User;
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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Repository
public class BankDao implements Dao<Bank>{
    JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall createNewBank;
    private SimpleJdbcCall getBankByBankCode;

    private SimpleJdbcCall getBankByBankName;

    private SimpleJdbcCall getBanksByPagination;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public void setDataSource(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);

        createNewBank = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("CreateNewBank")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Bank.class));

        getBankByBankCode = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetBankByBankCode")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Bank.class));

        getBankByBankName = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetBankByBankName")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Bank.class));

        getBanksByPagination = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetBanksByPagination")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Bank.class))
                .returningResultSet("#result-set-2", new BeanPropertyRowMapper<Integer>()
                {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        return rs.getInt("count");
                    }
                });
    }


    @Override
    public Optional<Bank> get(Long id) {
        return Optional.empty();
    }

    @Override
    public Map getAll() {
        Map returnList = new HashMap();
        returnList.put("list", new ArrayList<Bank>());
        returnList.put("totalCount", 100);
        return returnList;
    }

    @Override
    public Bank update(Bank Bank) {
        return null;
    }

    @Override
    public void delete(Bank Bank) {

    }

    public Bank createNewBank(String bankCode, String bankName, String bankOtherName, Long actorId, String ipAddress, String description,
                              ApplicationAction userAction, String carriedOutByUserFullName, String objectClassReference,
                              Long objectIdReference) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("bankCode", bankCode)
                .addValue("bankName", bankName)
                .addValue("bankOtherName", bankOtherName)
                .addValue("carriedOutByUserFullName", carriedOutByUserFullName)
                .addValue("userAction", userAction.name())
                .addValue("description", description)
                .addValue("ipAddress", ipAddress)
                .addValue("objectClassReference", objectClassReference)
                .addValue("objectIdReference", objectIdReference)
                .addValue("carriedOutByUserId", actorId);


        Map<String, Object> m = createNewBank.execute(in);
        logger.info("{}", m);
        List<Bank> result = (List<Bank>) m.get("#result-set-1");
        return result.get(0);
    }

    public Bank getBankByBankCode(String bankCode) {

        String otp = RandomStringUtils.randomNumeric(4);
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("bankCode", bankCode);

        Map<String, Object> m = getBankByBankCode.execute(in);
        logger.info("{}", m);
        List<Bank> result = (List<Bank>) m.get("#result-set-1");
        return result.isEmpty() ? null : result.get(0);
    }


    public Bank getBankByBankName(String BankName) {

        String otp = RandomStringUtils.randomNumeric(4);
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("BankName", BankName);

        Map<String, Object> m = getBankByBankName.execute(in);
        logger.info("{}", m);
        List<Bank> result = (List<Bank>) m.get("#result-set-1");
        return result.isEmpty() ? null : result.get(0);
    }


    public Map getBanksByPagination(Integer pageNumber, Integer pageSize) {

        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber", pageNumber)
                .addValue("pageSize", pageSize);

        Map<String, Object> m = getBanksByPagination.execute(in);
        logger.info("{}", m);
        List<Bank> result = (List<Bank>) m.get("#result-set-1");
        List<Integer> totalCount = (List<Integer>) m.get("#result-set-2");
        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", totalCount.get(0));
        return returnList;
    }
}
