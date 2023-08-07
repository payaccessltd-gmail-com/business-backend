package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
import com.jamub.payaccess.api.enums.MerchantStatus;
import com.jamub.payaccess.api.enums.UserStatus;
import com.jamub.payaccess.api.models.Merchant;
import com.jamub.payaccess.api.models.Transaction;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.MerchantBusinessBankAccountDataUpdateRequest;
import com.jamub.payaccess.api.models.request.MerchantBusinessDataUpdateRequest;
import com.jamub.payaccess.api.models.request.MerchantSignUpRequest;
import com.jamub.payaccess.api.models.request.MerchantUserBioDataUpdateRequest;
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
    }

    @Override
    public Optional<Transaction> get(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Transaction> getAll() {
        MapSqlParameterSource in = new MapSqlParameterSource();
        Map<String, Object> m = getAllTransactions.execute(in);

        List<Transaction> result = (List<Transaction>) m.get("#result-set-1");
        return result;
    }

    @Override
    public void update(Transaction transaction) {

    }

    @Override
    public void delete(Transaction transaction) {

    }
}
