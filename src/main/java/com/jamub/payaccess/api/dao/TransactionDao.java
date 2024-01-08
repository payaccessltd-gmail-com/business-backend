package com.jamub.payaccess.api.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
import com.jamub.payaccess.api.dto.SettlementTransactionDTO;
import com.jamub.payaccess.api.enums.MerchantStatus;
import com.jamub.payaccess.api.enums.ServiceType;
import com.jamub.payaccess.api.enums.TransactionStatus;
import com.jamub.payaccess.api.enums.UserStatus;
import com.jamub.payaccess.api.models.*;
import com.jamub.payaccess.api.models.request.*;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TransactionDao implements Dao<Transaction>{

    JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall getTransaction;
    private SimpleJdbcCall getAllTransactions;
    private SimpleJdbcCall getAllTransactionsByMerchantId;
    private SimpleJdbcCall createNewTransaction;
    private SimpleJdbcCall updateTransaction;
    private SimpleJdbcCall getTransactionByOrderRef;
    private SimpleJdbcCall getAllTransactionsForSettlement;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public void setDataSource(DataSource ds)
    {
        this.jdbcTemplate = new JdbcTemplate(ds);
        getTransaction = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetTransactions")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Transaction.class));
        getAllTransactions = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetAllTransactions")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Transaction.class))
                .returningResultSet("#result-set-2", new BeanPropertyRowMapper<Integer>()
                {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        return rs.getInt("count");
                    }
                });
        getAllTransactionsByMerchantId = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetAllTransactionsByMerchantId")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Transaction.class))
                .returningResultSet("#result-set-2", new BeanPropertyRowMapper<Integer>()
                {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        return rs.getInt("count");
                    }
                });
        createNewTransaction = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("CreateNewTransaction")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Transaction.class));
        updateTransaction = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateTransaction")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Transaction.class));
        getTransactionByOrderRef = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetTransactionByOrderRef")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(Transaction.class));
        getAllTransactionsForSettlement = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetAllTransactionsForSettlement")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(SettlementTransactionDTO.class));
    }

    @Override
    public Optional<Transaction> get(Long id) {
        return Optional.empty();
    }

    @Override
    public Map getAll() {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("transactionStatus", null)
                .addValue("merchantCode", null)
                .addValue("startDate", null)
                .addValue("endDate", null)
                .addValue("orderRef", null)
                .addValue("switchTransactionRef", null)
                .addValue("terminalCode", null);
        Map<String, Object> m = getAllTransactions.execute(in);

        List<Transaction> result = (List<Transaction>) m.get("#result-set-1");
        Integer totalCount = (Integer) m.get("#result-set-2");
        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", 100);
        return returnList;
    }


    public Map getAll(TransactionFilterRequest transactionFilterRequest, Integer pageNumber, Integer pageSize) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber", pageNumber)
                .addValue("pageSize", pageSize)
                .addValue("transactionStatus", transactionFilterRequest.getTransactionStatus())
                .addValue("merchantCode", transactionFilterRequest.getMerchantCode())
                .addValue("startDate", transactionFilterRequest.getStartDate())
                .addValue("endDate", transactionFilterRequest.getEndDate())
                .addValue("minAmount", transactionFilterRequest.getMinAmount())
                .addValue("maxAmount", transactionFilterRequest.getMaxAmount())
                .addValue("orderRef", transactionFilterRequest.getOrderRef())
                .addValue("switchTransactionRef", transactionFilterRequest.getSwitchTransactionRef())
                .addValue("terminalCode", transactionFilterRequest.getTerminalCode());
        Map<String, Object> m = getAllTransactions.execute(in);

        List<Transaction> result = (List<Transaction>) m.get("#result-set-1");
        List<Integer> totalCountResult = (List<Integer>) m.get("#result-set-2");
        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", totalCountResult.get(0));
        return returnList;
    }




