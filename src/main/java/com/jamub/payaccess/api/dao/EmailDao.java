package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
import com.jamub.payaccess.api.enums.EmailDocumentPriorityLevel;
import com.jamub.payaccess.api.models.EmailDocument;
import com.jamub.payaccess.api.models.User;
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
import java.util.*;

@Repository
public class EmailDao implements Dao<EmailDocument>{

    JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall getEmailDocument;
    private SimpleJdbcCall getAllEmailDocuments;
    private SimpleJdbcCall getAllEmailDocumentsByPriorityLevel;
    private SimpleJdbcCall saveEmailDocument;
    private SimpleJdbcCall updateEmailDocument;
    private SimpleJdbcCall deleteEmailDocument;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    public void setDataSource(DataSource ds)
    {
        this.jdbcTemplate = new JdbcTemplate(ds);
        getEmailDocument = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetEmailDocument")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(EmailDocument.class));

        getAllEmailDocuments = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetAllEmailDocuments")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(EmailDocument.class))
                .returningResultSet("#result-set-2", new BeanPropertyRowMapper<Integer>()
                {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        return rs.getInt("count");
                    }
                });

        getAllEmailDocumentsByPriorityLevel = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetAllEmailDocumentsByPriorityLevel")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(EmailDocument.class))
                .returningResultSet("#result-set-2", new BeanPropertyRowMapper<Integer>()
                {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        return rs.getInt("count");
                    }
                });

        saveEmailDocument = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SaveEmailDocument")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(EmailDocument.class));

        updateEmailDocument = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("UpdateEmailDocument")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(EmailDocument.class));


    }

    @Override
    public Optional<EmailDocument> get(Long id) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailDocumentId", id);
        Map<String, Object> m = getEmailDocument.execute(in);

        if(m.isEmpty()) {
            return Optional.empty();
        }
        else
        {
            List<EmailDocument> result = (List<EmailDocument>) m.get("#result-set-1");
            return Optional.of(result.get(0));
        }
    }


    public Optional<EmailDocument> get(Long id, Long merchantId) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("emailDocumentId", id);
        Map<String, Object> m = getEmailDocument.execute(in);

        if(m.isEmpty()) {
            return Optional.empty();
        }
        else
        {
            List<EmailDocument> result = (List<EmailDocument>) m.get("#result-set-1");
            if(result.isEmpty())
                return Optional.empty();
            return Optional.of(result.get(0));
        }
    }

    @Override
    public Map getAll() {
        MapSqlParameterSource in = new MapSqlParameterSource();
        Map<String, Object> m = getAllEmailDocuments.execute(in);

        List<EmailDocument> result = (List<EmailDocument>) m.get("#result-set-1");
        List<Integer> totalCountResult = (List<Integer>) m.get("#result-set-2");

        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", totalCountResult.get(0));

        return returnList;
    }



    @Override
    public EmailDocument update(EmailDocument emailDocument) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("htmlMessage", emailDocument.getHtmlMessage())
                .addValue("recipients", emailDocument.getRecipients())
                .addValue("attachmentList", emailDocument.getAttachmentList())
                .addValue("subject", emailDocument.getSubject())
                .addValue("emailDocumentId", emailDocument.getId());
        Map<String, Object> m = updateEmailDocument.execute(in);
        List<EmailDocument> result = (List<EmailDocument>) m.get("#result-set-1");
        EmailDocument merchant = result!=null && !result.isEmpty() ? result.get(0) : null;
        return merchant;
    }

    @Override
    public void delete(EmailDocument emailDocument) {

    }


    public EmailDocument saveEmailDocument(String htmlMessage, String recipients, String attachmentList, String subject, User user,
                                           EmailDocumentPriorityLevel emailDocumentPriorityLevel) {

        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("htmlMessage", htmlMessage, Types.LONGVARCHAR)
                .addValue("recipients", recipients)
                .addValue("attachmentList", attachmentList)
                .addValue("createdByUserId", user.getId())
                .addValue("subject", subject)
                .addValue("emailDocumentPriorityLevel", emailDocumentPriorityLevel);
        Map<String, Object> m = saveEmailDocument.execute(in);
        List<EmailDocument> result = (List<EmailDocument>) m.get("#result-set-1");
        EmailDocument emailDocument = result!=null && !result.isEmpty() ? result.get(0) : null;
        return emailDocument;
    }

    public Map getAll(int pageNumber, int maxSize, Long merchantId) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber", pageNumber*maxSize, Types.INTEGER)
                .addValue("pageSize", maxSize, Types.INTEGER);
        Map<String, Object> m = getAllEmailDocuments.execute(in);

        List<EmailDocument> result = (List<EmailDocument>) m.get("#result-set-1");
        List<Integer> totalCount = (ArrayList<Integer>)m.get("#result-set-2");
        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", totalCount.get(0));
        return returnList;
    }

    public Map getAllByPriorityLevel(int pageNumber, int maxSize, Long merchantId, EmailDocumentPriorityLevel emailDocumentPriorityLevel) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber", pageNumber*maxSize, Types.INTEGER)
                .addValue("pageSize", maxSize, Types.INTEGER)
                .addValue("emailDocumentPriorityLevel", emailDocumentPriorityLevel);
        Map<String, Object> m = getAllEmailDocumentsByPriorityLevel.execute(in);

        List<EmailDocument> result = (List<EmailDocument>) m.get("#result-set-1");
        List<Integer> totalCount = (ArrayList<Integer>)m.get("#result-set-2");
        Map returnList = new HashMap();
        returnList.put("list", result);
        returnList.put("totalCount", totalCount.get(0));
        return returnList;
    }


}
