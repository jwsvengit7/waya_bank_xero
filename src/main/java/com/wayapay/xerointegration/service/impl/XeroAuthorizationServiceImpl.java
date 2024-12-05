package com.wayapay.xerointegration.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wayapay.xerointegration.dto.xero.model.OAuth2TokenResponse;
import com.wayapay.xerointegration.service.Oath2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class XeroAuthorizationServiceImpl implements Oath2Service {

    private final static Logger LOGGER = Logger.getLogger(XeroAuthorizationServiceImpl.class.getName());
    private final RestTemplate restTemplate;
    private final HttpHeaders headers;

    private String accessToken;
    private String scope;
    private long expiryTime;

    @Value("${xero.client-id}")
    private  String CLIENT_ID;

    @Value("${xero.client-secret}")
    private String CLIENT_SECRET;

    @Value("${xero.token-url}")
    private String IDENTITY_URL;
    @Value("${xero.grant-type}")
    private String xeroGrantType;





    public String getAccessToken() {
        if (accessToken == null || isTokenExpired()) {
            refreshAccessToken();
        }
        return accessToken;
    }

    private boolean isTokenExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
    private void refreshAccessToken() {
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", xeroGrantType);
        body.add("client_id", CLIENT_ID);
        body.add("client_secret", CLIENT_SECRET);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(IDENTITY_URL, HttpMethod.POST, request, String.class);
            LOGGER.info(" response: " + response);


            HttpHeaders responseHeaders = response.getHeaders();


            String xeroTenantId = responseHeaders.getFirst("x-xero-tenant-id");

            LOGGER.info(" response: " + response);
            LOGGER.info(" xeroTenantId: " + xeroTenantId);
            LOGGER.info("Raw response: " + response.getBody());

            ObjectMapper objectMapper = new ObjectMapper();
            OAuth2TokenResponse tokenResponse = objectMapper.readValue(response.getBody(), OAuth2TokenResponse.class);
            // Log raw response body
            LOGGER.info("tokenResponse response: " + tokenResponse);
            if (tokenResponse != null && tokenResponse.getExpiresIn() != null) {
                this.accessToken=tokenResponse.getAccessToken();
                this.scope=tokenResponse.getScope();
                this.expiryTime=System.currentTimeMillis() + tokenResponse.getExpiresIn() * 1000;
                LOGGER.info("tokenResponse response: " + accessToken);
            } else {
                LOGGER.severe("Invalid token response: " + tokenResponse);
            }

        } catch (Exception e) {
            LOGGER.warning("Failed to refresh access token"+ e.getMessage());
        }
    }

}


