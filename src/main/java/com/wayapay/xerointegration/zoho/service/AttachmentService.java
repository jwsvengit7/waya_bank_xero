package com.wayapay.xerointegration.zoho.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
public class AttachmentService {


//    @Autowired
//    private ZohoAuthService zohoAuthService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${waya.authentication.url}")
    private String wayaAuthUrl;

    @Value("${waya.personalkyc.url}")
    private String wayaPersonalKycUrl;

    @Value("${waya.businesskyc.url}")
    private String wayaBusinessKycUrl;

    @Value("${waya.authentication.username}")
    private String wayaAuthUsername;

    @Value("${waya.authentication.password}")
    private String wayaAuthPassword;

    @Value("${waya.customers.accounts}")
    private String wayaAccountCustomers;

    String zohoApiUrl = "https://www.zohoapis.com/crm/v2/Customers";

    @Async("threadPoolTaskExecutor")
    public CompletableFuture<Void> sendAttachmentsAsync(String recordId, String userId, String zohoToken) {
//        String zohoToken = zohoAuthService.fetchTokens();


        String wayaToken = getWayaToken();
//        updateZohoRecord(recordId, accountNumber, zohoToken);
        sendAttachments(recordId, userId, zohoToken, wayaToken);
        customersAccounts(recordId, userId, zohoToken, wayaToken);
        return CompletableFuture.completedFuture(null);
    }


    public String getWayaToken() {
        RestTemplate restTemplate = new RestTemplate();
        String requestUrl = wayaAuthUrl;

        String token = "";
        // Create a JSON object for the request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("emailOrPhoneNumber", wayaAuthUsername);
        requestBody.put("password", wayaAuthPassword);

        // Create HttpHeaders and set the content type to JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create HttpEntity with the JSON body and headers
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        try {
            // Send POST request and capture response
            ResponseEntity<String> response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Handle the response
            if (response.getStatusCode() == HttpStatus.OK) {
                // Convert response body to JSON
                JSONObject responseBody = new JSONObject(response.getBody());

                // Extract the token from the 'data' field
                token = responseBody.getJSONObject("data").getString("token");
            }
        } catch (HttpClientErrorException e) {
            System.out.println("HTTP Error: " + e.getStatusCode() + " " + e.getStatusText());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return token;
    }

    public void sendAttachments(String recordId, String userId, String zohoToken, String wayaToken) {

                // Now, make the second API call using this token
                String apiEndpoint = String.format("%s/%s", wayaPersonalKycUrl, userId);

                // Set the Authorization header with Bearer token
                HttpHeaders newHeaders = new HttpHeaders();
                newHeaders.setContentType(MediaType.APPLICATION_JSON);
                newHeaders.set("Authorization", wayaToken); // Corrected header format

                // Create an HttpEntity with the new headers
                HttpEntity<String> newEntity = new HttpEntity<>(newHeaders);

                // Make the second API call
                ResponseEntity<String> apiResponse = restTemplate.exchange(
                        apiEndpoint,
                        HttpMethod.POST, // Changed to GET as per API specification
                        newEntity,
                        String.class
                );

                // Print the response of the second API call
                String apiResponseBody = apiResponse.getBody();
                System.out.println("API Response: " + apiResponseBody);

                // Parse the API response to extract tier4 and tier3 attachments
                JSONObject jsonResponse = new JSONObject(apiResponseBody);
                JSONArray tier4Array = jsonResponse.getJSONObject("data").getJSONArray("tier4");
                JSONArray tier3Array = jsonResponse.getJSONObject("data").getJSONArray("tier3");

                // Collect tier4 and tier3 attachment URLs
                List<String> tier4Attachments = new ArrayList<>();
                List<String> tier3Attachments = new ArrayList<>();

                for (int i = 0; i < tier4Array.length(); i++) {
                    JSONObject tier4Item = tier4Array.getJSONObject(i);
                    String attachment = tier4Item.optString("attachment", null);
                    if (attachment != null) {
                        tier4Attachments.add(attachment);
                    }
                }

                for (int i = 0; i < tier3Array.length(); i++) {
                    JSONObject tier3Item = tier3Array.getJSONObject(i);
                    String attachment = tier3Item.optString("attachment", null);
                    if (attachment != null) {
                        tier3Attachments.add(attachment);
                    }
                }

                // Print the list of tier4 attachments
                System.out.println("Tier 4 Attachments: " + tier4Attachments);
                System.out.println("Tier 3 Attachments: " + tier3Attachments);

                // Combine the two lists of attachments (you can also process them separately if needed)
                List<String> allAttachments = new ArrayList<>(tier4Attachments);
                allAttachments.addAll(tier3Attachments);
        if(!allAttachments.isEmpty()){
            // Download each attachment and upload to Zoho CRM
            for (String attachmentUrl : allAttachments) {
                downloadAndAttachToZoho(attachmentUrl, recordId, zohoToken);
            }
        }

//                return ResponseEntity.ok(apiResponseBody);


    }


    private void downloadAndAttachToZoho(String attachmentUrl, String recordId, String zohoToken) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            // Download the file
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    attachmentUrl,
                    HttpMethod.GET,
                    null,
                    byte[].class
            );

