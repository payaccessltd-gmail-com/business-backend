package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.enums.TicketStatus;
import com.jamub.payaccess.api.models.TransactionTicket;
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
public class TicketDao implements Dao<TransactionTicket>{
    JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall createNewTransactionTicket;
    private SimpleJdbcCall getTransactionTicketByTicketNumber;

    private SimpleJdbcCall getTransactionTicketByPagination;

    private SimpleJdbcCall assignTransactionTicket;

    private SimpleJdbcCall closeTransactionTicket;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public void setDataSource(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);

        createNewTransactionTicket = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("CreateNewTransactionTicket")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(TransactionTicket.class));

        getTransactionTicketByTicketNumber = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetTransactionTicketByTicketNumber")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(TransactionTicket.class));

        getTransactionTicketByPagination = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetTransactionTicketByPagination")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(TransactionTicket.class))
                .returningResultSet("#result-set-2", new BeanPropertyRowMapper<Integer>()
                {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        return rs.getInt("count");
                    }
                });

        assignTransactionTicket = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("AssignTransactionTicket")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(TransactionTicket.class));
        closeTransactionTicket = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("CloseTransactionTicket")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(TransactionTicket.class));
    }


    @Override
    public Optional<TransactionTicket> get(Long id) {
        return Optional.empty();
    }

    @Override
    public Map getAll() {
        Map returnList = new HashMap();
        returnList.put("list", new ArrayList<TransactionTicket>());
        returnList.put("totalCount", 100);
        return returnList;
    }

    @Override
    public TransactionTicket update(TransactionTicket transactionTicket) {
        return null;
    }

    @Override
    public void delete(TransactionTicket transactionTicket) {

    }

    public TransactionTicket createNewTransactionTicket(String productCategory, String orderRef, String ticketMessage, String ticketAttachmentFile,
                                                        Long actorId, String ipAddress, String description,
                              ApplicationAction userAction, String carriedOutByUserFullName, String objectClassReference,
                              Long objectIdReference) {
        String ticketNumber = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("ticketCategory", productCategory)
                .addValue("orderRef", orderRef)
                .addValue("ticketMessage", ticketMessage)
                .addValue("ticketNumber", ticketNumber)
                .addValue("createdByUserId", actorId)
                .addValue("attachmentImage", ticketAttachmentFile)
                .addValue("carriedOutByUserFullName", carriedOutByUserFullName)
                .addValue("userAction", userAction.name())
                .addValue("description", description)
                .addValue("ipAddress", ipAddress)
                .addValue("objectClassReference", objectClassReference)
                .addValue("objectIdReference", objectIdReference)
                .addValue("ticketStatus", TicketStatus.OPEN)
                .addValue("carriedOutByUserId", actorId);


        Map<String, Object> m = createNewTransactionTicket.execute(in);
        logger.info("{}", m);
        List<TransactionTicket> result = (List<TransactionTicket>) m.get("#result-set-1");
        return result.get(0);
    }

    public TransactionTicket getTransactionTicketByTicketNumber(String ticketNumber) {

        String otp = RandomStringUtils.randomNumeric(4);
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("ticketNumber", ticketNumber);

        Map<String, Object> m = getTransactionTicketByTicketNumber.execute(in);
        logger.info("{}", m);
        List<TransactionTicket> result = (List<TransactionTicket>) m.get("#result-set-1");
        return result.isEmpty() ? null : result.get(0);
    }

    public Map getTransactionTicketByPagination(Integer pageNumber, Integer pageSize, Long merchantId) {

        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantId", merchantId)
                .addValue("pageNumber", pageNumber)
                .addValue("pageSize", pageSize);

        Map<String, Object> m = getTransactionTicketByPagination.execute(in);
        logger.info("{}", m);
        List<TransactionTicket> result = (List<TransactionTicket>) m.get("#result-set-1");
        List<Integer> totalCount = (List<Integer>) m.get("#result-set-2");
        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", totalCount.get(0));
        return returnList;
    }

    public TransactionTicket assignTransactionTicket(String ticketNumber, Long assignToUserId, Long actorId, String ipAddress,
                                                     String description, ApplicationAction userAction, String carriedOutByUserFullName,
                                                     String objectClassReference, long objectIdReference) {

        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("ticketNumber", ticketNumber)
                .addValue("assignToUserId", assignToUserId)
                .addValue("createdByUserId", actorId)
                .addValue("carriedOutByUserFullName", carriedOutByUserFullName)
                .addValue("userAction", userAction.name())
                .addValue("description", description)
                .addValue("ipAddress", ipAddress)
                .addValue("objectClassReference", objectClassReference)
                .addValue("objectIdReference", objectIdReference)
                .addValue("carriedOutByUserId", actorId);


        Map<String, Object> m = assignTransactionTicket.execute(in);
        logger.info("{}", m);
        List<TransactionTicket> result = (List<TransactionTicket>) m.get("#result-set-1");
        return result.get(0);
    }

    public TransactionTicket closeTransactionTicket(String ticketNumber, long closedByUserId, Long actorId, String ipAddress, String description,
                                                    ApplicationAction userAction, String carriedOutByUserFullName, String objectClassReference,
                                                    Long objectIdReference) {

        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("ticketNumber", ticketNumber)
                .addValue("closedByUserId", closedByUserId)
                .addValue("carriedOutByUserFullName", carriedOutByUserFullName)
                .addValue("userAction", userAction.name())
                .addValue("description", description)
                .addValue("ipAddress", ipAddress)
                .addValue("objectClassReference", objectClassReference)
                .addValue("objectIdReference", objectIdReference)
                .addValue("actorId", actorId);


        Map<String, Object> m = closeTransactionTicket.execute(in);
        logger.info("{}", m);
        List<TransactionTicket> result = (List<TransactionTicket>) m.get("#result-set-1");
        return result.get(0);
    }
}
