package com.jamub.payaccess.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.jamub.payaccess.api.dao.TransactionDao;
import com.jamub.payaccess.api.dao.UserDao;
import com.jamub.payaccess.api.dto.MerchantDTO;
import com.jamub.payaccess.api.dto.UserDTO;
import com.jamub.payaccess.api.enums.Gender;
import com.jamub.payaccess.api.enums.PayAccessStatusCode;
import com.jamub.payaccess.api.enums.UserStatus;
import com.jamub.payaccess.api.models.Customer;
import com.jamub.payaccess.api.models.Merchant;
import com.jamub.payaccess.api.models.Transaction;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.request.CustomerSignUpRequest;
import com.jamub.payaccess.api.models.request.UpdateBioDataRequest;
import com.jamub.payaccess.api.models.request.UserSignUpRequest;
import com.jamub.payaccess.api.models.response.PayAccessResponse;
import com.sun.mail.smtp.SMTPTransport;
import org.apache.commons.lang3.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.DisabledException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {

    private UserDao userDao;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserService(UserDao userDao){
        this.userDao = userDao;
//        this.authenticationManager = authenticationManager;
    }


    public User getUserById(Long userId) {

        Optional<User> userOptional = userDao.get(userId);

        if(userOptional.isPresent())
            return userOptional.get();

        return null;
    }


    public User getUserByEmailAddress(String emailAddress) {

        List<User> userList = userDao.getUserByEmailAddress(emailAddress);

        if(userList.isEmpty())
            return null;

        return userList.get(0);
    }








    public PayAccessResponse createNewUser(UserSignUpRequest userSignUpRequest, Long otpExpiryPeriod, String localhostDomainEndpoint, int serverPort, String localhostDomainEndpointPath) {
        List<User> existingUser = userDao.getUserByEmailAddress(userSignUpRequest.getEmailAddress());
        if(existingUser!=null && !existingUser.isEmpty())
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Sign up was not successful. Email address is already signed up");
            return payAccessResponse;
        }
        String otp = RandomStringUtils.randomNumeric(6);
        String verificationLink = RandomStringUtils.randomAlphanumeric(128);
        LocalDateTime otpExpiryDate = LocalDateTime.now().plusSeconds(otpExpiryPeriod);
        String secretKey = "sk_test_"+RandomStringUtils.randomAlphanumeric(40).toLowerCase();
        String publicKey = "pk_test_"+RandomStringUtils.randomAlphanumeric(40).toLowerCase();
        String secretKeyLive = "sk_live_"+RandomStringUtils.randomAlphanumeric(40).toLowerCase();
        String publicKeyLive = "pk_live_"+RandomStringUtils.randomAlphanumeric(40).toLowerCase();
        String merchantCode = RandomStringUtils.randomAlphanumeric(8).toUpperCase();
        User user = userDao.save(userSignUpRequest, otp, verificationLink, otpExpiryDate, secretKey, publicKey, secretKeyLive, publicKeyLive, merchantCode);
        if(user!=null)
        {
            String verifyUrl = "http://"+localhostDomainEndpoint+":"+serverPort+"/"+localhostDomainEndpointPath+"/api/v1/user/activate-account/"+ user.getEmailAddress()+"/" + verificationLink;
            String htmlMessage = "<div style='background:#f5f5f5;background-color:#f5f5f5;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#f5f5f5;background-color:#f5f5f5;width:100%;'>	<tbody><tr>	<td><div style='Margin:0px auto;max-width:780px;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='width:100%;max-width:780px;background:#F5F5F5;background-color:#F5F5F5;'><tbody>	<tr><td style='direction:ltr;font-size:0px;padding-bottom:0px;text-align:center;vertical-align:top;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:transparent;background-color:transparent;width:100%;'><tbody>	<tr><td>	<div style='Margin:0px auto;max-width:620px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:30px;padding-bottom:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'>  </table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table><table align='center' style='width:100%;max-width:780px;background:#F5F5F5;background-color:#F5F5F5;' '=''>	<tbody><tr style='width:{headerImageWidth}px;'>	<td align='center' style='font-size:0px;padding:0px;word-break:break-word;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='border-collapse:collapse;border-spacing:0px;'>	<tbody><tr>	<td style='width:780px;padding:0pm 0px 0px 0px;padding-bottom:0px;'><img alt='Vend' height='auto' src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_header.png' style='border:0;display:block;outline:none;text-decoration:none;height:auto;width:100%;' width='{headerImageWidth}'> 	</td></tr>	</tbody></table>	</td></tr>	</tbody></table><div class='main-content' style='background:#fff;background-color:#fff;Margin:0px auto;max-width:780px;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#fff;background-color:#fff;width:100%'><tbody>	<tr><td colspan='3' style='height:30px'></td>	</tr>	<tr><td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;' class='mktoContainer' id='container'>	<table class='mktoModule' id='textSection' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable'><h1 style='text-align: center;'>Welcome to PayAccess! Let's get&nbsp;started.</h1><p style='padding-bottom: 20px;'>&nbsp;<br>Hello "+user.getFirstName()+"! I'll be showing you how to get started with PayAccess and grow your retail business. First, activate your account by clicking on the link - <a href='"+verifyUrl+"'>Activate Account</a></p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='textSection4fc63ee1-701e-488b-a2a4-b1d5cdffd971' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable4fc63ee1-701e-488b-a2a4-b1d5cdffd971'><table width='100%' style='background-color: #e9f6e8;'>	<tbody><tr>	<td width='10%'>&nbsp;</td>	<td width='80%' align='center' style='text-align: center;'><h2 style='font-family: Helvetica, Arial, sans-serif; color: #626262; padding-top: 20px; padding-bottom: 10px;'>Your PayAccess Login Details</h2><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262; padding-bottom: 10px;'>Your Username/Email is: "+ user.getEmailAddress() +"<br>Enter the OTP: "+ otp +"</p><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='border-collapse: separate; line-height: 100%;'>	<tbody><tr>	<td align='center' bgcolor='#41af4b' role='presentation' style='border: 2px solid transparent; border-radius: 0px; cursor: auto; padding: 14px 24px;' valign='middle'><a href='" + verifyUrl +"' style='background: #41af4b; color: #ffffff!important; font-family: Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 600; line-height: 14px; margin: 0; text-decoration: none; text-transform: uppercase; letter-spacing: 0.06rem;' target='_blank'>ACTIVATE YOUR ACCOUNT</a></td></tr>	</tbody></table><p>&nbsp;</p>	</td>	<td width='10%'>&nbsp;</td></tr>	</tbody></table><p>&nbsp;</p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='textSection4fc63ee1-701e-488b-a2a4-b1d5cdffd9718b5d4c4a-486c-40c3-9236-43cdf187ad89' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable4fc63ee1-701e-488b-a2a4-b1d5cdffd9718b5d4c4a-486c-40c3-9236-43cdf187ad89'><table width='100%' style='background-color: #f8f8f5;'>	<tbody><tr>	<td width='15%'>&nbsp;</td>	<td width='70%' align='center' style='text-align: center;'><h2 style='font-family: Helvetica, Arial, sans-serif; color: #626262; padding-top: 20px; padding-bottom: 10px;'>Set up PayAccess</h2><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>Get started with PayAccess by reading these four essential guides from our Help Centre and you'll be selling in no time!</p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_01.png' alt='V2439-Adoption-Onboarding-nurture-email-1_01.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Set up your outlets and registers</h3><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>Learn how to get PayAccess running on all of your registers and outlets.<br><a href='#'>Learn about registers</a></p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_02.png' alt='V2439-Adoption-Onboarding-nurture-email-1_02.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Organise your sales taxes</h3><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>Add any sales or value-added taxes (VAT) that are for your location&nbsp;or&nbsp;products.<br><a href='#'>Learn about taxes</a></p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_03.png' alt='V2439-Adoption-Onboarding-nurture-email-1_03.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Create different payment types</h3><p>Set up your payment terminal and registers so you can accept cash, cards and other&nbsp;payment&nbsp;types.<br><a href='#'>Learn about integrated payments</a></p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_04.png' alt='V2439-Adoption-Onboarding-nurture-email-1_04.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Add a product</h3><p>Start adding your products, stock levels and descriptions to&nbsp;your&nbsp;catalog.<br><a href='#'>Learn how to add products</a></p><p>&nbsp;</p>	</td>	<td width='15%'>&nbsp;</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='textSection2b16ab9a-73ae-43cf-8972-6db3159390c2' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable2b16ab9a-73ae-43cf-8972-6db3159390c2'><p>&nbsp;</p><h2>Your PayAccess to-do list</h2><p>Like to read ahead? Our <a href='#'>setup checklist</a> gives you a list of steps that you can check off at your own pace to get PayAccess set up.</p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='signoffWithoutProfilePhoto15f0ef74-c2b9-420f-a62b-2e96f521ed08' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;padding-top:30px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;'><p>Here's to your retail success, <br><br> <strong>Peters</strong> <br>Director of Adoption <br> </p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></td>	</tr>	<tr><td colspan='3' style='height:50px'></td>	</tr></tbody>	</table></div><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#f5f5f5;background-color:#f5f5f5;width:100%;'>	<tbody><tr>	<td><div style='Margin:0px auto;max-width:680px;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='width:100%;'><tbody>	<tr><td style='direction:ltr;font-size:0px;padding:5px;text-align:center;vertical-align:top;'>	<div style='background:#f5f5f5;background-color:#f5f5f5;Margin:0px auto;max-width:650px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#f5f5f5;background-color:#f5f5f5;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:15px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='center' style='font-size:0px;padding:0px;word-break:break-word;'>	<div class='mktoSnippet' id='unsubscribeFooter'><div style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>	PayAccess HQ, 2-36 Obalende Street, Abuja, Nigeria 	<br> 	<a style='color: #008cc5 !important; text-decoration: none !important;' href='#' target='_blank'>Unsubscribe</a> ∙ 	<a style='color: #008cc5 !important; text-decoration: none !important;' href='https://email.vendhq.com/Nzc2LVFGTy0zMzQAAAF_hWiDyzkDXU0MPpA_mZQkOV6uelqxQNlKl80Dp7nbfZsoBZZomppxXFRKN_z6O69Y_RlWN_c=' target='_blank'>Privacy Policy</a></div>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table></div>";

            try {
                logger.info("=========================");
                Properties props = System.getProperties();
                props.put("mail.smtps.host", "smtp.mailgun.org");
                props.put("mail.smtps.auth", "true");

                Session session = Session.getInstance(props, null);
                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress("emailer@payaccess.com", "PayAccess"));

                InternetAddress[] addrs = InternetAddress.parse(userSignUpRequest.getEmailAddress(), false);
                msg.setRecipients(Message.RecipientType.TO, addrs);

                msg.setSubject("Welcome to PayAccess");
                msg.setContent(htmlMessage, "text/html; charset=utf-8");

                //msg.setText("Copy the url and paste in your browser to activate your account - http://137.184.47.182:8081/payaccess/api/v1/user/activate-account/"+user.getEmailAddress()+"/" + verificationLink +" - providing the OTP: " + otp);

                msg.setSentDate(new Date());

                SMTPTransport t =
                        (SMTPTransport) session.getTransport("smtps");
                t.connect("smtp.mailgun.org", "postmaster@mails.valuenaira.com", "k0l01qaz!QAZ");
                t.sendMessage(msg, msg.getAllRecipients());

                logger.info("Response: {}" , t.getLastServerResponse());

                t.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                logger.error("Error Sending Mail ...{}", e);
            }

            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setResponseObject(verifyUrl);
            payAccessResponse.setMessage("An activation link has been sent to your email - '"+userSignUpRequest.getEmailAddress().toLowerCase()+"'. Please check your email to complete your registration.");
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Customer sign up was not successful. Please try again");
        return payAccessResponse;
    }



    public PayAccessResponse updateBiodata(UpdateBioDataRequest updateBioDataRequest, Long otpExpiryPeriod, User authenticatedUser) {

        Optional<User> optionalUser = userDao.get(authenticatedUser.getId());
        if(optionalUser.isPresent())
        {
            User userInDB = optionalUser.get();
            if(userInDB.getEmailAddress().equalsIgnoreCase(updateBioDataRequest.getEmailAddress()))
            {
                //Same User so no check against existing email
                userInDB.setFirstName(updateBioDataRequest.getFirstName());
                userInDB.setLastName(updateBioDataRequest.getLastName());
                userInDB.setGender(Gender.valueOf(updateBioDataRequest.getGender()));
                userInDB.setEmailAddress(updateBioDataRequest.getEmailAddress());
                userInDB.setMobileNumber(updateBioDataRequest.getPhoneNumer());
                userDao.update(userInDB);
            }
            else{
                List<User> existingUser = userDao.getUserByEmailAddress(updateBioDataRequest.getEmailAddress());
                if(existingUser!=null && !existingUser.isEmpty())
                {
                    PayAccessResponse payAccessResponse = new PayAccessResponse();
                    payAccessResponse.setStatusCode(PayAccessStatusCode.EMAIL_EXISTS.label);
                    payAccessResponse.setMessage("Email provided has already been provided by another person");
                    return payAccessResponse;
                }
            }
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Invalid action. Users profile could not be fetched to update profile. Please try again");
        return payAccessResponse;
    }



    public PayAccessResponse activateAccount(String emailAddress, String verificationLink, String otp) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        Map<String, Object> m = userDao.activateAccount(emailAddress, verificationLink, otp);
        logger.info("{}, {}, {}", emailAddress, verificationLink, otp);

        List<User> userList = (List<User>) m.get("#result-set-1");

        User user = userList.get(0);

        if(user==null)
        {
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("Your profile activation was not successful. Please try again");
            return payAccessResponse;
        }


        payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
        payAccessResponse.setMessage("Your profile has been activated successfully");
        String userToString = objectMapper.writeValueAsString(user);
        UserDTO userDto = objectMapper.readValue(userToString, UserDTO.class);
        payAccessResponse.setResponseObject(userDto);
        return payAccessResponse;
    }

    public PayAccessResponse getUsersList(Integer pageNumber, int pageSize) {
        if(pageNumber==null)
            pageNumber = 0;

        List<User> queryResponse = userDao.getUsers(pageNumber, pageSize);
        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setResponseObject(queryResponse);
        if(queryResponse!=null)
        {
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("Users fetched successfully");
            return payAccessResponse;
        }
        payAccessResponse.setStatusCode(PayAccessStatusCode.FAIL.label);
        payAccessResponse.setMessage("Merchant fetch failed");
        return payAccessResponse;
    }

    public PayAccessResponse generateUserOtp(long otpExpiryPeriod, User authenticatedUser) {
        Optional<User> optionalUser = userDao.get(authenticatedUser.getId());
        if(optionalUser.isPresent())
        {
            User userInDB = optionalUser.get();
            String otp = RandomStringUtils.randomNumeric(6);
            userInDB.setOtp(otp);
            userInDB.setOtpExpiryDate(LocalDateTime.now().plusSeconds(otpExpiryPeriod));
            userDao.update(userInDB);


            String htmlMessage = "<div style='background:#f5f5f5;background-color:#f5f5f5;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#f5f5f5;background-color:#f5f5f5;width:100%;'>	<tbody><tr>	<td><div style='Margin:0px auto;max-width:780px;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='width:100%;max-width:780px;background:#F5F5F5;background-color:#F5F5F5;'><tbody>	<tr><td style='direction:ltr;font-size:0px;padding-bottom:0px;text-align:center;vertical-align:top;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:transparent;background-color:transparent;width:100%;'><tbody>	<tr><td>	<div style='Margin:0px auto;max-width:620px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:30px;padding-bottom:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'>  </table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table><table align='center' style='width:100%;max-width:780px;background:#F5F5F5;background-color:#F5F5F5;' '=''>	<tbody><tr style='width:{headerImageWidth}px;'>	<td align='center' style='font-size:0px;padding:0px;word-break:break-word;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='border-collapse:collapse;border-spacing:0px;'>	<tbody><tr>	<td style='width:780px;padding:0pm 0px 0px 0px;padding-bottom:0px;'><img alt='Vend' height='auto' src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_header.png' style='border:0;display:block;outline:none;text-decoration:none;height:auto;width:100%;' width='{headerImageWidth}'> 	</td></tr>	</tbody></table>	</td></tr>	</tbody></table><div class='main-content' style='background:#fff;background-color:#fff;Margin:0px auto;max-width:780px;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#fff;background-color:#fff;width:100%'><tbody>	<tr><td colspan='3' style='height:30px'></td>	</tr>	<tr><td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;' class='mktoContainer' id='container'>	<table class='mktoModule' id='textSection' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable'><h1 style='text-align: center;'>Welcome to PayAccess! Let's get&nbsp;started.</h1><p style='padding-bottom: 20px;'>&nbsp;" +
                    "<br>Hello "+userInDB.getFirstName()+"!</p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='textSection4fc63ee1-701e-488b-a2a4-b1d5cdffd971' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable4fc63ee1-701e-488b-a2a4-b1d5cdffd971'><table width='100%' style='background-color: #e9f6e8;'>	<tbody><tr>	<td width='10%'>&nbsp;</td>	<td width='80%' align='center' style='text-align: center;'><h2 style='font-family: Helvetica, Arial, sans-serif; color: #626262; padding-top: 20px; padding-bottom: 10px;'>Your PayAccess Login Details</h2><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262; padding-bottom: 10px;'>Enter the OTP: "+ otp +"</p><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='border-collapse: separate; line-height: 100%;'>	<tbody><tr>	<td align='center' bgcolor='#41af4b' role='presentation' style='border: 2px solid transparent; border-radius: 0px; cursor: auto; padding: 14px 24px;' valign='middle'></td></tr>	</tbody></table><p>&nbsp;</p>	</td>	<td width='10%'>&nbsp;</td></tr>	</tbody></table><p>&nbsp;</p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='textSection4fc63ee1-701e-488b-a2a4-b1d5cdffd9718b5d4c4a-486c-40c3-9236-43cdf187ad89' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable4fc63ee1-701e-488b-a2a4-b1d5cdffd9718b5d4c4a-486c-40c3-9236-43cdf187ad89'><table width='100%' style='background-color: #f8f8f5;'>	<tbody><tr>	<td width='15%'>&nbsp;</td>	<td width='70%' align='center' style='text-align: center;'><h2 style='font-family: Helvetica, Arial, sans-serif; color: #626262; padding-top: 20px; padding-bottom: 10px;'>Set up PayAccess</h2><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>Get started with PayAccess by reading these four essential guides from our Help Centre and you'll be selling in no time!</p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_01.png' alt='V2439-Adoption-Onboarding-nurture-email-1_01.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Set up your outlets and registers</h3><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>Learn how to get PayAccess running on all of your registers and outlets.<br><a href='#'>Learn about registers</a></p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_02.png' alt='V2439-Adoption-Onboarding-nurture-email-1_02.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Organise your sales taxes</h3><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>Add any sales or value-added taxes (VAT) that are for your location&nbsp;or&nbsp;products.<br><a href='#'>Learn about taxes</a></p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_03.png' alt='V2439-Adoption-Onboarding-nurture-email-1_03.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Create different payment types</h3><p>Set up your payment terminal and registers so you can accept cash, cards and other&nbsp;payment&nbsp;types.<br><a href='#'>Learn about integrated payments</a></p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_04.png' alt='V2439-Adoption-Onboarding-nurture-email-1_04.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Add a product</h3><p>Start adding your products, stock levels and descriptions to&nbsp;your&nbsp;catalog.<br><a href='#'>Learn how to add products</a></p><p>&nbsp;</p>	</td>	<td width='15%'>&nbsp;</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='textSection2b16ab9a-73ae-43cf-8972-6db3159390c2' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable2b16ab9a-73ae-43cf-8972-6db3159390c2'><p>&nbsp;</p><h2>Your PayAccess to-do list</h2><p>Like to read ahead? Our <a href='#'>setup checklist</a> gives you a list of steps that you can check off at your own pace to get PayAccess set up.</p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='signoffWithoutProfilePhoto15f0ef74-c2b9-420f-a62b-2e96f521ed08' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;padding-top:30px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;'><p>Here's to your retail success, <br><br> <strong>Peters</strong> <br>Director of Adoption <br> </p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></td>	</tr>	<tr><td colspan='3' style='height:50px'></td>	</tr></tbody>	</table></div><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#f5f5f5;background-color:#f5f5f5;width:100%;'>	<tbody><tr>	<td><div style='Margin:0px auto;max-width:680px;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='width:100%;'><tbody>	<tr><td style='direction:ltr;font-size:0px;padding:5px;text-align:center;vertical-align:top;'>	<div style='background:#f5f5f5;background-color:#f5f5f5;Margin:0px auto;max-width:650px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#f5f5f5;background-color:#f5f5f5;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:15px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='center' style='font-size:0px;padding:0px;word-break:break-word;'>	<div class='mktoSnippet' id='unsubscribeFooter'><div style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>	PayAccess HQ, 2-36 Obalende Street, Abuja, Nigeria 	<br> 	<a style='color: #008cc5 !important; text-decoration: none !important;' href='#' target='_blank'>Unsubscribe</a> ∙ 	<a style='color: #008cc5 !important; text-decoration: none !important;' href='https://email.vendhq.com/Nzc2LVFGTy0zMzQAAAF_hWiDyzkDXU0MPpA_mZQkOV6uelqxQNlKl80Dp7nbfZsoBZZomppxXFRKN_z6O69Y_RlWN_c=' target='_blank'>Privacy Policy</a></div>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table></div>";

            try {
                logger.info("=========================");
                Properties props = System.getProperties();
                props.put("mail.smtps.host", "smtp.mailgun.org");
                props.put("mail.smtps.auth", "true");

                Session session = Session.getInstance(props, null);
                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress("emailer@payaccess.com", "PayAccess"));

                InternetAddress[] addrs = InternetAddress.parse(userInDB.getEmailAddress(), false);
                msg.setRecipients(Message.RecipientType.TO, addrs);

                msg.setSubject("Your One-Time Password");
                msg.setContent(htmlMessage, "text/html; charset=utf-8");

                //msg.setText("Copy the url and paste in your browser to activate your account - http://137.184.47.182:8081/payaccess/api/v1/user/activate-account/"+user.getEmailAddress()+"/" + verificationLink +" - providing the OTP: " + otp);

                msg.setSentDate(new Date());

                SMTPTransport t =
                        (SMTPTransport) session.getTransport("smtps");
                t.connect("smtp.mailgun.org", "postmaster@mails.valuenaira.com", "k0l01qaz!QAZ");
                t.sendMessage(msg, msg.getAllRecipients());

                logger.info("Response: {}" , t.getLastServerResponse());

                t.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                logger.error("Error Sending Mail ...{}", e);
            }

            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("A code has been sent to your email account. Please enter the code to continue");
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Invalid action. Users profile could not be fetched to generate a one-time password. Please try again");
        return payAccessResponse;
    }

    public PayAccessResponse validateUserOtp(String otp, User authenticatedUser) {
        Optional<User> optionalUser = userDao.get(authenticatedUser.getId());
        if(optionalUser.isPresent()) {
            User userInDB = optionalUser.get();

            LocalDateTime now = LocalDateTime.now();
            if(now.isAfter(userInDB.getOtpExpiryDate()))
            {
                PayAccessResponse payAccessResponse = new PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.OTP_EXPIRED.label);
                payAccessResponse.setMessage("One-time password generated as expired");
                return payAccessResponse;
            }

            if(!otp.equals(userInDB.getOtp()))
            {
                PayAccessResponse payAccessResponse = new PayAccessResponse();
                payAccessResponse.setStatusCode(PayAccessStatusCode.OTP_MISMATCH.label);
                payAccessResponse.setMessage("One-time password does not match. Please provide the valid OTP");
                return payAccessResponse;
            }
            userInDB.setOtp(null);
            userInDB.setOtpExpiryDate(null);
            userDao.update(userInDB);

            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("One-time password validation successful");
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("Invalid OTP provided.");
        return payAccessResponse;


    }

