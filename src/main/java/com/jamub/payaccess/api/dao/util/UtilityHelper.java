package com.jamub.payaccess.api.dao.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
//import com.itextpdf.text.BaseColor;
//import com.itextpdf.text.Phrase;
//import com.itextpdf.text.pdf.PdfPCell;
//import com.itextpdf.text.pdf.PdfPTable;
import com.jamub.payaccess.api.enums.BusinessCategory;
import com.jamub.payaccess.api.enums.MerchantReviewStatus;
import com.jamub.payaccess.api.enums.MerchantStage;
import com.jamub.payaccess.api.enums.QRDataType;
import com.jamub.payaccess.api.models.Merchant;
import com.jamub.payaccess.api.models.MerchantApproval;
import com.jamub.payaccess.api.models.request.MerchantReviewUpdateStatusRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.security.*;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UtilityHelper {

    private final static Logger logger = LoggerFactory.getLogger(UtilityHelper.class);
    private static final List<String> contentTypes = Arrays.asList("image/png", "image/jpeg", "image/gif");

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


    public static boolean checkIfImage(MultipartFile file)
    {
        String fileContentType = file.getContentType();
        if(contentTypes.contains(fileContentType)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkValidEnumValue(String enumName, Class class_) {
        Object[] objectArray = class_.getEnumConstants();
        String[] stringArray = Arrays.copyOf(objectArray, objectArray.length, String[].class);
        List arrayList = Arrays.asList(stringArray);

        if(!arrayList.contains(enumName))
        {
            return false;
        }

        return true;
    }

    public static ArrayList<String> getCountryList() {
        String[] isoCountries = Locale.getISOCountries();
        ArrayList<String> countryList = new ArrayList<String>();
        for(String isoCountry : isoCountries)
        {
            Locale obj = new Locale("", isoCountry);
            countryList.add(obj.getDisplayCountry().toUpperCase());
        }

        countryList.sort(String::compareToIgnoreCase);

        return countryList;
    }

    public static String generateInvoiceQRCode(String referenceNumber, String merchantName, String merchantCode, BigDecimal amount, String url,
                                             String qrFileLocation) throws NoSuchAlgorithmException, WriterException, IOException {
        String qrTrackingNumber = referenceNumber;
        Map qrData = new HashMap();
        qrData.put("qrTrackingNumber", qrTrackingNumber);
        qrData.put("qrType", QRDataType.INVOICE.name());
        qrData.put("qrMerchantName", merchantName);
        qrData.put("qrMerchantCode", merchantCode);
        qrData.put("qrAmount", amount);
        qrData.put("qrUrl", url);
        String qrDataString = qrData.toString();

        String imageString = qrDataString;

        // create a buffered image
        byte[] data = DatatypeConverter.parseBase64Binary(imageString);
        String fileName = qrTrackingNumber + ".png";
        String path = qrFileLocation + File.separator + fileName;
        logger.info("Path......");
        logger.info(path);

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        String encData = UtilityHelper.get_SHA_512_SecurePassword(qrDataString, salt);

        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(encData, BarcodeFormat.QR_CODE, 200, 200);

        BufferedImage bi = MatrixToImageWriter.toBufferedImage(bitMatrix);
        File outputfile = new File(path);

        ImageIO.write(bi, "PNG", outputfile);

//        Set<PosixFilePermission> crunchifyPermissions = new HashSet<PosixFilePermission>();
//        crunchifyPermissions.add(PosixFilePermission.OWNER_READ);
//        crunchifyPermissions.add(PosixFilePermission.OWNER_WRITE);
//        crunchifyPermissions.add(PosixFilePermission.OWNER_EXECUTE);
//        crunchifyPermissions.add(PosixFilePermission.GROUP_READ);
//        crunchifyPermissions.add(PosixFilePermission.GROUP_EXECUTE);
//        crunchifyPermissions.add(PosixFilePermission.OTHERS_READ);
//        crunchifyPermissions.add(PosixFilePermission.OTHERS_EXECUTE);

//        Files.setPosixFilePermissions(Paths.get(path), crunchifyPermissions);


        return fileName;
    }

    public static Boolean validateMerchantReview(Merchant merchantCheck, MerchantReviewUpdateStatusRequest merchantReviewUpdateStatusRequest) {
        Boolean check = false;
        switch(merchantCheck.getBusinessType())
        {
            case NGO_BUSINESS:
                if(merchantCheck.getKycSet().equals(Boolean.TRUE) && merchantReviewUpdateStatusRequest.getMerchantStage().equals(MerchantStage.MERCHANT_KYC.name()))
                    check= true;
                else if(merchantCheck.getBusinessInfoSet().equals(Boolean.TRUE) && merchantReviewUpdateStatusRequest.getMerchantStage().equals(MerchantStage.MERCHANT_BUSINESS_DATA.name()))
                    check= true;
                else if(merchantCheck.getPersonalInfoSet().equals(Boolean.TRUE) && merchantReviewUpdateStatusRequest.getMerchantStage().equals(MerchantStage.MERCHANT_BIO_DATA.name()))
                    check= true;
                else if(merchantCheck.getAccountInfoSet().equals(Boolean.TRUE) && merchantReviewUpdateStatusRequest.getMerchantStage().equals(MerchantStage.MERCHANT_BUSINESS_ACCOUNT_DATA.name()))
                    check= true;
                break;
            case INDIVIDUAL:
                if(merchantCheck.getPersonalInfoSet().equals(Boolean.TRUE) && merchantReviewUpdateStatusRequest.getMerchantStage().equals(MerchantStage.MERCHANT_BIO_DATA.name()))
                    check= true;
                else if(merchantCheck.getBusinessInfoSet().equals(Boolean.TRUE) && merchantReviewUpdateStatusRequest.getMerchantStage().equals(MerchantStage.MERCHANT_BUSINESS_DATA.name()))
                    check= true;
                else if(merchantCheck.getAccountInfoSet().equals(Boolean.TRUE) && merchantReviewUpdateStatusRequest.getMerchantStage().equals(MerchantStage.MERCHANT_BUSINESS_ACCOUNT_DATA.name()))
                    check= true;
                break;
            case REGISTERED_BUSINESS:
                if(merchantCheck.getKycSet().equals(Boolean.TRUE) && merchantReviewUpdateStatusRequest.getMerchantStage().equals(MerchantStage.MERCHANT_KYC.name()))
                    check= true;
                else if(merchantCheck.getBusinessInfoSet().equals(Boolean.TRUE) && merchantReviewUpdateStatusRequest.getMerchantStage().equals(MerchantStage.MERCHANT_BUSINESS_DATA.name()))
                    check= true;
                else if(merchantCheck.getPersonalInfoSet().equals(Boolean.TRUE) && merchantReviewUpdateStatusRequest.getMerchantStage().equals(MerchantStage.MERCHANT_BIO_DATA.name()))
                    check= true;
                else if(merchantCheck.getAccountInfoSet().equals(Boolean.TRUE) && merchantReviewUpdateStatusRequest.getMerchantStage().equals(MerchantStage.MERCHANT_BUSINESS_ACCOUNT_DATA.name()))
                    check= true;
                break;
        }

        return check;
    }

    public static Boolean checkIfMerchantValidForApproval(Merchant merchant, List<MerchantApproval> merchantApproval) {
        Boolean check = false;
        switch (merchant.getBusinessType())
        {
            case INDIVIDUAL:
                List checkList = merchantApproval.stream().map(ma -> {
                    return
                            ((ma.getMerchantStage().equals(MerchantStage.MERCHANT_BIO_DATA) && ma.getMerchantReviewStatus().equals(MerchantReviewStatus.APPROVED)) ||
                            (ma.getMerchantStage().equals(MerchantStage.MERCHANT_BUSINESS_DATA) && ma.getMerchantReviewStatus().equals(MerchantReviewStatus.APPROVED)) ||
                            (ma.getMerchantStage().equals(MerchantStage.MERCHANT_BUSINESS_ACCOUNT_DATA) && ma.getMerchantReviewStatus().equals(MerchantReviewStatus.APPROVED)));
                }).filter(t -> {
                    return t.equals(Boolean.TRUE);
                }).collect(Collectors.toList());
                logger.info("1...{}", checkList);
                check = checkList.size()==3;
                break;
            case REGISTERED_BUSINESS:
                checkList = merchantApproval.stream().map(ma -> {
                    return
                            ((ma.getMerchantStage().equals(MerchantStage.MERCHANT_KYC) && ma.getMerchantReviewStatus().equals(MerchantReviewStatus.APPROVED)) ||
                                    (ma.getMerchantStage().equals(MerchantStage.MERCHANT_BUSINESS_DATA) && ma.getMerchantReviewStatus().equals(MerchantReviewStatus.APPROVED)) ||
                                    (ma.getMerchantStage().equals(MerchantStage.MERCHANT_BIO_DATA) && ma.getMerchantReviewStatus().equals(MerchantReviewStatus.APPROVED)) ||
                                    (ma.getMerchantStage().equals(MerchantStage.MERCHANT_BUSINESS_ACCOUNT_DATA) && ma.getMerchantReviewStatus().equals(MerchantReviewStatus.APPROVED)));
                }).filter(t -> {
                    return t.equals(Boolean.TRUE);
                }).collect(Collectors.toList());
                logger.info("2...{}", checkList);
                check = checkList.size()==4;
                break;
            case NGO_BUSINESS:
                checkList = merchantApproval.stream().map(ma -> {
                    return
                            ((ma.getMerchantStage().equals(MerchantStage.MERCHANT_KYC) && ma.getMerchantReviewStatus().equals(MerchantReviewStatus.APPROVED)) ||
                                    (ma.getMerchantStage().equals(MerchantStage.MERCHANT_BUSINESS_DATA) && ma.getMerchantReviewStatus().equals(MerchantReviewStatus.APPROVED)) ||
                                    (ma.getMerchantStage().equals(MerchantStage.MERCHANT_BIO_DATA) && ma.getMerchantReviewStatus().equals(MerchantReviewStatus.APPROVED)) ||
                                    (ma.getMerchantStage().equals(MerchantStage.MERCHANT_BUSINESS_ACCOUNT_DATA) && ma.getMerchantReviewStatus().equals(MerchantReviewStatus.APPROVED)));
                }).filter(t -> {
                    return t.equals(Boolean.TRUE);
                }).collect(Collectors.toList());
                logger.info("3...{}", checkList);
                check = checkList.size()==4;
                break;
        }
        logger.info("check..{}", check);

        return check;
    }

//    public static void addTableHeader(PdfPTable table) {
//        Stream.of("S/N", "Customer", "Order Ref", "Merchant Code", "Business Name", "Terminal", "Channel", "Transaction Date", "Service", "Amount")
//                .forEach(columnTitle -> {
//                    PdfPCell header = new PdfPCell();
//                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
//                    header.setBorderWidth(2);
//                    header.setPhrase(new Phrase(columnTitle));
//                    table.addCell(header);
//                });
//    }
}
