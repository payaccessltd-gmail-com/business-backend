package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.models.Settlement;
import com.jamub.payaccess.api.models.SettlementBreakdown;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.CreateSettlementBreakdownRequest;
import com.jamub.payaccess.api.models.request.SettlementBreakdownFilterRequest;
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
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class SettlementBreakdownDao implements Dao<SettlementBreakdown>{

    JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall getSettlementBreakdown;
    private SimpleJdbcCall getAllSettlementBreakdown;
    private SimpleJdbcCall getSettlementBreakdownByFilter;
    private SimpleJdbcCall saveSettlementBreakdown;
    private SimpleJdbcCall updateSettlementBreakdown;
    private SimpleJdbcCall deleteSettlementBreakdown;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public void setDataSource(DataSource ds)
    {
        this.jdbcTemplate = new JdbcTemplate(ds);
        getSettlementBreakdown = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetSettlementBreakdown")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(SettlementBreakdown.class));

        getAllSettlementBreakdown = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetAllSettlementBreakdowns")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(SettlementBreakdown.class))
                .returningResultSet("#result-set-2", new BeanPropertyRowMapper<Integer>()
                {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        return rs.getInt("count");
                    }
                });

        getSettlementBreakdownByFilter = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("FilterSettlementBreakdowns")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(SettlementBreakdown.class))
                .returningResultSet("#result-set-2", new BeanPropertyRowMapper<Integer>()
                {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        return rs.getInt("count");
                    }
                });;

        saveSettlementBreakdown = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SaveSettlementBreakdown")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(SettlementBreakdown.class));

        updateSettlementBreakdown = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateSettlementBreakdown")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(SettlementBreakdown.class));


    }

    @Override
    public Optional<SettlementBreakdown> get(Long id) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("SettlementBreakdownId", id);
        Map<String, Object> m = getSettlementBreakdown.execute(in);

        if(m.isEmpty()) {
            return Optional.empty();
        }
        else
        {
            List<SettlementBreakdown> result = (List<SettlementBreakdown>) m.get("#result-set-1");
            return Optional.of(result.get(0));
        }
    }


    public Optional<SettlementBreakdown> get(Long id, Long merchantId) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("SettlementBreakdownId", id)
                .addValue("merchantId", merchantId);
        Map<String, Object> m = getSettlementBreakdown.execute(in);

        if(m.isEmpty()) {
            return Optional.empty();
        }
        else
        {
            List<SettlementBreakdown> result = (List<SettlementBreakdown>) m.get("#result-set-1");
            return Optional.of(result.get(0));
        }
    }

    @Override
    public Map getAll() {
        MapSqlParameterSource in = new MapSqlParameterSource();
        Map<String, Object> m = getAllSettlementBreakdown.execute(in);

        List<SettlementBreakdown> result = (List<SettlementBreakdown>) m.get("#result-set-1");
        List<Integer> resultInt = (List<Integer>) m.get("#result-set-2");
        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", resultInt.get(0));
        return returnList;
    }


    @Override
    public SettlementBreakdown update(SettlementBreakdown SettlementBreakdown) {
        return null;
    }

    @Override
    public void delete(SettlementBreakdown SettlementBreakdown) {

    }


    public SettlementBreakdown saveSettlementBreakdown(CreateSettlementBreakdownRequest createSettlementBreakdownRequest, User authenticatedUser, String ipAddress) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DecimalFormat df = new DecimalFormat("#,##0.000");
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("settlementId", createSettlementBreakdownRequest.getSettlement().getId())
                .addValue("payAccessCurrency", createSettlementBreakdownRequest.getSettlement().getPayAccessCurrency())
                .addValue("merchantId", createSettlementBreakdownRequest.getMerchantId())
                .addValue("merchantCode", createSettlementBreakdownRequest.getMerchantCode())
                .addValue("businessName", createSettlementBreakdownRequest.getMerchantName())
                .addValue("settlementAmount", createSettlementBreakdownRequest.getSettlementAmount())
                .addValue("createdByUserId", authenticatedUser.getId())
                .addValue("carriedOutByUserFullName", authenticatedUser.getFirstName().concat(" ").concat(authenticatedUser.getLastName()))
                .addValue("userAction", ApplicationAction.SETTLEMENT_BREAKDOWN_GENERATE)
                .addValue("description", "Settlement Breakdown ".
                        concat(" for settlement #").
                        concat(createSettlementBreakdownRequest.getSettlement().getSettlementCode()).
                        concat(" for the merchant code - ").
                        concat(createSettlementBreakdownRequest.getMerchantCode().toUpperCase()).
                        concat(" - for the amount ").
                        concat(createSettlementBreakdownRequest.getSettlement().getPayAccessCurrency().name()).
                        concat(df.format(createSettlementBreakdownRequest.getSettlementAmount())).
                        concat(" and the date ").
                        concat(
                                formatter.format(createSettlementBreakdownRequest.getSettlement().getSettlementDate())
                        )
                )
                .addValue("ipAddress", ipAddress)
                .addValue("objectClassReference", SettlementBreakdown.class.getCanonicalName());
        Map<String, Object> m = saveSettlementBreakdown.execute(in);
        List<SettlementBreakdown> result = (List<SettlementBreakdown>) m.get("#result-set-1");
        SettlementBreakdown SettlementBreakdown = result!=null && !result.isEmpty() ? result.get(0) : null;
        return SettlementBreakdown;
    }

    public Map getAll(int pageNumber, int maxSize) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber", pageNumber*maxSize, Types.INTEGER)
                .addValue("pageSize", maxSize, Types.INTEGER);
        Map<String, Object> m = getAllSettlementBreakdown.execute(in);

        List<SettlementBreakdown> result = (List<SettlementBreakdown>) m.get("#result-set-1");
        Integer totalCount = (Integer) m.get("#result-set-2");
        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", totalCount);
        return returnList;
    }

    public Map getSettlementBreakdownByFilter(SettlementBreakdownFilterRequest SettlementBreakdownFilterRequest, int pageNumber, int pageSize) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantCode", SettlementBreakdownFilterRequest.getMerchantCode())
                .addValue("startDate", SettlementBreakdownFilterRequest.getStartDate())
                .addValue("endDate", SettlementBreakdownFilterRequest.getEndDate())
                .addValue("settlementStatus", SettlementBreakdownFilterRequest.getSettlementStatus())
                .addValue("settlementId", SettlementBreakdownFilterRequest.getSettlementId())


                .addValue("pageSize", pageSize)
                .addValue("pageNumber", pageNumber);
        Map<String, Object> m = getSettlementBreakdownByFilter.execute(in);

        List<SettlementBreakdown> result = (List<SettlementBreakdown>) m.get("#result-set-1");
        List<Integer> resultInt = (List<Integer>) m.get("#result-set-2");
        Map returnMap = new HashMap();
        returnMap.put("list", result);
        returnMap.put("totalCount", resultInt.get(0));

        return returnMap;
    }

}
