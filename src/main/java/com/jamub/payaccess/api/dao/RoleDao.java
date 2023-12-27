package com.jamub.payaccess.api.dao;

import com.jamub.payaccess.api.dao.util.MerchantRowMapper;
import com.jamub.payaccess.api.enums.ApplicationAction;
import com.jamub.payaccess.api.models.Bank;
import com.jamub.payaccess.api.models.UserRolePermission;
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
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;


@Repository
public class RoleDao implements Dao<Bank>{
    JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall createNewUserRolePermission;
    private SimpleJdbcCall getUserRolePermissionList;
    private SimpleJdbcCall getUserRolePermissionByRoleAndPermission;
    private SimpleJdbcCall getPermissionsByRole;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public void setDataSource(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);

        createNewUserRolePermission = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("CreateNewUserRolePermission")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(UserRolePermission.class));

        getUserRolePermissionList = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetUserRolePermissionList")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(UserRolePermission.class));

        getUserRolePermissionByRoleAndPermission = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetUserRolePermissionByRoleAndPermission")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(UserRolePermission.class));

        getPermissionsByRole = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GetUserRolePermissionByRole")
                .returningResultSet("#result-set-1",
                        MerchantRowMapper.newInstance(UserRolePermission.class));
    }


    @Override
    public Optional<Bank> get(Long id) {
        return Optional.empty();
    }

    @Override
    public Map getAll() {
        Map returnList = new HashMap();
        returnList.put("list", new ArrayList<Bank>());
        returnList.put("totalCount", 100);
        return returnList;
    }

    @Override
    public Bank update(Bank Bank) {
        return null;
    }

    @Override
    public void delete(Bank Bank) {

    }

    public UserRolePermission createUserRolePermission(String userRole, String permission, Long actorId, String ipAddress, String description,
                                                       ApplicationAction userAction, String carriedOutByUserFullName, String objectClassReference) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("userRole", userRole)
                .addValue("userPermission", permission)
                .addValue("carriedOutByUserFullName", carriedOutByUserFullName)
                .addValue("userAction", userAction.name())
                .addValue("description", description)
                .addValue("ipAddress", ipAddress)
                .addValue("objectClassReference", objectClassReference)
                .addValue("carriedOutByUserId", actorId);


        Map<String, Object> m = createNewUserRolePermission.execute(in);
        logger.info("{}", m);
        List<UserRolePermission> result = (List<UserRolePermission>) m.get("#result-set-1");
        return result.get(0);
    }

    public List<UserRolePermission> getUserRolePermissionList(Integer pageNumber, Integer rowCount) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("pageNumber", pageNumber)
                .addValue("pageSize", rowCount);


        Map<String, Object> m = getUserRolePermissionList.execute(in);
        logger.info("{}", m);
        List<UserRolePermission> result = (List<UserRolePermission>) m.get("#result-set-1");

        return result;
    }

    public UserRolePermission getUserRolePermissionByRoleAndPermission(String userRole, String p) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("userRole", userRole)
                .addValue("permission", p);


        Map<String, Object> m = getUserRolePermissionByRoleAndPermission.execute(in);
        logger.info("{}", m);
        List<UserRolePermission> result = (List<UserRolePermission>) m.get("#result-set-1");

        return result!=null && !result.isEmpty() ? result.get(0) : null;
    }


    public List<UserRolePermission> getPermissionsByRole(Integer pageNumber, Integer rowCount, String userRole) {
        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("userRole", userRole)
                .addValue("pageNumber", pageNumber)
                .addValue("rowCount", rowCount);
        Map<String, Object> m = getPermissionsByRole.execute(in);
        logger.info("{}", m);
        List<UserRolePermission> result = (List<UserRolePermission>) m.get("#result-set-1");
        return result;
    }
}
