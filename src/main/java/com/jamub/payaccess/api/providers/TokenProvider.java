package com.jamub.payaccess.api.providers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamub.payaccess.api.models.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import io.jsonwebtoken.Claims;

import javax.management.ObjectName;


@Component
public class TokenProvider implements Serializable {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    public String generateToken(Authentication authentication) {
//        logger.info(">>>>>>>><<<<<<");
//        return "";
//    }
//
//    public Boolean validateToken(String token, UserDetails userDetails) {
//        return true;
//    }
//
//
//    UsernamePasswordAuthenticationToken getAuthenticationToken(final String token, final Authentication existingAuth, final UserDetails userDetails) {
//
//        final Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
//        logger.info("{}", 55555);
//
//        return new UsernamePasswordAuthenticationToken(existingAuth.getPrincipal(), existingAuth.getCredentials(), authorities);
//    }
//
//
//    public String getUsernameFromToken(String token) {
//        return "Test";
//    }


    private static final long serialVersionUID = -2550185165626007488L;

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    @Value("${jwt.signing.key}")
    private String secret;

    @Value("${jwt.authorities.key}")
    private String roleKey;

    //retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    //for retrieveing any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).build().parseClaimsJws(token).getBody();
    }

    //check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //generate token for user
    public String generateToken(Authentication userDetails) throws JsonProcessingException {
        Map<String, Object> claims = new HashMap<>();
        claims.put(roleKey, userDetails.getAuthorities());
//        claims.put("username", ((User)(userDetails.getPrincipal())).getEmailAddress());
        claims.put("user", userDetails.getPrincipal());
        return doGenerateToken(claims, ((User)(userDetails.getPrincipal())).getEmailAddress());
    }

    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, Object> claims, String subject) throws JsonProcessingException {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    //validate token
    public Boolean validateToken(String token
//            , UserDetails userDetails
    ) {
//        final String username = getUsernameFromToken(token);
        return
//                (username.equals(userDetails.getUsername()) &&
                !isTokenExpired(token);
//        );
    }

    public User getUserFromToken(String token) {
//        Claims c = Jwts.parser().setSigningKey(secret).build().parseClaimsJws(token).getBody();
//        return c.getSubject();
//        return getClaimFromToken(token, Claims::getSubject);
//        Jwts.builder().claims().
        Claims c = Jwts.parser().setSigningKey(secret).build().parseClaimsJws(token).getBody();
        LinkedHashMap lhm = (LinkedHashMap)(c.get("user"));
        logger.info("{}", lhm);
        logger.info("{}", lhm.get("id"));

        ObjectMapper mapper = new ObjectMapper();
        User user = mapper.convertValue(lhm, User.class);

        return user;
    }
}
