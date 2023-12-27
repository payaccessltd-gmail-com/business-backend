package com.jamub.payaccess.api.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamub.payaccess.api.enums.UserRole;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.response.ISWAuthTokenResponse;
import com.jamub.payaccess.api.models.response.PayAccessAuthResponse;
import lombok.SneakyThrows;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashSet;
import java.util.Set;


@Component
public class PayAccessAuthenticationProvider implements AuthenticationProvider {

    public PayAccessAuthenticationProvider() {
        super();
    }
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestTemplate restTemplate;

    @SneakyThrows
    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        String username = authentication.getPrincipal().toString(); // (1)

        logger.info("{} ... {}", username, authentication);
        String password = authentication.getCredentials().toString(); // (1)

        logger.info("{} ... {}", username, password);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
//        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
//        map.add("username",username);
//        map.add("password",password);
//        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        JSONObject personJsonObject = new JSONObject();
        personJsonObject.put("username", username);
        personJsonObject.put("password", password);
        String req = new ObjectMapper().writeValueAsString(personJsonObject);

        HttpEntity<String> entity =
                new HttpEntity<String>(req, headers);

        String uri = UriComponentsBuilder
                .fromUriString("http://localhost:8088/token-issuer-1.0.0/api/authenticate")
                .build()
                .toString();



        ResponseEntity<PayAccessAuthResponse> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, entity, PayAccessAuthResponse.class);
        PayAccessAuthResponse payAccessAuthResponse = responseEntity.getBody();
        logger.info("{}", payAccessAuthResponse);
        logger.info("{} merchantList", payAccessAuthResponse.getMerchantList());
//        logger.info("iswAuthTokenResponse...{}", payAccessAuthResponse.getAccess_token());
        HttpStatus httpStatus = responseEntity.getStatusCode();

//        User user = payAccessAuthResponse.getAuthenticatedUser();
        UserRole userRole = payAccessAuthResponse.getRole();
        String subj = payAccessAuthResponse.getSubject();

        logger.info("subj .. {}", subj);

        logger.info("userRole .. {}", userRole);
        User user = new ObjectMapper().readValue(subj, User.class);
        logger.info("use id .. {}", user.getId());

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.name()));


        UsernamePasswordAuthenticationToken r = new UsernamePasswordAuthenticationToken(user, password, authorities); // (4)
        logger.info("{}", r);
        logger.info("{}", r.isAuthenticated());

        return r;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        logger.info("{}", authentication.getCanonicalName());
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
