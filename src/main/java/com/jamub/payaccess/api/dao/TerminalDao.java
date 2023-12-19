package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
import com.jamub.payaccess.api.enums.TerminalStatus;
import com.jamub.payaccess.api.models.Terminal;
import com.jamub.payaccess.api.models.TerminalRequest;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.CreateTerminalRequest;
import com.jamub.payaccess.api.models.request.TerminalOrderRequest;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TerminalDao implements Dao<Terminal>{

    JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall getTerminal;
    private SimpleJdbcCall getAllTerminals;
    private SimpleJdbcCall getTerminalsByFilter;
    private SimpleJdbcCall saveTerminal;
    private SimpleJdbcCall updateTerminal;
    private SimpleJdbcCall deleteTerminal;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public void setDataSource(DataSource ds)
    {
        this.jdbcTemplate = new JdbcTemplate(ds);
        getTerminal = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetTerminal")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Terminal.class));

        getAllTerminals = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetAllTerminals")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Terminal.class))
                .returningResultSet("#result-set-2", new BeanPropertyRowMapper<Integer>()
                {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        return rs.getInt("count");
                    }
                });

        getTerminalsByFilter = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("FilterTerminals")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Terminal.class));

        saveTerminal = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SaveTerminal")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Terminal.class));

        updateTerminal = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateTerminal")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Terminal.class));


    }

    @Override
    public Optional<Terminal> get(Long id) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("terminalId", id);
        Map<String, Object> m = getTerminal.execute(in);

        if(m.isEmpty()) {
            return Optional.empty();
        }
        else
        {
            List<Terminal> result = (List<Terminal>) m.get("#result-set-1");
            return Optional.of(result.get(0));
        }
    }


    public Optional<Terminal> get(Long id, Long merchantId) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("terminalId", id)
                .addValue("merchantId", merchantId);
        Map<String, Object> m = getTerminal.execute(in);

        if(m.isEmpty()) {
            return Optional.empty();
        }
        else
        {
            List<Terminal> result = (List<Terminal>) m.get("#result-set-1");
            return Optional.of(result.get(0));
        }
    }

    @Override
    public Map getAll() {
        MapSqlParameterSource in = new MapSqlParameterSource();
        Map<String, Object> m = getAllTerminals.execute(in);

        List<Terminal> result = (List<Terminal>) m.get("#result-set-1");
        List<Integer> resultInt = (List<Integer>) m.get("#result-set-2");
        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", resultInt.get(0));
        return returnList;
    }


    @Override
    public Terminal update(Terminal terminal) {
        return null;
    }

    @Override
    public void delete(Terminal terminal) {

    }


    public Terminal saveTerminal(CreateTerminalRequest createTerminalRequest, User authenticatedUser) {

        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantId", createTerminalRequest.getMerchantId())
                .addValue("ownedByUserId", authenticatedUser.getId())
                .addValue("terminalCode", createTerminalRequest.getTerminalCode())
                .addValue("serialNo", createTerminalRequest.getTerminalSerialNo())
                .addValue("terminalType", createTerminalRequest.getTerminalType())
                .addValue("terminalBrand", createTerminalRequest.getTerminalBrand())
                .addValue("terminalRequestId", createTerminalRequest.getTerminalRequestId())
                .addValue("acquirerId", createTerminalRequest.getAcquirerId())
                .addValue("terminalKey", RandomStringUtils.randomAlphanumeric(16).toString().toUpperCase())
                .addValue("terminalStatus", TerminalStatus.ACTIVE.name());
        Map<String, Object> m = saveTerminal.execute(in);
        List<Terminal> result = (List<Terminal>) m.get("#result-set-1");
        Terminal terminal = result!=null && !result.isEmpty() ? result.get(0) : null;
        return terminal;
    }

    public Map getAll(int pageNumber, int maxSize) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber", pageNumber*maxSize, Types.INTEGER)
                .addValue("pageSize", maxSize, Types.INTEGER);
        Map<String, Object> m = getAllTerminals.execute(in);

        List<Terminal> result = (List<Terminal>) m.get("#result-set-1");
        Integer totalCount = (Integer) m.get("#result-set-2");
        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", totalCount);
        return returnList;
    }

    public List<Terminal> getTerminalsByFilter(String terminalStatus, String terminalBrand, String terminalType, String startDate,
                                               String endDate, String merchantCode, int pageNumber, int pageSize) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("terminalStatus", terminalStatus)
                .addValue("terminalBrand", terminalBrand)
                .addValue("terminalType", terminalType)
                .addValue("startDate", startDate)
                .addValue("endDate", endDate)
                .addValue("merchantCode", merchantCode)
                .addValue("pageSize", pageSize)
                .addValue("pageNumber", pageNumber);
        Map<String, Object> m = getTerminalsByFilter.execute(in);

        List<Terminal> result = (List<Terminal>) m.get("#result-set-1");
        return result;
    }

    public List<Terminal> getTerminalByTerminalCode(String terminalCode) {
        return null;
    }
}
