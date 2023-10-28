package com.jamub.payaccess.api.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TransactionDao implements Dao<Transaction>{

    JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall getTransaction;
    private SimpleJdbcCall getAllTransactions;
    private SimpleJdbcCall createNewTransaction;
    private SimpleJdbcCall updateTransaction;
    private SimpleJdbcCall getTransactionByOrderRef;

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
                        MerchantRowMapper.newInstance(Transaction.class));
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
    }

    @Override
    public Optional<Transaction> get(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Transaction> getAll() {
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
        return result;
    }


    public List<Transaction> getAll(TransactionFilterRequest transactionFilterRequest) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("transactionStatus", transactionFilterRequest.getTransactionStatus())
                .addValue("merchantCode", transactionFilterRequest.getMerchantCode())
                .addValue("startDate", transactionFilterRequest.getStartDate())
                .addValue("endDate", transactionFilterRequest.getEndDate())
                .addValue("orderRef", transactionFilterRequest.getOrderRef())
                .addValue("switchTransactionRef", transactionFilterRequest.getSwitchTransactionRef())
                .addValue("terminalCode", transactionFilterRequest.getTerminalCode());
        Map<String, Object> m = getAllTransactions.execute(in);

        List<Transaction> result = (List<Transaction>) m.get("#result-set-1");
        return result;
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
}
