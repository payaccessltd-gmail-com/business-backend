package com.jamub.payaccess.api.dao.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

public class UtilityHelper {

    private final static Logger logger = LoggerFactory.getLogger(UtilityHelper.class);
    public static String generateBCryptPassword(String password)
    {
        String generatedSecuredPasswordHash = BCrypt.hashpw(password, BCrypt.gensalt(12));
        return generatedSecuredPasswordHash;
    }


    public static String get_SHA_512_SecurePassword(String passwordToHash, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt);
        byte[] hashedPassword = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
        String s = new String(hashedPassword, StandardCharsets.UTF_8);
        return s;
    }

    public static String uploadFile(MultipartFile identificationDocumentPath, String fileDestinationPath) throws IOException {
        logger.info("identificationDocumentPath ...{}", identificationDocumentPath.getSize());
        byte[] fileBytes = identificationDocumentPath.getBytes();
        String pathName = identificationDocumentPath.getOriginalFilename();
        String newFileName = RandomStringUtils.randomAlphanumeric(16);
        String newFileNameExt = pathName.substring(pathName.lastIndexOf(".") + 1);
        File destinationFile = new File(fileDestinationPath + File.separator + newFileName + "." + newFileNameExt);
        BufferedOutputStream is = new BufferedOutputStream(new FileOutputStream(destinationFile));
        is.write(fileBytes);
        is.close();

        return newFileName + "." + newFileNameExt;
    }

    public static String getAuthData(String version, String pan, String pin, String expiryDate, String cvv2) throws Exception {
        String authData = "";
        String authDataCipher = version + "Z" + pan + "Z" + pin + "Z" + expiryDate + "Z" + cvv2;
        // The Modulus and Public Exponent will be supplied by Interswitch. please ask for one
        String modulus = "9c7b3ba621a26c4b02f48cfc07ef6ee0aed8e12b4bd11c5cc0abf80d5206be69e1891e60fc88e2d565e2fabe4d0cf630e318a6c721c3ded718d0c530cdf050387ad0a30a336899bbda877d0ec7c7c3ffe693988bfae0ffbab71b25468c7814924f022cb5fda36e0d2c30a7161fa1c6fb5fbd7d05adbef7e68d48f8b6c5f511827c4b1c5ed15b6f20555affc4d0857ef7ab2b5c18ba22bea5d3a79bd1834badb5878d8c7a4b19da20c1f62340b1f7fbf01d2f2e97c9714a9df376ac0ea58072b2b77aeb7872b54a89667519de44d0fc73540beeaec4cb778a45eebfbefe2d817a8a8319b2bc6d9fa714f5289ec7c0dbc43496d71cf2a642cb679b0fc4072fd2cf";
        String publicExponent = "010001";
        Security.addProvider(new BouncyCastleProvider());
        RSAPublicKeySpec publicKeyspec = new RSAPublicKeySpec(new BigInteger(modulus, 16), new BigInteger(publicExponent, 16));
        KeyFactory factory = KeyFactory.getInstance("RSA"); //, "JHBCI");
        PublicKey publicKey = factory.generatePublic(publicKeyspec);
        Cipher encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] authDataBytes = encryptCipher.doFinal(authDataCipher.getBytes("UTF8"));
        authData = Base64.getEncoder().encodeToString(authDataBytes).replaceAll("\\r|\\n", "");
        return authData;
    }

}
