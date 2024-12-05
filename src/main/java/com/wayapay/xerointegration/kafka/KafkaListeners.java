package com.wayapay.xerointegration.kafka;

import com.google.gson.Gson;
import com.wayapay.xerointegration.payloads.CustomerDetails;
import com.wayapay.xerointegration.service.XeroIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.waya.producer.payload.Transaction;

@Component
@EnableKafka
public class KafkaListeners {

    @Autowired
    private XeroIntegrationService xeroApiService;

    private static final Gson JSON = new Gson();
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaListeners.class);

    @KafkaListener(topics = "wallet-transaction", groupId = "${spring.kafka.group-id}")
    public void listener(String data) {
        LOGGER.warn("<<<--------------Listener received----------------->>>: " + data);
        Transaction transaction = JSON.fromJson(data, Transaction.class);
        LOGGER.info("transaction ---->>>> {}", transaction);
        xeroApiService.setXeroApiTransaction(transaction);
    }

    @KafkaListener(topics = "process-registration", groupId = "${spring.kafka.group-id}")
    public void customerListener(String data) {
        LOGGER.warn("<<<----------------customerListener received------------------>>>: " + data);
        CustomerDetails customerData = JSON.fromJson(data, CustomerDetails.class);
        LOGGER.info("customerListener ---->>>> {}", customerData);
        xeroApiService.setXeroApiCreateAccount(customerData);
    }
}
