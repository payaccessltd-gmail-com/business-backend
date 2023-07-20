package com.jamub.payaccess.api.dao.util;

import org.mindrot.jbcrypt.BCrypt;

public class UtilityHelper {
    public static String generateBCryptPassword(String password)
    {
        String generatedSecuredPasswordHash = BCrypt.hashpw(password, BCrypt.gensalt(12));
        return generatedSecuredPasswordHash;
    }
}