            // Extract file content
            byte[] fileContent = response.getBody();

            if (fileContent != null && fileContent.length > 0) {
                System.out.println("Successfully downloaded file. Size: " + fileContent.length + " bytes");

                // Prepare the request for Zoho attachment API
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                headers.setBearerAuth(zohoToken);

                // Extract filename from URL
                String filename = attachmentUrl.substring(attachmentUrl.lastIndexOf('/') + 1);

                // Create the MultiValueMap for the file
                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("file", new ByteArrayResource(fileContent) {
                    @Override
                    public String getFilename() {
                        return filename;
                    }
                });

                // Create the HttpEntity for the file attachment request
                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

                // Attach the file to Zoho CRM
                String zohoApiUrl = String.format("https://www.zohoapis.com/crm/v2/Customers/%s/Attachments", recordId);
                ResponseEntity<String> uploadResponse = restTemplate.exchange(
                        zohoApiUrl,
                        HttpMethod.POST,
                        requestEntity,
                        String.class
                );

                // Check if the attachment was successful
                if (uploadResponse.getStatusCode() == HttpStatus.OK || uploadResponse.getStatusCode() == HttpStatus.CREATED) {
                    System.out.println("File successfully attached to Zoho record: " + recordId);
                    System.out.println("Response body: " + uploadResponse.getBody());
                } else {
                    System.out.println("Unexpected response when attaching file to Zoho: " + uploadResponse.getStatusCode());
                    System.out.println("Response body: " + uploadResponse.getBody());
                }
            } else {
                System.out.println("Downloaded file is empty or null");
            }
        } catch (HttpClientErrorException e) {
            System.out.println("HTTP Error when downloading or attaching file: " + e.getStatusCode() + " " + e.getStatusText());
            System.out.println("Error response body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unexpected error when downloading or attaching file: " + e.getMessage());
        }

}


    public void updateZohoRecord(String recordId, String accountNumber, String zohoToken) {
        RestTemplate restTemplate = new RestTemplate();

        // Create the update URL for the specific record in the Customers module
        String zohoApiUrl = String.format("https://www.zohoapis.com/crm/v2/Customers/%s", recordId);

        // Create the request body with the updated field(s)
        JSONObject requestBody = new JSONObject();
        JSONArray recordsArray = new JSONArray();
        JSONObject record = new JSONObject();

        // Update the field 'Account_Number'
        record.put("Account_Number", accountNumber);

        // Add the record to the array
        recordsArray.put(record);
        requestBody.put("data", recordsArray);

        // Set up the headers with the Zoho token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(zohoToken);

        // Create the HttpEntity with the request body and headers
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        try {
            // Send the update request to Zoho CRM
            ResponseEntity<String> response = restTemplate.exchange(
                    zohoApiUrl,
                    HttpMethod.PUT,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Record successfully updated: " + recordId);
                System.out.println("Response: " + response.getBody());
            } else {
                System.out.println("Failed to update record: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            System.out.println("HTTP Error when updating record: " + e.getStatusCode() + " " + e.getStatusText());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unexpected error when updating record: " + e.getMessage());
        }
    }


    public void customersAccounts(String recordId, String userId, String zohoToken, String wayaToken){

        // Now, make the second API call using this token
        String apiEndpoint = String.format("%s/%s", wayaAccountCustomers, userId);

        // Set the Authorization header with Bearer token
        HttpHeaders newHeaders = new HttpHeaders();
        newHeaders.setContentType(MediaType.APPLICATION_JSON);
        newHeaders.set("Authorization", wayaToken); // Corrected header format

        // Create an HttpEntity with the new headers
        HttpEntity<String> newEntity = new HttpEntity<>(newHeaders);

        // Make the second API call
        ResponseEntity<String> apiResponse = restTemplate.exchange(
                apiEndpoint,
                HttpMethod.GET, // Changed to GET as per API specification
                newEntity,
                String.class
        );

        // Print the response of the second API call
        String apiResponseBody = apiResponse.getBody();
        System.out.println("API Response for Customer Accounts: " + apiResponseBody);

        extractAccountNumbers(apiResponseBody, recordId, zohoToken);
    }


    public void extractAccountNumbers(String apiResponseBody, String recordId, String zohoToken) {
        // Parse the API response body into a JSON object
        JSONObject jsonResponse = new JSONObject(apiResponseBody);

        // Get the 'data' array from the JSON response
        JSONArray dataArray = jsonResponse.getJSONArray("data");

        // Initialize a list to store the account numbers
        List<String> accountNumbers = new ArrayList<>();

        // Loop through each object in the 'data' array
        for (int i = 0; i < dataArray.length(); i++) {
            // Get each object in the array
            JSONObject dataObject = dataArray.getJSONObject(i);

            // Extract the 'accountNo' key
            String accountNo = dataObject.getString("accountNo");

            // Add the account number to the list
            accountNumbers.add(accountNo);
        }

        // Print the list of account numbers
        System.out.println("List of Account Numbers: " + accountNumbers);

        // Create a JSON object to store the account number fields
        JSONObject accountNumberFields = new JSONObject();

        // Assign the account numbers to the appropriate fields
        for (int i = 0; i < accountNumbers.size(); i++) {
            if (i == 0) {
                accountNumberFields.put("Account_Number", accountNumbers.get(i));
            } else if (i == 1) {
                accountNumberFields.put("Account_Number_2", accountNumbers.get(i));
            } else if (i == 2) {
                accountNumberFields.put("Account_Number_3", accountNumbers.get(i));
            } else if (i == 3) {
                accountNumberFields.put("Account_Number_4", accountNumbers.get(i));
            } else if (i == 4) {
                accountNumberFields.put("Account_Number_5", accountNumbers.get(i));
            }
        }

        // Call the updateZohoRecord method with the account number fields
        updateZohoRecord(recordId, accountNumberFields, zohoToken);
    }

    public void updateZohoRecord(String recordId, JSONObject accountNumberFields, String zohoToken) {
        RestTemplate restTemplate = new RestTemplate();

        // Create the update URL for the specific record in the Customers module
        String zohoApiUrl = String.format("https://www.zohoapis.com/crm/v2/Customers/%s", recordId);

        // Create the request body with the updated field(s)
        JSONObject requestBody = new JSONObject();
        JSONArray recordsArray = new JSONArray();
        JSONObject record = new JSONObject();

        // Add all account number fields to the record
        for (String key : accountNumberFields.keySet()) {
            record.put(key, accountNumberFields.get(key));
        }

        // Add the record to the array
        recordsArray.put(record);
        requestBody.put("data", recordsArray);

        // Set up the headers with the Zoho token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(zohoToken);

        // Create the HttpEntity with the request body and headers
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        try {
            // Send the update request to Zoho CRM
            ResponseEntity<String> response = restTemplate.exchange(
                    zohoApiUrl,
                    HttpMethod.PUT,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Record successfully updated: " + recordId);
                System.out.println("Response: " + response.getBody());
            } else {
                System.out.println("Failed to update record: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            System.out.println("HTTP Error when updating record: " + e.getStatusCode() + " " + e.getStatusText());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unexpected error when updating record: " + e.getMessage());
        }
    }

}