//    public Map getAll(TransactionFilterRequest transactionFilterRequest, Integer pageNumber, Integer pageSize) {
//        MapSqlParameterSource in = new MapSqlParameterSource()
//                .addValue("pageNumber", pageNumber)
//                .addValue("pageSize", pageSize)
//                .addValue("transactionStatus", transactionFilterRequest.getTransactionStatus())
//                .addValue("merchantCode", transactionFilterRequest.getMerchantCode())
//                .addValue("startDate", transactionFilterRequest.getStartDate())
//                .addValue("endDate", transactionFilterRequest.getEndDate())
//                .addValue("minAmount", transactionFilterRequest.getMinAmount())
//                .addValue("maxAmount", transactionFilterRequest.getMaxAmount())
//                .addValue("orderRef", transactionFilterRequest.getOrderRef())
//                .addValue("switchTransactionRef", transactionFilterRequest.getSwitchTransactionRef())
//                .addValue("terminalCode", transactionFilterRequest.getTerminalCode());
//        Map<String, Object> m = getAllTransactions.execute(in);
//
//        List<Transaction> result = (List<Transaction>) m.get("#result-set-1");
//        List<Integer> totalCountResult = (List<Integer>) m.get("#result-set-2");
//        Map returnList = new HashMap();
//        returnList.put("list", result);
//        returnList.put("totalCount", totalCountResult.get(0));
//        return returnList;
//    }


    public Map getAllByMerchantId(TransactionFilterRequest transactionFilterRequest, Integer pageNumber, Integer pageSize, Long merchantId) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber", pageNumber)
                .addValue("pageSize", pageSize)
                .addValue("transactionStatus", transactionFilterRequest==null ? null : transactionFilterRequest.getTransactionStatus())
                .addValue("merchantId", merchantId)
                .addValue("startDate", transactionFilterRequest==null ? null : transactionFilterRequest.getStartDate())
                .addValue("endDate", transactionFilterRequest==null ? null : transactionFilterRequest.getEndDate())
                .addValue("orderRef", transactionFilterRequest==null ? null : transactionFilterRequest.getOrderRef())
                .addValue("minAmount", transactionFilterRequest.getMinAmount())
                .addValue("maxAmount", transactionFilterRequest.getMaxAmount())
                .addValue("switchTransactionRef", transactionFilterRequest==null ? null : transactionFilterRequest.getSwitchTransactionRef())
                .addValue("terminalCode", transactionFilterRequest==null ? null : transactionFilterRequest.getTerminalCode());
        Map<String, Object> m = getAllTransactionsByMerchantId.execute(in);

        List<Transaction> result = (List<Transaction>) m.get("#result-set-1");
        List<Integer> totalCountResult = (List<Integer>) m.get("#result-set-2");
        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", totalCountResult.get(0));
        return returnList;
    }

    @Override
    public Transaction update(Transaction transaction) {
        return null;
    }

    @Override
    public void delete(Transaction transaction) {

    }

    public Transaction createNewTransaction(InitiateTransactionRequest initiateTransactionRequest, Merchant merchant, Terminal terminal, String messageRequest) {

        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("customData", initiateTransactionRequest.getCustomData())
                .addValue("payAccessCurrency", initiateTransactionRequest.getCurrencyCode())
                .addValue("amount", initiateTransactionRequest.getAmount())
                .addValue("orderRef", initiateTransactionRequest.getOrderRef())
                .addValue("payerEmail", initiateTransactionRequest.getCustomerId())
                .addValue("channel", initiateTransactionRequest.getChannel())
                .addValue("redirectUrl", initiateTransactionRequest.getRedirectUrl())
                .addValue("terminalId", terminal!=null ? terminal.getId() : null)
                .addValue("merchantId", merchant.getId())
                .addValue("messageRequest", messageRequest)
                .addValue("serviceType", ServiceType.DEBIT_CARD)
                .addValue("transactionStatus", TransactionStatus.PENDING)
                .addValue("otp", null)
                .addValue("merchantCode", merchant.getMerchantCode())
                .addValue("acquirerId", terminal!=null ? terminal.getAcquirerId() : null)
                .addValue("merchantName", merchant.getBusinessName());
        Map<String, Object> m = createNewTransaction.execute(in);
        List<Transaction> result = (List<Transaction>) m.get("#result-set-1");
        Transaction transaction = result!=null && !result.isEmpty() ? result.get(0) : null;
        return transaction;


    }

    public Transaction updateTransaction(Transaction transaction) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("customData", transaction.getCustomData())
                .addValue("payAccessCurrency", transaction.getPayAccessCurrency())
                .addValue("amount", transaction.getAmount())
                .addValue("orderRef", transaction.getOrderRef())
                .addValue("payerEmail", transaction.getCustomerId())
                .addValue("channel", transaction.getChannel())
                .addValue("redirectUrl", transaction.getRedirectUrl())
                .addValue("terminalId", transaction.getTerminalId())
                .addValue("merchantId", transaction.getMerchantId())
                .addValue("messageRequest", transaction.getMessageRequest())
                .addValue("serviceType", transaction.getServiceType())
                .addValue("transactionStatus", transaction.getTransactionStatus())
                .addValue("otp", transaction.getOtp())
                .addValue("merchantCode", transaction.getMerchantCode())
                .addValue("acquirerId", transaction.getAcquirerId())
                .addValue("merchantName", transaction.getMerchantName())
                .addValue("createdAt", transaction.getCreatedAt())
                .addValue("deletedAt", transaction.getDeletedAt())
                .addValue("customerId", transaction.getCustomerId())
                .addValue("messageResponse", transaction.getMessageResponse())
                .addValue("payerMobile", transaction.getPayerMobile())
                .addValue("payerName", transaction.getPayerName())
                .addValue("poolId", transaction.getPoolId())
                .addValue("postCreditSourceBalance", transaction.getPostCreditSourceBalance())
                .addValue("postDebitSourceBalance", transaction.getPostDebitSourceBalance())
                .addValue("preCreditSourceBalance", transaction.getPreCreditSourceBalance())
                .addValue("preDebitSourceBalance", transaction.getPreDebitSourceBalance())
                .addValue("recipientCustomerId", transaction.getRecipientCustomerId())
                .addValue("summary", transaction.getSummary())
                .addValue("transactionCharge", transaction.getTransactionCharge())
                .addValue("transactionDetail", transaction.getTransactionDetail())
                .addValue("transactionRef", transaction.getTransactionRef())
                .addValue("destinationWalletId", transaction.getDestinationWalletId())
                .addValue("destinationWalletNumber", transaction.getDestinationWalletNumber())
                .addValue("sourceWalletId", transaction.getSourceWalletId())
                .addValue("sourceWalletNumber", transaction.getSourceWalletNumber())
                .addValue("transactionReceipientDetails", transaction.getTransactionReceipientDetails())
                .addValue("transactionRemark", transaction.getTransactionRemark())
                .addValue("transactionSourceDetails", transaction.getTransactionSourceDetails())
                .addValue("switchtransactionref", transaction.getSwitchTransactionRef())
                .addValue("transactionId", transaction.getId());

        Map<String, Object> m = updateTransaction.execute(in);
        List<Transaction> result = (List<Transaction>) m.get("#result-set-1");
        transaction = result!=null && !result.isEmpty() ? result.get(0) : null;
        return transaction;
    }

    public Transaction getTransactionByOrderRef(String orderRef, String merchantCode) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("merchantCode", merchantCode)
                .addValue("orderRef", orderRef);

        Map<String, Object> m = getTransactionByOrderRef.execute(in);
        List<Transaction> result = (List<Transaction>) m.get("#result-set-1");
        Transaction transaction = result!=null && !result.isEmpty() ? result.get(0) : null;
        return transaction;
    }

    public List getAllTransactionsForSettlement(TransactionFilterRequest transactionFilterRequest, TransactionStatus transactionStatus) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("transactionStatus", transactionStatus)
//                .addValue("merchantCode", transactionFilterRequest.getMerchantCode())
                .addValue("startDate", transactionFilterRequest.getStartDate())
                .addValue("endDate", transactionFilterRequest.getEndDate());

        Map<String, Object> m = getAllTransactionsForSettlement.execute(in);
        List<SettlementTransactionDTO> result = (List<SettlementTransactionDTO>) m.get("#result-set-1");
        return result;
    }

}