//    private void authenticate(String username, String password) throws Exception {
//        try {
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//        } catch (DisabledException e) {
//            throw new Exception("USER_DISABLED", e);
//        } catch (BadCredentialsException e) {
//            throw new Exception("INVALID_CREDENTIALS", e);
//        }
//    }


    public PayAccessResponse updateUserForgotPasswordLink(String emailAddress, String forgotPasswordEndpoint, String forgotPasswordLink)
    {
        User user = userDao.updateUserForgotPasswordLink(emailAddress, forgotPasswordLink);
        if(user!=null)
        {

            try {
                logger.info("=========================");
                Properties props = System.getProperties();
                props.put("mail.smtps.host", "smtp.mailgun.org");
                props.put("mail.smtps.auth", "true");

                Session session = Session.getInstance(props, null);
                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress("emailer@payaccess.com", "PayAccess"));


                InternetAddress[] addrs = InternetAddress.parse(emailAddress, false);
                msg.setRecipients(Message.RecipientType.TO, addrs);

                msg.setSubject("Hello");
                msg.setText("Copy the url and paste in your browser to activate your account - "+forgotPasswordEndpoint+"/payaccess/api/v1/auth/update-forgot-password/"+emailAddress+"/"+forgotPasswordLink);

                msg.setSentDate(new Date());

                SMTPTransport t =
                        (SMTPTransport) session.getTransport("smtps");
                t.connect("smtp.mailgun.org", "postmaster@mails.valuenaira.com", "k0l01qaz!QAZ");
                t.sendMessage(msg, msg.getAllRecipients());

                logger.info("Response: {}" , t.getLastServerResponse());

                t.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                logger.error("Error Sending Mail ...{}", e);
            }

            PayAccessResponse tokenResponse = new PayAccessResponse();
            tokenResponse.setStatusCode("00");
            tokenResponse.setMessage("An email containing a link to recover your password has been sent to you");
            return tokenResponse;
        }

        PayAccessResponse tokenResponse = new PayAccessResponse();
        tokenResponse.setStatusCode("01");
        tokenResponse.setMessage("Invalid action. We could not recover your password");
        return tokenResponse;
    }


    public PayAccessResponse forgotUserPassword(String emailAddress, String forgotPasswordLink, String newPassword) {
        User user = userDao.handleRecoverUserPassword(emailAddress, forgotPasswordLink, newPassword);
        if(user!=null)
        {

//            try {
//                logger.info("=========================");
//                Properties props = System.getProperties();
//                props.put("mail.smtps.host", "smtp.mailgun.org");
//                props.put("mail.smtps.auth", "true");
//
//                Session session = Session.getInstance(props, null);
//                Message msg = new MimeMessage(session);
//                msg.setFrom(new InternetAddress("test@mails.valuenaira.com"));
//
//                InternetAddress[] addrs = InternetAddress.parse(emailAddress, false);
//                msg.setRecipients(Message.RecipientType.TO, addrs);
//
//                msg.setSubject("Account Update");
//                msg.setText("We noticed an update to your account");
//
//                msg.setSentDate(new Date());
//
//                SMTPTransport t =
//                        (SMTPTransport) session.getTransport("smtps");
//                t.connect("smtp.mailgun.org", "postmaster@mails.valuenaira.com", "k0l01qaz!QAZ");
//                t.sendMessage(msg, msg.getAllRecipients());
//
//                logger.info("Response: {}" , t.getLastServerResponse());
//
//                t.close();
//            }
//            catch(Exception e)
//            {
//                e.printStackTrace();
//                logger.error("Error Sending Mail ...{}", e);
//            }

            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode("00");
            payAccessResponse.setMessage("Password has been reset successfully");
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode("01");
        payAccessResponse.setMessage("Password could not be reset successfully");
        return payAccessResponse;
    }


    public PayAccessResponse updateUserPassword(String password, String newPassword, User authenticatedUser)
    {
        Optional<User> userInDBOpt = userDao.get(authenticatedUser.getId());
        if(userInDBOpt.isPresent())
        {
            User userInDB = userInDBOpt.get();
            boolean matchedPin = BCrypt.checkpw(password, userInDB.getPassword());

            if(matchedPin==false)
            {
                PayAccessResponse payAccessResponse = new PayAccessResponse();
                payAccessResponse.setStatusCode("01");
                payAccessResponse.setMessage("Invalid current password provided");
                return payAccessResponse;
            }

            User user = userDao.updateUserPassword(password, newPassword, authenticatedUser.getId());
            if(user!=null)
            {

                PayAccessResponse payAccessResponse = new PayAccessResponse();
                payAccessResponse.setStatusCode("00");
                payAccessResponse.setMessage("Password has been updated successfully");
                return payAccessResponse;
            }
        }


        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode("01");
        payAccessResponse.setMessage("Password has not been updated successfully");
        return payAccessResponse;
    }

    public PayAccessResponse resendSignUpOtp(UserSignUpRequest userSignUpRequest, Long otpExpiryPeriod, String localhostDomainEndpoint, int serverPort) {
        List<User> existingUser = userDao.getUserByEmailAddress(userSignUpRequest.getEmailAddress());
        if(existingUser!=null && existingUser.isEmpty())
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("OTP resend was not successful");
            return payAccessResponse;
        }

        User user = existingUser.get(0);

        if(!user.getUserStatus().equals(UserStatus.NOT_ACTIVATED))
        {
            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
            payAccessResponse.setMessage("OTP resend was not successful");
            return payAccessResponse;
        }
        String otp = RandomStringUtils.randomNumeric(6);
        LocalDateTime otpExpiryDate = LocalDateTime.now().plusSeconds(otpExpiryPeriod);
        user.setOtp(otp);
        user.setOtpExpiryDate(otpExpiryDate);
        user = userDao.update(user);

        if(user!=null)
        {
            String htmlMessage = "<div style='background:#f5f5f5;background-color:#f5f5f5;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#f5f5f5;background-color:#f5f5f5;width:100%;'>	<tbody><tr>	<td><div style='Margin:0px auto;max-width:780px;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='width:100%;max-width:780px;background:#F5F5F5;background-color:#F5F5F5;'><tbody>	<tr><td style='direction:ltr;font-size:0px;padding-bottom:0px;text-align:center;vertical-align:top;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:transparent;background-color:transparent;width:100%;'><tbody>	<tr><td>	<div style='Margin:0px auto;max-width:620px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:30px;padding-bottom:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'>  </table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table><table align='center' style='width:100%;max-width:780px;background:#F5F5F5;background-color:#F5F5F5;' '=''>	<tbody><tr style='width:{headerImageWidth}px;'>	<td align='center' style='font-size:0px;padding:0px;word-break:break-word;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='border-collapse:collapse;border-spacing:0px;'>	<tbody><tr>	<td style='width:780px;padding:0pm 0px 0px 0px;padding-bottom:0px;'><img alt='Vend' height='auto' src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_header.png' style='border:0;display:block;outline:none;text-decoration:none;height:auto;width:100%;' width='{headerImageWidth}'> 	</td></tr>	</tbody></table>	</td></tr>	</tbody></table><div class='main-content' style='background:#fff;background-color:#fff;Margin:0px auto;max-width:780px;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#fff;background-color:#fff;width:100%'><tbody>	<tr><td colspan='3' style='height:30px'></td>	</tr>	<tr><td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;' class='mktoContainer' id='container'>	<table class='mktoModule' id='textSection' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable'><h1 style='text-align: center;'>OTP Resend.</h1><p style='padding-bottom: 20px;'>&nbsp;<br>Hello "+user.getFirstName()+"! Your new OTP to complete your registrations is "+otp+"</p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='textSection4fc63ee1-701e-488b-a2a4-b1d5cdffd971' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable4fc63ee1-701e-488b-a2a4-b1d5cdffd971'><table width='100%' style='background-color: #e9f6e8;'>	<tbody><tr>	<td width='10%'>&nbsp;</td>	<td width='80%' align='center' style='text-align: center;'><h2 style='font-family: Helvetica, Arial, sans-serif; color: #626262; padding-top: 20px; padding-bottom: 10px;'>Your PayAccess Login Details</h2><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262; padding-bottom: 10px;'>Your Username/Email is: "+ user.getEmailAddress() +"<br>Enter the OTP: "+ otp +"</p><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='border-collapse: separate; line-height: 100%;'>	<tbody><tr>	<td align='center' bgcolor='#41af4b' role='presentation' style='border: 2px solid transparent; border-radius: 0px; cursor: auto; padding: 14px 24px;' valign='middle'>&nbsp;</td></tr>	</tbody></table><p>&nbsp;</p>	</td>	<td width='10%'>&nbsp;</td></tr>	</tbody></table><p>&nbsp;</p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='textSection4fc63ee1-701e-488b-a2a4-b1d5cdffd9718b5d4c4a-486c-40c3-9236-43cdf187ad89' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable4fc63ee1-701e-488b-a2a4-b1d5cdffd9718b5d4c4a-486c-40c3-9236-43cdf187ad89'><table width='100%' style='background-color: #f8f8f5;'>	<tbody><tr>	<td width='15%'>&nbsp;</td>	<td width='70%' align='center' style='text-align: center;'><h2 style='font-family: Helvetica, Arial, sans-serif; color: #626262; padding-top: 20px; padding-bottom: 10px;'>Set up PayAccess</h2><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>Get started with PayAccess by reading these four essential guides from our Help Centre and you'll be selling in no time!</p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_01.png' alt='V2439-Adoption-Onboarding-nurture-email-1_01.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Set up your outlets and registers</h3><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>Learn how to get PayAccess running on all of your registers and outlets.<br><a href='#'>Learn about registers</a></p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_02.png' alt='V2439-Adoption-Onboarding-nurture-email-1_02.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Organise your sales taxes</h3><p style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>Add any sales or value-added taxes (VAT) that are for your location&nbsp;or&nbsp;products.<br><a href='#'>Learn about taxes</a></p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_03.png' alt='V2439-Adoption-Onboarding-nurture-email-1_03.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Create different payment types</h3><p>Set up your payment terminal and registers so you can accept cash, cards and other&nbsp;payment&nbsp;types.<br><a href='#'>Learn about integrated payments</a></p><p><img src='https://retail.vendhq.com/rs/776-QFO-334/images/V2439-Adoption-Onboarding-nurture-email-0A_04.png' alt='V2439-Adoption-Onboarding-nurture-email-1_04.png' height='165' width='230' style='padding-top: 15px; width=230px;height: 165px;'></p><h3 style='margin-bottom: 0px!important;'>Add a product</h3><p>Start adding your products, stock levels and descriptions to&nbsp;your&nbsp;catalog.<br><a href='#'>Learn how to add products</a></p><p>&nbsp;</p>	</td>	<td width='15%'>&nbsp;</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='textSection2b16ab9a-73ae-43cf-8972-6db3159390c2' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;' class='mktoText' id='textSectionEditable2b16ab9a-73ae-43cf-8972-6db3159390c2'><p>&nbsp;</p><h2>Your PayAccess to-do list</h2><p>Like to read ahead? Our <a href='#'>setup checklist</a> gives you a list of steps that you can check off at your own pace to get PayAccess set up.</p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table>	<table class='mktoModule' id='signoffWithoutProfilePhoto15f0ef74-c2b9-420f-a62b-2e96f521ed08' style='width:100%;'><tbody>	<tr><td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>	<div style='background:white;background-color:white;Margin:0px auto;max-width:680px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:white;background-color:white;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:0px;padding-top:30px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='left' style='font-size:0px;padding:0px;word-break:break-word;'>	<div style='font-family:Helvetica, Arial, sans-serif;font-size:16px;font-weight:300;line-height:24px;text-align:left;color:#626262;'><p>Here's to your retail success, <br><br> <strong>Peters</strong> <br>Director of Adoption <br> </p>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></td>	</tr>	<tr><td colspan='3' style='height:50px'></td>	</tr></tbody>	</table></div><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#f5f5f5;background-color:#f5f5f5;width:100%;'>	<tbody><tr>	<td><div style='Margin:0px auto;max-width:680px;'>	<table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='width:100%;'><tbody>	<tr><td style='direction:ltr;font-size:0px;padding:5px;text-align:center;vertical-align:top;'>	<div style='background:#f5f5f5;background-color:#f5f5f5;Margin:0px auto;max-width:650px;'><table align='center' border='0' cellpadding='0' cellspacing='0' role='presentation' style='background:#f5f5f5;background-color:#f5f5f5;width:100%;'>	<tbody><tr>	<td style='direction:ltr;font-size:0px;padding:15px;text-align:center;vertical-align:top;'><div class='mj-column-per-100 outlook-group-fix' style='font-size:13px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' width='100%'><tbody>	<tr><td style='vertical-align:top;padding:0px 0px;'>	<table border='0' cellpadding='0' cellspacing='0' role='presentation' style='' width='100%'><tbody>	<tr><td align='center' style='font-size:0px;padding:0px;word-break:break-word;'>	<div class='mktoSnippet' id='unsubscribeFooter'><div style='font-family: Helvetica, Arial, sans-serif; font-size: 16px; font-weight: 300; line-height: 24px; text-align: center; color: #626262;'>	PayAccess HQ, 2-36 Obalende Street, Abuja, Nigeria 	<br> 	<a style='color: #008cc5 !important; text-decoration: none !important;' href='#' target='_blank'>Unsubscribe</a> ∙ 	<a style='color: #008cc5 !important; text-decoration: none !important;' href='https://email.vendhq.com/Nzc2LVFGTy0zMzQAAAF_hWiDyzkDXU0MPpA_mZQkOV6uelqxQNlKl80Dp7nbfZsoBZZomppxXFRKN_z6O69Y_RlWN_c=' target='_blank'>Privacy Policy</a></div>	</div></td>	</tr></tbody>	</table></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table>	</div></td>	</tr></tbody>	</table></div>	</td></tr>	</tbody></table></div>";

            try {
                logger.info("=========================");
                Properties props = System.getProperties();
                props.put("mail.smtps.host", "smtp.mailgun.org");
                props.put("mail.smtps.auth", "true");

                Session session = Session.getInstance(props, null);
                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress("emailer@payaccess.com", "PayAccess"));

                InternetAddress[] addrs = InternetAddress.parse(userSignUpRequest.getEmailAddress(), false);
                msg.setRecipients(Message.RecipientType.TO, addrs);

                msg.setSubject("Welcome to PayAccess");
                msg.setContent(htmlMessage, "text/html; charset=utf-8");

                //msg.setText("Copy the url and paste in your browser to activate your account - http://137.184.47.182:8081/payaccess/api/v1/user/activate-account/"+user.getEmailAddress()+"/" + verificationLink +" - providing the OTP: " + otp);

                msg.setSentDate(new Date());

                SMTPTransport t =
                        (SMTPTransport) session.getTransport("smtps");
                t.connect("smtp.mailgun.org", "postmaster@mails.valuenaira.com", "k0l01qaz!QAZ");
                t.sendMessage(msg, msg.getAllRecipients());

                logger.info("Response: {}" , t.getLastServerResponse());

                t.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                logger.error("Error Sending Mail ...{}", e);
            }

            PayAccessResponse payAccessResponse = new PayAccessResponse();
            payAccessResponse.setStatusCode(PayAccessStatusCode.SUCCESS.label);
            payAccessResponse.setMessage("A new OTP has been sent to your email - '"+userSignUpRequest.getEmailAddress().toLowerCase()+"'. Please check your email to complete your registration.");
            return payAccessResponse;
        }

        PayAccessResponse payAccessResponse = new PayAccessResponse();
        payAccessResponse.setStatusCode(PayAccessStatusCode.GENERAL_ERROR.label);
        payAccessResponse.setMessage("OTP could not be regenerated. Please try again");
        return payAccessResponse;
    }
}
