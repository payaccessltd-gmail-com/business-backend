package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.enums.PaymentRequestType;
import com.jamub.payaccess.api.models.PaymentRequest;
import com.jamub.payaccess.api.models.PaymentRequest;
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
import java.util.*;


@Repository
public class PaymentRequestDao implements Dao<PaymentRequest>{
    JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall createPaymentRequest;
    private SimpleJdbcCall updatePaymentRequest;
    private SimpleJdbcCall getPaymentRequestByPagination;
    private SimpleJdbcCall getPaymentRequestById;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public void setDataSource(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);

        createPaymentRequest = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("CreatePaymentRequest")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(PaymentRequest.class));

        updatePaymentRequest = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdatePaymentRequest")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(PaymentRequest.class));

        getPaymentRequestById = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetPaymentRequestById")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(PaymentRequest.class));

        getPaymentRequestByPagination = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetPaymentRequestByPagination")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(PaymentRequest.class))
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
    public Optional<PaymentRequest> get(Long id) {
        return Optional.empty();
    }

    @Override
    public Map getAll() {
        Map returnList = new HashMap();
        returnList.put("list", new ArrayList<PaymentRequest>());
        returnList.put("totalCount", 100);
        return returnList;
    }

    @Override
    public PaymentRequest update(PaymentRequest paymentRequest) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantCode", paymentRequest.getMerchantCode())
                .addValue("terminalCode", paymentRequest.getTerminalCode())
                .addValue("orderRef", paymentRequest.getOrderRef())
                .addValue("paymentRequestType", paymentRequest.getPaymentRequestType())
                .addValue("requestBody", paymentRequest.getRequestBody())
                .addValue("paymentRequestId", paymentRequest.getId())
                .addValue("responseBody", paymentRequest.getResponseBody());



        Map<String, Object> m = updatePaymentRequest.execute(in);
        logger.info("{}", m);
        List<PaymentRequest> result = (List<PaymentRequest>) m.get("#result-set-1");
        return result.get(0);
    }

    @Override
    public void delete(PaymentRequest PaymentRequest) {

    }

    public PaymentRequest createNewPaymentRequest(String merchantCode, String terminalCode, String orderRef, PaymentRequestType paymentRequestType, String requestBody) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantCode", merchantCode)
                .addValue("terminalCode", terminalCode)
                .addValue("orderRef", orderRef)
                .addValue("paymentRequestType", paymentRequestType)
                .addValue("requestBody", requestBody);


        Map<String, Object> m = createPaymentRequest.execute(in);
        logger.info("{}", m);
        List<PaymentRequest> result = (List<PaymentRequest>) m.get("#result-set-1");
        return result.get(0);
    }

    public Map getPaymentRequestsByPagination(Integer pageNumber, Integer pageSize) {

        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber", pageNumber)
                .addValue("pageSize", pageSize);

        Map<String, Object> m = getPaymentRequestByPagination.execute(in);
        logger.info("{}", m);
        List<PaymentRequest> result = (List<PaymentRequest>) m.get("#result-set-1");
        List<Integer> totalCount = (List<Integer>) m.get("#result-set-2");
        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", totalCount.get(0));
        return returnList;
    }

    public PaymentRequest getPaymentRequestsById(Long paymentRequestId) {

        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("paymentRequestId", paymentRequestId);

        Map<String, Object> m = getPaymentRequestById.execute(in);
        logger.info("{}", m);
        List<PaymentRequest> returnList = (List<PaymentRequest>) m.get("#result-set-1");
        return returnList!=null && !returnList.isEmpty() ? returnList.get(0) : null;
    }
}
