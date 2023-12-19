package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
//import com.jamub.payaccess.api.enums.AcquirerType;
import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.models.*;
import com.jamub.payaccess.api.models.request.CustomerPinUpdateRequest;
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
public class AcquirerDao implements Dao<Acquirer>{
    JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall createNewAcquirer;
    private SimpleJdbcCall getAcquirerByAcquirerCode;

    private SimpleJdbcCall getAcquirerByAcquirerName;

    private SimpleJdbcCall getAcquirers;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public void setDataSource(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);

        createNewAcquirer = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("CreateNewAcquirer")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Acquirer.class));

        getAcquirerByAcquirerCode = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetAcquirerByAcquirerCode")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Acquirer.class));

        getAcquirerByAcquirerName = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetAcquirerByAcquirerName")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Acquirer.class));

        getAcquirers = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetAllAcquirers")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Acquirer.class))
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
    public Optional<Acquirer> get(Long id) {
        return Optional.empty();
    }

    @Override
    public Map getAll() {

        MapSqlParameterSource in = new MapSqlParameterSource();

        Map<String, Object> m = getAcquirers.execute(in);
        logger.info("{}", m);
        List<Acquirer> result = (List<Acquirer>) m.get("#result-set-1");
        List<Integer> totalCount = (List<Integer>) m.get("#result-set-2");
        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", totalCount.get(0));
        return returnList;
    }

    public Map getAcquirersByPage(Integer pageNumber, Integer pageSize) {

        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber", pageNumber)
                .addValue("pageSize", pageSize);

        Map<String, Object> m = getAcquirers.execute(in);
        logger.info("{}", m);
        List<Acquirer> result = (List<Acquirer>) m.get("#result-set-1");
        List<Integer> totalCount = (List<Integer>) m.get("#result-set-2");
        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", totalCount.get(0));
        return returnList;
    }

    @Override
    public Acquirer update(Acquirer Acquirer) {
        return null;
    }

    @Override
    public void delete(Acquirer Acquirer) {

    }

    public Acquirer createNewAcquirer(String acquirerName, String acquirerCode, Bank bank, Boolean isBank,
                                      User authenticatedUser,
                                      Long actorId, String ipAddress, String description,
                                      ApplicationAction userAction, String carriedOutByUserFullName, String objectClassReference,
                                      Long objectIdReference) {

        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("acquirerName", acquirerName)
//                .addValue("AcquirerType", AcquirerType.CUSTOMER.name())
                .addValue("acquirerCode", acquirerCode)
                .addValue("bankId", bank.getId())
                .addValue("isBank", isBank)
                .addValue("carriedOutByUserFullName", carriedOutByUserFullName)
                .addValue("userAction", userAction.name())
                .addValue("description", description)
                .addValue("ipAddress", ipAddress)
                .addValue("objectClassReference", objectClassReference)
//                .addValue("objectIdReference", objectIdReference)
                .addValue("carriedOutByUserId", actorId);

        Map<String, Object> m = createNewAcquirer.execute(in);
        logger.info("{}", m);
        List<Acquirer> result = (List<Acquirer>) m.get("#result-set-1");
        return result.isEmpty() ? null : result.get(0);
    }

    public Acquirer getAcquirerByAcquirerCode(String acquirerCode) {

        String otp = RandomStringUtils.randomNumeric(4);
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("acquirerCode", acquirerCode);

        Map<String, Object> m = getAcquirerByAcquirerCode.execute(in);
        logger.info("{}", m);
        List<Acquirer> result = (List<Acquirer>) m.get("#result-set-1");
        return result.isEmpty() ? null : result.get(0);
    }


    public Acquirer getAcquirerByAcquirerName(String acquirerName) {

        String otp = RandomStringUtils.randomNumeric(4);
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("acquirerName", acquirerName);

        Map<String, Object> m = getAcquirerByAcquirerName.execute(in);
        logger.info("{}", m);
        List<Acquirer> result = (List<Acquirer>) m.get("#result-set-1");
        return result.isEmpty() ? null : result.get(0);
    }

}
