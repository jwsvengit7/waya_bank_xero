package com.wayapay.xerointegration.seeder;


import com.wayapay.xerointegration.dto.waya.response.TransactionData;
import com.wayapay.xerointegration.dto.waya.response.WayaTransactionResponse;
import com.wayapay.xerointegration.service.XeroIntegrationService;
import com.wayapay.xerointegration.service.impl.XeroAuthorizationServiceImpl;
import com.wayapay.xerointegration.service.impl.XeroIntegrationServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.util.logging.Logger;

import static com.wayapay.xerointegration.constant.Utils.*;
import static com.wayapay.xerointegration.dto.waya.response.TransactionDataMock.createMockTransactionData;
import static com.wayapay.xerointegration.payloads.CustomerDetails.createMockCustomerDetails;
import static org.waya.producer.payload.Transaction.createDummyTransaction;

@Slf4j
@Component
public class Seeders implements CommandLineRunner {
    @Autowired
    XeroIntegrationService xeroApiService;
    @Autowired
    XeroAuthorizationServiceImpl xeroAuthorizationService;
    @Autowired
    RestTemplate restTemplate;

    @Override
    public void run(String... args) throws Exception {
//        xeroApiService.createXeroUploadPayload(createMockTransactionData());
//        xeroApiService.setXeroApiCreateAccount(createMockCustomerDetails());
//        xeroApiService.setXeroApiTransaction(createDummyTransaction());
        String token =xeroAuthorizationService.getAccessToken();
        log.info("token {}",token);
         String url = XERO_JOURNAL_ENDPOINT;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response =restTemplate.exchange(url, HttpMethod.GET,requestEntity, String.class);
       log.info("Response {}",response.getBody());


    }


//    @Override
//    public void run(String... args) {
//
//        xeroApiService.createXeroUploadPayloads(createMockTransactionData());
//
////        xeroApiService.uploadToXero(wayaTransactionResponse);
//
//    }
}
