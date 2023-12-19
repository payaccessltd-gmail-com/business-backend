package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
import com.jamub.payaccess.api.enums.*;
import com.jamub.payaccess.api.models.Invoice;
import com.jamub.payaccess.api.models.InvoiceBreakdown;
import com.jamub.payaccess.api.models.Merchant;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
public class InvoiceDao implements Dao<Invoice>{

    JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall getInvoice;

    private SimpleJdbcCall getInvoiceByInvoiceNumberAndMerchantCode;
    private SimpleJdbcCall getInvoiceBreakdown;
    private SimpleJdbcCall getAllInvoices;
    private SimpleJdbcCall getInvoiceByFilter;
    private SimpleJdbcCall saveInvoice;
    private SimpleJdbcCall saveInvoiceBreakdown;
    private SimpleJdbcCall updateInvoice;
    private SimpleJdbcCall deleteInvoice;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public void setDataSource(DataSource ds)
    {
        this.jdbcTemplate = new JdbcTemplate(ds);
        getInvoice = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetInvoice")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Invoice.class));

        getInvoiceByInvoiceNumberAndMerchantCode = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetInvoiceByInvoiceNumberAndMerchantCode")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Invoice.class));

        getInvoiceBreakdown = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetInvoiceBreakdown")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(InvoiceBreakdown.class));

        getAllInvoices = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetAllInvoices")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Invoice.class))
                .returningResultSet("#result-set-2", new BeanPropertyRowMapper<Integer>()
                {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        return rs.getInt("count");
                    }
                });

