package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
import com.jamub.payaccess.api.models.AuditTrail;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.AuditTrailFilterRequest;
import com.jamub.payaccess.api.models.request.GetMerchantFilterRequest;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class AuditTrailDao implements Dao<AuditTrail>{

    JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall getAuditTrail;
    private SimpleJdbcCall getAllAuditTrails;
    private SimpleJdbcCall getAuditTrailsByFilter;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public void setDataSource(DataSource ds)
    {
        this.jdbcTemplate = new JdbcTemplate(ds);
        getAuditTrail = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetAuditTrail")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(AuditTrail.class));

        getAllAuditTrails = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetAllAuditTrails")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(AuditTrail.class))
                .returningResultSet("#result-set-2", new BeanPropertyRowMapper<Integer>()
                {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        return rs.getInt("count");
                    }
                });

        getAuditTrailsByFilter = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("FilterAuditTrails")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(AuditTrail.class));


    }

    @Override
    public Optional<AuditTrail> get(Long id) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("AuditTrailId", id);
        Map<String, Object> m = getAuditTrail.execute(in);

        if(m.isEmpty()) {
            return Optional.empty();
        }
        else
        {
            List<AuditTrail> result = (List<AuditTrail>) m.get("#result-set-1");
            return Optional.of(result.get(0));
        }
    }


    public Optional<AuditTrail> get(Long id, Long merchantId) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("AuditTrailId", id)
                .addValue("merchantId", merchantId);
        Map<String, Object> m = getAuditTrail.execute(in);

        if(m.isEmpty()) {
            return Optional.empty();
        }
        else
        {
            List<AuditTrail> result = (List<AuditTrail>) m.get("#result-set-1");
            return Optional.of(result.get(0));
        }
    }

    @Override
    public Map getAll() {
        MapSqlParameterSource in = new MapSqlParameterSource();
        Map<String, Object> m = getAllAuditTrails.execute(in);

        List<AuditTrail> result = (List<AuditTrail>) m.get("#result-set-1");
        List<Integer> resultInt = (List<Integer>) m.get("#result-set-2");
        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", resultInt.get(0));
        return returnList;
    }


    @Override
    public AuditTrail update(AuditTrail AuditTrail) {
        return null;
    }

    @Override
    public void delete(AuditTrail AuditTrail) {

    }



    public Map getAll(int pageNumber, int maxSize, AuditTrailFilterRequest auditTrailFilterRequest) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber", pageNumber*maxSize, Types.INTEGER)
                .addValue("pageSize", maxSize, Types.INTEGER)
                .addValue("startDate", auditTrailFilterRequest.getStartDate())
                .addValue("endDate", auditTrailFilterRequest.getEndDate())
                .addValue("auditTrailAction", auditTrailFilterRequest.getAuditTrailAction())
                .addValue("actorUserId", auditTrailFilterRequest.getActorUserId());

        logger.info("{}", in.getValues());
        Map<String, Object> m = getAllAuditTrails.execute(in);

        List<AuditTrail> result = (List<AuditTrail>) m.get("#result-set-1");
        Integer totalCount = (Integer) m.get("#result-set-2");
        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", totalCount);
        return returnList;
    }

    public List<AuditTrail> getAuditTrailsByFilter(String AuditTrailStatus, String AuditTrailBrand, String AuditTrailType, String startDate,
                                               String endDate, String merchantCode, int pageNumber, int pageSize) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("AuditTrailStatus", AuditTrailStatus)
                .addValue("AuditTrailBrand", AuditTrailBrand)
                .addValue("AuditTrailType", AuditTrailType)
                .addValue("startDate", startDate)
                .addValue("endDate", endDate)
                .addValue("merchantCode", merchantCode)
                .addValue("pageSize", pageSize)
                .addValue("pageNumber", pageNumber);
        Map<String, Object> m = getAuditTrailsByFilter.execute(in);

        List<AuditTrail> result = (List<AuditTrail>) m.get("#result-set-1");
        return result;
    }

    public List<AuditTrail> getAuditTrailByAuditTrailCode(String AuditTrailCode) {
        return null;
    }
}
