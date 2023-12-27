package com.jamub.payaccess.api.dao.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

public class MerchantRowMapper extends BeanPropertyRowMapper {
    private ObjectMapper objectMapper;

    @Override
    protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd) throws SQLException {
        Class<?> type = pd.getPropertyType();

        Object value = null;
        if (Map.class.equals(type)) {
            String values = rs.getString(index);
            if (values != null) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> additionalInfo = objectMapper.readValue(values, Map.class);
                    value =  additionalInfo;
                } catch (IOException e) {
                    logger.warn("Could not deserialize map property from JSON: ", e);
                }
            }
        } else if (Set.class.equals(type)) {
            value = StringUtils.commaDelimitedListToSet(rs.getString(index));
        }
        if (value == null) {

            return super.getColumnValue(rs, index, pd);
        } else {
            return (rs.wasNull() ? null : value);
        }
    }

}
