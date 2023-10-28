package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
import com.jamub.payaccess.api.enums.TerminalStatus;
import com.jamub.payaccess.api.models.Terminal;
import com.jamub.payaccess.api.models.TerminalRequest;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.CreateTerminalRequest;
import com.jamub.payaccess.api.models.request.TerminalOrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDate;
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
                        MerchantRowMapper.newInstance(Terminal.class));

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
    public List<Terminal> getAll() {
        MapSqlParameterSource in = new MapSqlParameterSource();
        Map<String, Object> m = getAllTerminals.execute(in);

        List<Terminal> result = (List<Terminal>) m.get("#result-set-1");
        return result;
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
                .addValue("terminalStatus", TerminalStatus.ACTIVE.name());
        Map<String, Object> m = saveTerminal.execute(in);
        List<Terminal> result = (List<Terminal>) m.get("#result-set-1");
        Terminal terminal = result!=null && !result.isEmpty() ? result.get(0) : null;
        return terminal;
    }

    public List<Terminal> getAll(int pageNumber, int maxSize) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber", pageNumber*maxSize, Types.INTEGER)
                .addValue("pageSize", maxSize, Types.INTEGER);
        Map<String, Object> m = getAllTerminals.execute(in);

        List<Terminal> result = (List<Terminal>) m.get("#result-set-1");
        return result;
    }

    public List<Terminal> getTerminalsByFilter(String terminalStatus, String terminalBrand, String terminalType, LocalDate startDate, LocalDate endDate, Long merchantId) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("terminalStatus", terminalStatus)
                .addValue("terminalBrand", terminalBrand)
                .addValue("terminalType", terminalType)
                .addValue("startDate", startDate)
                .addValue("endDate", endDate)
                .addValue("merchantId", merchantId);
        Map<String, Object> m = getTerminalsByFilter.execute(in);

        List<Terminal> result = (List<Terminal>) m.get("#result-set-1");
        return result;
    }

    public List<Terminal> getTerminalByTerminalCode(String terminalCode) {
        return null;
    }
}
