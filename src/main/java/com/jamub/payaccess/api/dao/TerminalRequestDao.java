package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
import com.jamub.payaccess.api.models.TerminalRequest;
import com.jamub.payaccess.api.models.User;
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
public class TerminalRequestDao implements Dao<TerminalRequest>{

    JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall getTerminalRequest;
    private SimpleJdbcCall getAllTerminalRequests;
    private SimpleJdbcCall getTerminalRequestsByFilter;
    private SimpleJdbcCall saveTerminalRequest;
    private SimpleJdbcCall updateTerminalRequest;
    private SimpleJdbcCall deleteTerminalRequest;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public void setDataSource(DataSource ds)
    {
        this.jdbcTemplate = new JdbcTemplate(ds);
        getTerminalRequest = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetTerminal")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(TerminalRequest.class));

        getAllTerminalRequests = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetAllTerminalRequests")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(TerminalRequest.class));

        getTerminalRequestsByFilter = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("FilterTerminalRequests")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(TerminalRequest.class));

        saveTerminalRequest = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SaveTerminalRequest")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(TerminalRequest.class));

        updateTerminalRequest = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateTerminalRequest")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(TerminalRequest.class));


    }

    @Override
    public Optional<TerminalRequest> get(Long id) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("terminalRequestId", id);
        Map<String, Object> m = getTerminalRequest.execute(in);

        if(m.isEmpty()) {
            return Optional.empty();
        }
        else
        {
            List<TerminalRequest> result = (List<TerminalRequest>) m.get("#result-set-1");
            return Optional.of(result.get(0));
        }
    }


    public Optional<TerminalRequest> get(Long id, Long merchantId) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("terminalRequestId", id)
                .addValue("merchantId", merchantId);
        Map<String, Object> m = getTerminalRequest.execute(in);

        if(m.isEmpty()) {
            return Optional.empty();
        }
        else
        {
            List<TerminalRequest> result = (List<TerminalRequest>) m.get("#result-set-1");
            return Optional.of(result.get(0));
        }
    }

    @Override
    public List<TerminalRequest> getAll() {
        MapSqlParameterSource in = new MapSqlParameterSource();
        Map<String, Object> m = getAllTerminalRequests.execute(in);

        List<TerminalRequest> result = (List<TerminalRequest>) m.get("#result-set-1");
        return result;
    }


    @Override
    public TerminalRequest update(TerminalRequest terminal) {
        return null;
    }

    @Override
    public void delete(TerminalRequest terminal) {

    }


    public TerminalRequest saveTerminalRequest(TerminalOrderRequest terminalOrderRequest, User authenticatedUser) {

        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("terminalBrand", terminalOrderRequest.getTerminalBrand())
                .addValue("quantity", terminalOrderRequest.getQuantity())
                .addValue("terminalType", terminalOrderRequest.getTerminalType())
                .addValue("merchantId", terminalOrderRequest.getMerchantId())
                .addValue("userId", authenticatedUser.getId());
        Map<String, Object> m = saveTerminalRequest.execute(in);
        List<TerminalRequest> result = (List<TerminalRequest>) m.get("#result-set-1");
        TerminalRequest terminalRequest = result!=null && !result.isEmpty() ? result.get(0) : null;
        return terminalRequest;
    }

    public List<TerminalRequest> getAll(int pageNumber, int maxSize) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber", pageNumber*maxSize, Types.INTEGER)
                .addValue("pageSize", maxSize, Types.INTEGER);
        Map<String, Object> m = getAllTerminalRequests.execute(in);

        List<TerminalRequest> result = (List<TerminalRequest>) m.get("#result-set-1");
        return result;
    }

    public List<TerminalRequest> getTerminalRequestsByFilter(String terminalRequestStatus, String terminalBrand, String terminalType, LocalDate startDate, LocalDate endDate, Long merchantId) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("terminalRequestStatus", terminalRequestStatus)
                .addValue("terminalBrand", terminalBrand)
                .addValue("terminalType", terminalType)
                .addValue("startDate", startDate)
                .addValue("endDate", endDate)
                .addValue("merchantId", merchantId);
        Map<String, Object> m = getTerminalRequestsByFilter.execute(in);

        List<TerminalRequest> result = (List<TerminalRequest>) m.get("#result-set-1");
        return result;
    }

}
