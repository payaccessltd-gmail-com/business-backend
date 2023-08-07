package com.jamub.payaccess.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class RestClientConfig {


    @Value("${request.connect-timeout}")
    private int connectTimeout;
    @Value("${request.read-timeout}")
    private int readTimeout;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
        rf.setConnectTimeout(connectTimeout);
        rf.setReadTimeout(readTimeout);
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(rf));

        return restTemplate;
    }
}