        getInvoiceByFilter = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("FilterInvoices")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Invoice.class));

        saveInvoice = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SaveInvoice")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Invoice.class));

        saveInvoiceBreakdown = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SaveInvoiceBreakdown")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(InvoiceBreakdown.class));

        updateInvoice = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateInvoice")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Invoice.class));

        deleteInvoice = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("DeleteInvoice")
                .returningResultSet("#result-set-1", new BeanPropertyRowMapper<Integer>()
                {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        return rs.getInt("count");
                    }
                });


    }

    @Override
    public Optional<Invoice> get(Long id) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("invoiceId", id);
        Map<String, Object> m = getInvoice.execute(in);

        if(m.isEmpty()) {
            return Optional.empty();
        }
        else
        {
            List<Invoice> result = (List<Invoice>) m.get("#result-set-1");
            return Optional.of(result.get(0));
        }
    }


    public Optional<Invoice> get(Long id, Long merchantId) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("invoiceId", id)
                .addValue("merchantId", merchantId);
        Map<String, Object> m = getInvoice.execute(in);

        if(m.isEmpty()) {
            return Optional.empty();
        }
        else
        {
            List<Invoice> result = (List<Invoice>) m.get("#result-set-1");
            if(result.isEmpty())
                return Optional.empty();
            return Optional.of(result.get(0));
        }
    }




    public Optional<Invoice> getInvoiceByInvoiceNumberAndMerchantCode(String invoiceNumber, String merchantCode) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("invoiceNumber", invoiceNumber)
                .addValue("merchantCode", merchantCode);
        Map<String, Object> m = getInvoiceByInvoiceNumberAndMerchantCode.execute(in);

        if(m.isEmpty()) {
            return Optional.empty();
        }
        else
        {
            List<Invoice> result = (List<Invoice>) m.get("#result-set-1");
            if(result.isEmpty())
                return Optional.empty();
            return Optional.of(result.get(0));
        }
    }



    @Override
    public Map getAll() {
        MapSqlParameterSource in = new MapSqlParameterSource();
        Map<String, Object> m = getAllInvoices.execute(in);

        List<Invoice> result = (List<Invoice>) m.get("#result-set-1");
        List<Integer> totalCountResult = (List<Integer>) m.get("#result-set-2");

        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", totalCountResult.get(0));

        return returnList;
    }


    @Override
    public Invoice update(Invoice invoice) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("invoiceType", invoice.getInvoiceType())
                .addValue("customerName", invoice.getCustomerName())
                .addValue("customerEmail", invoice.getCustomerEmail())
                .addValue("additionalCustomerEmailAddress", invoice.getAdditionalCustomerEmailAddress())
                .addValue("dueDate", invoice.getDueDate())
                .addValue("amount", invoice.getAmount())
                .addValue("userId", invoice.getCreatedByUserId())
                .addValue("invoiceNote", invoice.getInvoiceNote())
                .addValue("businessLogo", invoice.getBusinessLogo())
                .addValue("merchantId", invoice.getCreatedByMerchantId())
                .addValue("userId", invoice.getCreatedByUserId())
                .addValue("taxamount", invoice.getTaxAmount())
                .addValue("shippingFee", invoice.getShippingFee())
                .addValue("invoiceStatus", invoice.getInvoiceStatus())
                .addValue("qrFileName", invoice.getQrFileName())
                .addValue("deletedAt", invoice.getDeletedAt()==null ? Types.NULL : invoice.getDeletedAt().format(formatter))
                .addValue("invoiceId", invoice.getId());
        Map<String, Object> m = updateInvoice.execute(in);
        List<Invoice> result = (List<Invoice>) m.get("#result-set-1");
        Invoice merchant = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchant;
    }

    @Override
    public void delete(Invoice invoice) {

    }


    public Invoice saveInvoice(String invoiceType, String customerName, String customerEmailAddress, String additionalCustomerEmailAddress,
                               LocalDate dueDate, BigDecimal amount, String invoiceNote, String logoFileName, Long merchantId, User authenticatedUser,
                               String referenceNumber, String invoiceStatus, BigDecimal taxPercent, BigDecimal discountAmount, String discountType,
                               BigDecimal shippingFee) {

        logger.info("{}, {} {} {} {}", merchantId, authenticatedUser.getId(), customerEmailAddress, customerName, invoiceStatus);
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("invoiceType", invoiceType)
                .addValue("customerName", customerName)
                .addValue("referenceNumber", referenceNumber)
                .addValue("customerEmail", customerEmailAddress)
                .addValue("additionalCustomerEmailAddress", additionalCustomerEmailAddress)
                .addValue("dueDate", dueDate)
                .addValue("amount", amount)
                .addValue("createdByUserId", authenticatedUser.getId())
                .addValue("invoiceNote", invoiceNote)
                .addValue("businesslogo", logoFileName)
                .addValue("taxAmount", taxPercent)
                .addValue("shippingFee", shippingFee)
                .addValue("discountAmount", discountAmount)
                .addValue("discountType", discountType)
                .addValue("invoiceStatus", invoiceStatus!=null && invoiceStatus.equals("DRAFT") ? InvoiceStatus.DRAFT.name() : InvoiceStatus.PENDING.name())
//                .addValue("invoicestatus", InvoiceStatus.PENDING.name())
                .addValue("createdbymerchantid", merchantId);
        Map<String, Object> m = saveInvoice.execute(in);
        List<Invoice> result = (List<Invoice>) m.get("#result-set-1");
        Invoice invoice = result!=null && !result.isEmpty() ? result.get(0) : null;
        return invoice;
    }

    public Map getAll(GetInvoiceFilterRequest getInvoiceFilterRequest, int pageNumber, int maxSize, Long merchantId) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantId", merchantId)
                .addValue("pageNumber", pageNumber*maxSize, Types.INTEGER)
                .addValue("pageSize", maxSize, Types.INTEGER);

        if(getInvoiceFilterRequest!=null)
        {
            in.addValue("invoiceStatus", getInvoiceFilterRequest.getInvoiceStatus())
                    .addValue("creationStartDate", getInvoiceFilterRequest.getCreationStartDate())
                    .addValue("creationEndDate", getInvoiceFilterRequest.getCreationEndDate())
                    .addValue("dueDateStartDate", getInvoiceFilterRequest.getDueDateStartDate())
                    .addValue("dueDateEndDate", getInvoiceFilterRequest.getDueDateEndDate())
                    .addValue("minAmount", getInvoiceFilterRequest.getMinAmount())
                    .addValue("maxAmount", getInvoiceFilterRequest.getMaxAmount());
        }
        else
        {
            in.addValue("invoiceStatus", "")
                    .addValue("creationStartDate", "")
                    .addValue("creationEndDate", "")
                    .addValue("dueDateStartDate", "")
                    .addValue("dueDateEndDate", "")
                    .addValue("minAmount", "")
                    .addValue("maxAmount", "");
        }
        Map<String, Object> m = getAllInvoices.execute(in);

        List<Invoice> result = (List<Invoice>) m.get("#result-set-1");
        List<Integer> totalCount = (ArrayList<Integer>)m.get("#result-set-2");
        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", totalCount.get(0));
        return returnList;
    }

    public List<Invoice> getInvoiceByFilter(String invoiceStatus, String emailAddress, String startDate, String endDate, Long userId, Long merchantId) {
        logger.info("{} {} {} {} {} {}", invoiceStatus, emailAddress, startDate, endDate, merchantId, userId);
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("invoiceStatus", invoiceStatus)
                .addValue("emailAddress", emailAddress)
                .addValue("startDate", startDate)
                .addValue("endDate", endDate)
                .addValue("merchantId", merchantId)
                .addValue("userId", userId);
        Map<String, Object> m = getInvoiceByFilter.execute(in);

        List<Invoice> result = (List<Invoice>) m.get("#result-set-1");
        return result;
    }

    public InvoiceBreakdown saveInvoiceBreakDown(Long invoiceId, String invoiceItem, Integer quantity, BigDecimal costPerUnit) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("invoiceId", invoiceId)
                .addValue("invoiceItem", invoiceItem)
                .addValue("quantity", quantity)
                .addValue("costPerUnit", costPerUnit);
        Map<String, Object> m = saveInvoiceBreakdown.execute(in);
        List<InvoiceBreakdown> result = (List<InvoiceBreakdown>) m.get("#result-set-1");
        InvoiceBreakdown invoiceBreakdown = result!=null && !result.isEmpty() ? result.get(0) : null;
        return invoiceBreakdown;
    }


    public List<InvoiceBreakdown> getInvoiceBreakdownByInvoiceId(Long invoiceId, Long merchantId)
    {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantId", merchantId)
                .addValue("invoiceId", invoiceId);
        Map<String, Object> m = getInvoiceBreakdown.execute(in);
        List<InvoiceBreakdown> invoiceBreakdownList = (List<InvoiceBreakdown>) m.get("#result-set-1");
        return invoiceBreakdownList;
    }

    public Integer deleteInvoice(Long invoiceId, Long merchantId)
    {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantId", merchantId)
                .addValue("invoiceId", invoiceId);
        Map<String, Object> m = deleteInvoice.execute(in);
        List<Integer> successCheck = (List<Integer>) m.get("#result-set-1");
        return successCheck.get(0);
    }
}
