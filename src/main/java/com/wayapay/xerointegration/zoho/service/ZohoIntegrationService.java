package com.wayapay.xerointegration.zoho.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wayapay.xerointegration.payloads.CustomerDetails;
import com.wayapay.xerointegration.payloads.KYCLimits;
import com.wayapay.xerointegration.payloads.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ZohoIntegrationService {

//    private static final Logger log = LoggerFactory.getLogger(ZohoIntegrationService.class);
    private final RestTemplate restTemplate;

    @Value("${zoho.client.id}")
    private String clientId;

    @Value("${zoho.api.base.url}")
    private String zohoContactsUrl;

    @Value("${zoho.api.customer.url}")
    private String zohoCustomersUrl;

    @Value("${zoho.api.transactions.url}")
    private String zohoTransactionsUrl;

    @Value("${zoho.api.kyclimits.url}")
    private String zohoKycLimitsUrl;

    @Value("${zoho.client.secret}")
    private String clientSecret;

    @Value("${zoho.token.url}")
    private String tokenUrl;

    @Value("${zoho.crm.soid}")
    private String soid;

    @Value("${zoho.crm.scope}")
    private String scope;

    @Value("${zoho.redirect.uri}")
    private String redirectUri;

    // Class-level variables for token and expiry time
    private String accessToken;
    private long tokenExpiryTime; // in milliseconds

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    public ZohoIntegrationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Method to check if the token is expired
    private boolean isTokenExpired() {
        return System.currentTimeMillis() >= tokenExpiryTime;
    }

    // Fetch tokens and update expiry time
    private void fetchTokens() {
        RestTemplate restTemplate = new RestTemplate();
        String requestUrl = String.format("%s?grant_type=client_credentials&client_id=%s&client_secret=%s&scope=%s&soid=%s",
                tokenUrl, clientId, clientSecret, scope, soid);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(requestUrl, HttpMethod.POST, requestEntity, JsonNode.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode body = response.getBody();
            assert body != null;
            this.accessToken = body.get("access_token").asText();
            int expiresIn = body.get("expires_in").asInt(); // assuming expires_in is in seconds
            this.tokenExpiryTime = System.currentTimeMillis() + (expiresIn * 1000L); // convert to milliseconds
        } else {
            throw new RuntimeException("Failed to fetch tokens");
        }
    }

    // Method to get a valid token (fetch or refresh if needed)
    private String getValidToken() {
        if (accessToken == null || isTokenExpired()) {
            fetchTokens();
        }
        return accessToken;
    }

    // Method to create or update a customer in Zoho
    public void createCustomer(CustomerDetails customerDetails) {
        String token = getValidToken();
        log.info("ZOHO User Token acquired to create customer");
        // Validate email
        String email = customerDetails.getEmail();
        if (email == null || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$")) {
            log.error("Invalid email format: {}", email);
            throw new RuntimeException("Invalid email format: " + email);
        }

        Map<String, Object> customerData = ZohoEntityMapper.mapCustomerToZoho(customerDetails);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("data", List.of(customerData));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            log.info("Calling Zoho Customer API Endpoint");
            ResponseEntity<String> response = restTemplate.postForEntity(zohoCustomersUrl, requestEntity, String.class);
            log.info("Awaiting response from Zoho");

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully created/updated customer in Zoho: {}", response.getBody());
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode dataNode = rootNode.path("data").get(0);
                String recordId = dataNode.path("details").path("id").asText();

                attachmentService.sendAttachmentsAsync(recordId, customerDetails.getUserId(), token);
            } else {
                log.error("Failed to create customer in Zoho. Status code: {}, Response: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to create/update customer in Zoho: " + response.getBody());
            }
        } catch (Exception e) {
            log.error("Error creating customer in Zoho", e);
            throw new RuntimeException("Error creating/updating customer in Zoho", e);
        }
    }

    // Method to create a transaction in Zoho
    public void createTransaction(Transaction transaction) {
        String token = getValidToken();
        log.info("ZOHO User Token acquired to create transaction");

        Map<String, Object> transactionData = ZohoEntityMapper.mapTransactionToZoho(transaction);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("data", Arrays.asList(transactionData));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            log.info("Calling Zoho Transaction API Endpoint");
            ResponseEntity<String> response = restTemplate.postForEntity(zohoTransactionsUrl, requestEntity, String.class);
            log.info("Awaiting response from Zoho");
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully created transaction in Zoho: {}", response.getBody());
            } else {
                log.error("Failed to create transaction in Zoho. Status code: {}, Response: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to create/update transaction in Zoho: " + response.getBody());
            }
        } catch (Exception e) {
            log.error("Error creating transaction in Zoho", e);
            throw new RuntimeException("Error creating transaction in Zoho", e);
        }
    }

    // Method to create or update KYC limits in Zoho
    public void createOrUpdateKYCLimits(KYCLimits kycLimits) {
        String token = getValidToken();
        log.info("ZOHO User Token acquired to create kyc limit");

        Map<String, Object> kycLimitsData = ZohoEntityMapper.mapKYCLimitsToZoho(kycLimits);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("data", List.of(kycLimitsData));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            log.info("Calling Zoho Transaction API Endpoint");
            ResponseEntity<String> response = restTemplate.postForEntity(zohoKycLimitsUrl, requestEntity, String.class);
            log.info("Awaiting response from Zoho");
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully created/updated KYCLimits in Zoho: {}", response.getBody());
            } else {
                log.error("Failed to create/update KYCLimits in Zoho. Status code: {}, Response: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to create/update KYCLimits in Zoho: " + response.getBody());
            }
        } catch (Exception e) {
            log.error("Error creating/updating KYCLimits in Zoho", e);
            throw new RuntimeException("Error creating/updating KYCLimits in Zoho", e);
        }
    }
}
