package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.enums.SettlementStatus;
import com.jamub.payaccess.api.models.Settlement;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.CreateSettlementRequest;
import com.jamub.payaccess.api.models.request.SettlementFilterRequest;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class SettlementDao implements Dao<Settlement>{

    JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall getSettlement;
    private SimpleJdbcCall getAllSettlement;
    private SimpleJdbcCall getSettlementByFilter;
    private SimpleJdbcCall saveSettlement;
    private SimpleJdbcCall updateSettlement;
    private SimpleJdbcCall deleteSettlement;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public void setDataSource(DataSource ds)
    {
        this.jdbcTemplate = new JdbcTemplate(ds);
        getSettlement = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetSettlement")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Settlement.class));

        getAllSettlement = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetAllSettlements")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Settlement.class))
                .returningResultSet("#result-set-2", new BeanPropertyRowMapper<Integer>()
                {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        return rs.getInt("count");
                    }
                });

        getSettlementByFilter = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("FilterSettlements")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Settlement.class)
                )
                .returningResultSet("#result-set-2", new BeanPropertyRowMapper<Integer>()
                {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        return rs.getInt("count");
                    }
                });

        saveSettlement = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SaveSettlement")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Settlement.class));

        updateSettlement = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateSettlement")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Settlement.class));


    }

    @Override
    public Optional<Settlement> get(Long id) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("SettlementId", id);
        Map<String, Object> m = getSettlement.execute(in);

        if(m.isEmpty()) {
            return Optional.empty();
        }
        else
        {
            List<Settlement> result = (List<Settlement>) m.get("#result-set-1");
            return Optional.of(result.get(0));
        }
    }


    public Optional<Settlement> get(Long id, Long merchantId) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("SettlementId", id)
                .addValue("merchantId", merchantId);
        Map<String, Object> m = getSettlement.execute(in);

        if(m.isEmpty()) {
            return Optional.empty();
        }
        else
        {
            List<Settlement> result = (List<Settlement>) m.get("#result-set-1");
            return Optional.of(result.get(0));
        }
    }

    @Override
    public Map getAll() {
        MapSqlParameterSource in = new MapSqlParameterSource();
        Map<String, Object> m = getAllSettlement.execute(in);

        List<Settlement> result = (List<Settlement>) m.get("#result-set-1");
        List<Integer> resultInt = (List<Integer>) m.get("#result-set-2");
        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", resultInt.get(0));
        return returnList;
    }


    @Override
    public Settlement update(Settlement settlement) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("settlementStatus", SettlementStatus.COMPLETED)
                .addValue("settlementId", settlement.getId())
                .addValue("settlementDate", settlement.getSettlementDate());
        Map<String, Object> m = updateSettlement.execute(in);
        List<Settlement> result = (List<Settlement>) m.get("#result-set-1");
        settlement = result!=null && !result.isEmpty() ? result.get(0) : null;
        return settlement;
    }

    @Override
    public void delete(Settlement Settlement) {

    }


    public Settlement saveSettlement(CreateSettlementRequest createSettlementRequest, User authenticatedUser, String ipAddress) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("settlementDate", createSettlementRequest.getSettlementDate())
                .addValue("totalSettlementAmount", createSettlementRequest.getSettlementAmount())
                .addValue("payAccessCurrency", createSettlementRequest.getPayAccessCurrency())
                .addValue("createdByUserId", authenticatedUser.getId())
                .addValue("carriedOutByUserFullName", authenticatedUser.getFirstName().concat(" ").concat(authenticatedUser.getLastName()))
                .addValue("userAction", ApplicationAction.RUN_SETTLEMENT)
                .addValue("description", "Run settlement for ".concat(
                                formatter.format(createSettlementRequest.getSettlementDate())
                        )
                )
                .addValue("ipAddress", ipAddress)
                .addValue("objectClassReference", Settlement.class.getCanonicalName())
                .addValue("settlementCode", RandomStringUtils.randomAlphanumeric(16).toString().toUpperCase())
                .addValue("settlementStatus", SettlementStatus.PENDING.name());
        Map<String, Object> m = saveSettlement.execute(in);
        List<Settlement> result = (List<Settlement>) m.get("#result-set-1");
        Settlement settlement = result!=null && !result.isEmpty() ? result.get(0) : null;
        return settlement;
    }

    public Map getAll(int pageNumber, int maxSize) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber", pageNumber*maxSize, Types.INTEGER)
                .addValue("pageSize", maxSize, Types.INTEGER);
        Map<String, Object> m = getAllSettlement.execute(in);

        List<Settlement> result = (List<Settlement>) m.get("#result-set-1");
        Integer totalCount = (Integer) m.get("#result-set-2");
        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", totalCount);
        return returnList;
    }

    public Map getSettlementByFilter(SettlementFilterRequest settlementFilterRequest, int pageNumber, int pageSize) {
        logger.info("result...{}", settlementFilterRequest.getSettlementStartDate());
        logger.info("result...{}", settlementFilterRequest.getSettlementEndDate());
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("settlementStatus", settlementFilterRequest.getSettlementStatus())
                .addValue("startDate", settlementFilterRequest.getSettlementStartDate())
                .addValue("endDate", settlementFilterRequest.getSettlementEndDate())
                .addValue("pageSize", pageSize)
                .addValue("pageNumber", pageNumber);
        Map<String, Object> m = getSettlementByFilter.execute(in);

        List<Settlement> result = (List<Settlement>) m.get("#result-set-1");
        List<Integer> resultInt = (List<Integer>) m.get("#result-set-2");
        logger.info("result...{}", result);
        Map returnMap = new HashMap();
        returnMap.put("list", result);
        returnMap.put("totalCount", resultInt.get(0));

        return returnMap;
    }

}
