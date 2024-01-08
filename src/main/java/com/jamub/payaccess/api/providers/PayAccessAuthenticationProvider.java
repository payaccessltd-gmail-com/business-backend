package com.jamub.payaccess.api.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.jamub.payaccess.api.deserializers.TimestampDeserializer;
import com.jamub.payaccess.api.enums.UserRole;
import com.jamub.payaccess.api.exception.PayAccessAuthException;
import com.jamub.payaccess.api.models.User;
import com.jamub.payaccess.api.models.response.PayAccessAuthResponse;
import lombok.SneakyThrows;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;


@Component
public class PayAccessAuthenticationProvider implements AuthenticationProvider {

    public PayAccessAuthenticationProvider() {
        super();
    }
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Value("${authentication.provider.url}")
    private String authenticationProviderUrl;

    @SneakyThrows
    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {

//        JavaTimeModule javaTimeModule = new JavaTimeModule();
//        javaTimeModule.addDeserializer(LocalDateTime.class, new TimestampDeserializer());
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
//                .registerModule(javaTimeModule)
                .registerModule(new JavaTimeModule());


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
                .fromUriString(authenticationProviderUrl)
                .build()
                .toString();





        try
        {
            ResponseEntity<PayAccessAuthResponse> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, entity, PayAccessAuthResponse.class);
            if(responseEntity.getStatusCode().equals(HttpStatus.OK))
            {
                PayAccessAuthResponse payAccessAuthResponse = responseEntity.getBody();

                if(payAccessAuthResponse!=null && payAccessAuthResponse.getStatus().intValue()==1)
                {
                    throw new PayAccessAuthException(payAccessAuthResponse.getMessage());
                }
                logger.info("{}", payAccessAuthResponse);
                logger.info("{} merchantList", payAccessAuthResponse.getMerchantList());
//        logger.info("iswAuthTokenResponse...{}", payAccessAuthResponse.getAccess_token());
                HttpStatus httpStatus = responseEntity.getStatusCode();

//        User user = payAccessAuthResponse.getAuthenticatedUser();
                UserRole userRole = payAccessAuthResponse.getRole();
                String subj = payAccessAuthResponse.getSubject();

                logger.info("subj .. {}", subj);

                logger.info("userRole .. {}", userRole);
                User user = objectMapper.readValue(subj, User.class);
                logger.info("use id .. {}", user.getId());

                Set<SimpleGrantedAuthority> authorities = new HashSet<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + userRole.name()));


                UsernamePasswordAuthenticationToken r = new UsernamePasswordAuthenticationToken(user, password, authorities); // (4)
                logger.info("{}", r);
                logger.info("{}", r.isAuthenticated());

                return r;
            }
            else if(responseEntity.getStatusCode().equals(HttpStatus.UNAUTHORIZED))
            {
                throw new PayAccessAuthException("Invalid credentials");
            }

//            responseEntity.g

        }
        catch(HttpServerErrorException e)
        {
            logger.info("HttpServerErrorException ... {}", e.getResponseBodyAsString());
            throw new Exception("Connection to authentication server timed out");

        } catch (HttpClientErrorException e) {
            logger.info("{}", e.getResponseBodyAsString());
            throw new Exception("Connection to authentication server timed out");
        } catch (ResourceAccessException e) {
            logger.info("ResourceAccessException ... {}", e.getMessage());
            throw new TimeoutException("Connection to resource timed out");
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        logger.info("{}", authentication.getCanonicalName());
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
