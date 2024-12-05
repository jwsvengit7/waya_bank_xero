package com.wayapay.xerointegration.zoho.consumer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wayapay.xerointegration.payloads.*;

import com.wayapay.xerointegration.zoho.service.ZohoIntegrationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;



@Slf4j
@Service
public class KafkaConsumer {

    @Autowired
    ObjectMapper objectMapper;
    private final ZohoIntegrationService zohoIntegrationService;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.topics.registration}")
    private String registrationTopic;

    @Value("${spring.kafka.topics.wallet-transaction}")
    private String walletTransactionTopic;

    @Value("${spring.kafka.topics.kyc-limit-setup}")
    private String kycLimitSetupTopic;

    @Autowired
    public KafkaConsumer(ZohoIntegrationService zohoIntegrationService) {
        this.zohoIntegrationService = zohoIntegrationService;
    }

    @KafkaListener(topics = "${spring.kafka.topics.registration}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeRegistration(ConsumerRecord<String, String> record) {
        log.info("****************** LISTENING FOR CUSTOMER ***********************************************");
        log.info("Received registration record: {}", record);
        logRecordDetails(record);

        // Deserialize JSON string to CustomerDetails object
        String jsonValue = record.value();
        try {
            log.info("Trying to create Customer");
            CustomerDetails customerDetails = objectMapper.readValue(jsonValue, CustomerDetails.class);
            zohoIntegrationService.createCustomer(customerDetails);
        } catch (Exception e) {
            log.error("Failed to deserialize JSON and move data to Zoho Contacts", e);
        }
    }


    @KafkaListener(topics = "${spring.kafka.topics.wallet-transaction}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeWalletTransaction(ConsumerRecord<String, String> record) {
        log.info("**************** LISTENING FOR TRANSACTION *************************************************");
        log.info("Received wallet transaction record: {}", record);
        logRecordDetails(record);

        String transaction = record.value();
        try {

        log.info("Trying to create Wallet Transaction");
            Transaction transactionDetails = objectMapper.readValue(transaction, Transaction.class);
            zohoIntegrationService.createTransaction(transactionDetails);
        } catch (Exception e) {
            log.error("Failed to process wallet transaction", e);
        }
    }

    @KafkaListener(topics = "${spring.kafka.topics.kyc-limit-setup}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeKYCLimits(ConsumerRecord<String, String> record) {
        log.info("************** LISTENING FOR KYC LIMIT SETUP ***************************************************");
        log.info("Received KYC limit setup record: {}", record);
        logRecordDetails(record);

        String kycLimits = record.value();

        try {
        log.info("Trying to create KYC Limit");
            KYCLimits kycLimitDetails = objectMapper.readValue(kycLimits, KYCLimits.class);
            zohoIntegrationService.createOrUpdateKYCLimits(kycLimitDetails);
        } catch (Exception e) {
            log.error("Failed to process KYC limit setup", e);
        }
    }

    private void logRecordDetails(ConsumerRecord<?, ?> record) {
        log.info("Message key: {}", record.key());
        log.info("Message value: {}", record.value());
        log.info("Message partition: {}", record.partition());
        log.info("Message offset: {}", record.offset());
    }
}
