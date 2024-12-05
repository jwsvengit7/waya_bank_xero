package com.wayapay.xerointegration.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.wayapay.xerointegration.constant.AccountType;
import com.wayapay.xerointegration.dto.waya.response.TransactionData;
import com.wayapay.xerointegration.dto.xero.request.JournalLines;
import com.wayapay.xerointegration.dto.xero.request.Tracking;
import com.wayapay.xerointegration.dto.xero.request.XeroManualJournalUploadRequest;
import com.wayapay.xerointegration.dto.xero.response.BankTransactionResponse;
import com.wayapay.xerointegration.service.GenericService;
import com.wayapay.xerointegration.service.Oath2Service;
import com.wayapay.xerointegration.service.XeroIntegrationService;
import com.xero.api.XeroApiException;
import com.xero.models.accounting.*;
import com.wayapay.xerointegration.dto.waya.request.WayaTransactionRequest;
import com.wayapay.xerointegration.dto.waya.response.WayaTransactionResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import com.wayapay.xerointegration.payloads.CustomerDetails;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.waya.producer.payload.Transaction;

import java.io.IOException;
import java.util.*;

import static com.wayapay.xerointegration.constant.Utils.*;

@Service
@RequiredArgsConstructor
public class XeroIntegrationServiceImpl implements XeroIntegrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(XeroIntegrationServiceImpl.class);
    private static final String NIGERIA_COUNTRY = "Nigeria";
    private static final String DEFAULT_POSTAL_CODE = "000000";
    private static final Gson JSON = new Gson();

    private final Oath2Service oAuth2Service;
    private final RestTemplate restTemplate;

    @Value("${waya.transaction}")
    private String wayaTransaction;

    @Value("${waya.token}")
    private String wayaToken;



    @Override
    public WayaTransactionResponse getTransactionFromWaya(WayaTransactionRequest wayaTransactionRequest) {
        String url = wayaTransaction + wayaTransactionRequest.getTransactionId();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", wayaToken);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        try {
            WayaTransactionResponse response = new ObjectMapper().readValue(responseEntity.getBody(), WayaTransactionResponse.class);
            uploadToXero(response);
            return response;
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }


    }

    public void createContact(String accessToken, Contacts contacts) {
        String url = XERO_CREATE_CUSTOMER_ENDPOINT;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/json");

        HashMap<String, Object> contactMap = new HashMap<>();

        List<Contact> contactList = contacts.getContacts();
        if (contactList != null && !contactList.isEmpty()) {
            Contact contact = contactList.get(0);

            contactMap.put("name", contact.getName());
            contactMap.put("firstName", contact.getFirstName());
            contactMap.put("lastName", contact.getLastName());
            contactMap.put("emailAddress", contact.getEmailAddress());
            contactMap.put("contactStatus", contact.getContactStatus());
            contactMap.put("isSupplier", contact.getIsSupplier());
            contactMap.put("isCustomer", contact.getIsCustomer());

            // Handle addresses
            List<Address> addresses = contact.getAddresses();
            if (addresses != null && !addresses.isEmpty()) {
                List<HashMap<String, Object>> addressList = new ArrayList<>();
                for (Address address : addresses) {
                    HashMap<String, Object> addressMap = new HashMap<>();
                    addressMap.put("addressLine1", address.getAddressLine1());
                    addressMap.put("city", address.getCity());
                    addressMap.put("region", address.getRegion());
                    addressMap.put("postalCode", address.getPostalCode());
                    addressMap.put("country", address.getCountry());
                    addressList.add(addressMap);
                }
                contactMap.put("addresses", addressList);
            }

            // Handle phones
            List<Phone> phones = contact.getPhones();
            if (phones != null && !phones.isEmpty()) {
                List<HashMap<String, String>> phoneList = new ArrayList<>();
                for (Phone phone : phones) {
                    HashMap<String, String> phoneMap = new HashMap<>();
                    phoneMap.put("phoneNumber", phone.getPhoneNumber());
                    phoneList.add(phoneMap);
                }
                contactMap.put("phones", phoneList);
            }
        }

        // Create the request entity with the HashMap
        HttpEntity<HashMap<String, Object>> requestEntity = new HttpEntity<>(contactMap, headers);

        try {
            ResponseEntity<HashMap> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, HashMap.class);
            LOGGER.info("Contact created successfully: {}", responseEntity.getBody());
        } catch (Exception e) {
            LOGGER.error("Error creating contact: {}", e.getMessage());
            throw new RuntimeException("Failed to create contact", e);
        }
    }

    private void createBankTransaction(String accessToken, BankTransaction bankTransaction) {
        String url = XERO_BANK_TRANSACTION_ENDPOINT;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/json");

        // Convert BankTransaction to HashMap
        HashMap<String, Object> bankTransactionMap = new HashMap<>();
        bankTransactionMap.put("Type", bankTransaction.getType());
        bankTransactionMap.put("Reference", bankTransaction.getReference());
        bankTransactionMap.put("Contact", buildContactMap(bankTransaction.getContact()));

        // Convert LineItems
        List<HashMap<String, Object>> lineItemsList = new ArrayList<>();
        for (LineItem lineItem : bankTransaction.getLineItems()) {
            HashMap<String, Object> lineItemMap = new HashMap<>();
            lineItemMap.put("Description", lineItem.getDescription());
            lineItemMap.put("Quantity", lineItem.getQuantity());
            lineItemMap.put("UnitAmount", lineItem.getUnitAmount());
            lineItemMap.put("AccountCode", lineItem.getAccountCode());
            lineItemsList.add(lineItemMap);
        }
        bankTransactionMap.put("LineItems", lineItemsList);

        if (bankTransaction.getBankAccount() != null) {
            HashMap<String, Object> bankAccountMap = new HashMap<>();
//            bankAccountMap.put("AccountNumber", bankTransaction.getBankAccount().getBankAccountNumber());
//            bankAccountMap.put("Name", bankTransaction.getBankAccount().getName());
            bankAccountMap.put("Code", AccountType.fromCode(bankTransaction.getBankAccount().getCode()).getCode());
            bankAccountMap.put("Status", bankTransaction.getBankAccount().getStatus());
            bankAccountMap.put("Description", bankTransaction.getBankAccount().getDescription());
            bankAccountMap.put("Type", bankTransaction.getBankAccount().getType());
            bankAccountMap.put("AccountID",AccountType.fromAccountCode(bankTransaction.getBankAccount().getCode()).getAccountCode());
            bankTransactionMap.put("BankAccount", bankAccountMap);
        }
        LOGGER.info("******************");
        LOGGER.info("Bank transaction : {}", bankTransactionMap);
        HttpEntity<HashMap<String, Object>> requestEntity = new HttpEntity<>(bankTransactionMap, headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            LOGGER.info("Bank transaction created successfully: {}", responseEntity.getBody());
        } catch (HttpClientErrorException e) {
            LOGGER.error("Error creating bank transaction: {}, Response Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to create bank transaction", e);
        }
    }

    private HashMap<String, Object> buildContactMap(Contact contact) {
        HashMap<String, Object> contactMap = new HashMap<>();
        contactMap.put("ContactID", XERO_CONTACT_ID);
        contactMap.put("Name", contact.getName());
        contactMap.put("EmailAddress", contact.getEmailAddress());


        return contactMap;
    }



    public void createJounalsTransaction(XeroManualJournalUploadRequest journals) {
        String accessToken = oAuth2Service.getAccessToken();
        LOGGER.info("Access Token retrieved from genericService." +accessToken);
        LOGGER.info("Access Token retrieved for uploading journals.");
        String url = XERO_JOURNAL_ENDPOINT;

        try {
            HashMap<String, Object> journalMap = new HashMap<>();
            journalMap.put("Date", journals.getDate());
            journalMap.put("Status", journals.getStatus());
            journalMap.put("Narration", journals.getNarration());
            journalMap.put("LineAmountTypes", journals.getLineAmountTypes());
            journalMap.put("ShowOnCashBasisReports", "false");

            List<HashMap<String, Object>> journalLines = new ArrayList<>();
            for (int i = 0; i < journals.getJournalLines().size(); i++) {

                HashMap<String, Object> line1 = new HashMap<>();
                line1.put("Description", journals.getJournalLines().get(i).getDescription());
                line1.put("LineAmount", -journals.getJournalLines().get(i).getLineAmount());
                line1.put("AccountCode", journals.getJournalLines().get(i).getAccountCode());
                line1.put("TaxType", journals.getJournalLines().get(i).getTaxType());


                List<HashMap<String, String>> tracking1 = new ArrayList<>();
                if (!journals.getJournalLines().get(i).getTracking().isEmpty()) {
                    HashMap<String, String> trackingCategory1 = new HashMap<>();
                    trackingCategory1.put("Name", journals.getJournalLines().get(i).getTracking().get(0).getName()); // Change index to 0
                    trackingCategory1.put("Option", journals.getJournalLines().get(i).getTracking().get(0).getOption()); // Change index to 0
                    tracking1.add(trackingCategory1);
                }
                line1.put("Tracking", tracking1);

                journalLines.add(line1);
            }
            journalMap.put("JournalLines", journalLines);

            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("Content-Type", "application/json");
            LOGGER.error("Upload: {}", journalMap);

            // Create the request entity
            HttpEntity<HashMap<String, Object>> requestEntity = new HttpEntity<>(journalMap, headers);

            // Send the request
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                LOGGER.info("Manual Journal created successfully: {}", responseEntity.getBody());
            } else {
                LOGGER.warn("Unexpected response status: {}", responseEntity.getStatusCode());
                // Handle DRAFT or other statuses if necessary
            }
        } catch (HttpClientErrorException e) {
            LOGGER.error("HTTP Error: {}, Response Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to create manual journal", e);
        }
    }

    @Override
    public ResponseEntity<BankTransactionResponse> getTransactionFromXero(String transactionId, String contactId) {
        String url = XERO_BANK_TRANSACTION_ENDPOINT;
        String accessToken = oAuth2Service.getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/json");

        HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
            LOGGER.info("Retrieved Bank transaction successfully: {}", responseEntity.getBody());

            ObjectMapper objectMapper = new ObjectMapper();
            BankTransactionResponse bankTransactionResponse = objectMapper.readValue(responseEntity.getBody(), BankTransactionResponse.class);

            return new ResponseEntity<>(bankTransactionResponse, HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            LOGGER.error("Error Getting bank transaction: {}, Response Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Failed to get bank transaction", e);
        } catch (IOException e) {
            LOGGER.error("Error parsing bank transaction response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse bank transaction response", e);
        }
    }




        @Override
    public void setXeroApiCreateAccount(CustomerDetails registrationData) {
        String accessToken = oAuth2Service.getAccessToken();
        LOGGER.info("Access Token retrieved for creating account.");

        Contacts contacts = buildContact(registrationData);

        try {
            createContact(accessToken,contacts);
            LOGGER.info("Contact created with ID: {}");
        } catch (XeroApiException  e) {
            logXeroApiException(e, "creating contacts in Xero");
        }
    }





    @Override
    public void setXeroApiTransaction(Transaction transactionData) {
        String accessToken = oAuth2Service.getAccessToken();

        BankTransaction bankTransaction = new BankTransaction();
        bankTransaction.setType(BankTransaction.TypeEnum.SPEND);
        LineItem lineItem = new LineItem();
        lineItem.setDescription(transactionData.getTranNarration());
        lineItem.setUnitAmount(transactionData.getAmount());
        lineItem.setAccountCode("400");
        bankTransaction.addLineItemsItem(lineItem);
        bankTransaction.setContact(buildContactPayload(transactionData));

        Account account = new Account();
        account.setBankAccountNumber(transactionData.getAccountNo());
        account.setName(transactionData.getSenderName());
        account.setCode("100");
        account.setStatus(Account.StatusEnum.ACTIVE);
        account.setDescription(transactionData.getTranNarration());
        bankTransaction.setBankAccount(account);

        bankTransaction.setReference(transactionData.getPaymentReference());

        try {
            createBankTransaction(accessToken, bankTransaction);
            LOGGER.info("Transaction uploaded successfully.");
        } catch (XeroApiException e) {
            logXeroApiException(e, "Uploading bank transaction to Xero: " + e.getMessages());
        }
    }




    private Contact buildContactPayload(Transaction registrationData) {
        Contact contact = new Contact()
                .name(registrationData.getSenderName())
                .firstName(registrationData.getUserToken().getFirstName())
                .lastName(registrationData.getUserToken().getSurname())
                .emailAddress(registrationData.getEmail())
                .phones(Collections.singletonList(new Phone().phoneNumber(registrationData.getPhone())))
                .addresses(Collections.singletonList(buildAddress(registrationData)))
                .contactStatus(Contact.ContactStatusEnum.ACTIVE)
                .isSupplier(false)
                .isCustomer(true);
        Contacts contacts = new Contacts();
        contacts.setContacts(Collections.singletonList(contact));
        return contact;
    }
    private Contacts buildContact(CustomerDetails registrationData) {
        Contact contact = new Contact()
                .name(registrationData.getName())
                .firstName(registrationData.getFirstName())
                .lastName(registrationData.getSurname())
                .emailAddress(registrationData.getEmail())
                .phones(Collections.singletonList(new Phone().phoneNumber(registrationData.getPhoneNumber())))
                .addresses(Collections.singletonList(buildAddressAccount(registrationData)))
                .contactStatus(Contact.ContactStatusEnum.ACTIVE)
                .isSupplier(false)
                .isCustomer(true);

        Contacts contacts = new Contacts();
        contacts.setContacts(Collections.singletonList(contact));
        return contacts;
    }

    private Address buildAddress(Transaction registrationData) {
        return new Address()
                .addressLine1(registrationData.getUserToken().getUserLocation())
                .city(registrationData.getUserToken().getUserLocation())
                .region(registrationData.getUserToken().getUserLocation())
                .postalCode(DEFAULT_POSTAL_CODE)
                .country(NIGERIA_COUNTRY);
    }
    private Address buildAddressAccount(CustomerDetails registrationData) {
        return new Address()
                .addressLine1(registrationData.getAddress())
                .city(registrationData.getCity())
                .region(registrationData.getAddress())
                .postalCode(DEFAULT_POSTAL_CODE)
                .country(NIGERIA_COUNTRY);
    }

    @Override
    public void uploadToXero(WayaTransactionResponse wayaTransactionResponse) {
        LOGGER.info("Initiating upload to Xero for Waya transactions.");

        wayaTransactionResponse.getData().forEach(transactionData -> {
            XeroManualJournalUploadRequest manualJournals = getManualJournal(transactionData);
            createJounalsTransaction(manualJournals);
        });
    }

    @Override
    public void createXeroUploadPayload(TransactionData transactionData) {
        LOGGER.info("Preparing Xero payload for transaction: {}", transactionData.getSenderName());
        XeroManualJournalUploadRequest data=  getManualJournal(transactionData);
        createJounalsTransaction(data);
    }


    private XeroManualJournalUploadRequest getManualJournal(TransactionData transactionData) {
        List<JournalLines> journalLines = Arrays.asList(
                createJournalLine(transactionData, transactionData.getTranCategory() + " - Debit", transactionData.getTranAmount(),"fa437cfd-f005-4538-ae84-943857da5c8c","fc96efd9-b832-4b31-a93e-61f56158adad"),
                createJournalLine(transactionData, transactionData.getTranCategory() + " - Credit", -transactionData.getTranAmount(),"fa437cfd-f005-4538-ae84-943857da5c8c","fc96efd9-b832-4b31-a93e-61f56158adad")
        );


        XeroManualJournalUploadRequest data= new XeroManualJournalUploadRequest();
        data.setJournalLines(journalLines);
        data.setStatus(ManualJournal.StatusEnum.DRAFT.toString());
        data.setLineAmountTypes(LineAmountTypes.NOTAX.toString());
        data.setShowOnCashBasisReports("true");
                data.setDate(transactionData.getTranDate());
                data.setNarration(transactionData.getTranNarrate());
                return data;

    }

    private JournalLines createJournalLine(TransactionData data, String description, double amount, String trankCode, String trackOption) {
        return  JournalLines.builder()
                .tracking(Collections.singletonList(new Tracking("Region" ,"West",trankCode,trackOption)))
                .description(description)
                .taxType("NONE")
                .lineAmount((int) amount)
                .accountCode(data.getTranCrncyCode()).build();
    }



    private void logXeroApiException(Exception e, String action) {
        LOGGER.error("Error occurred while {}: {}", action, e.getMessage());
        LOGGER.debug("Detailed error: {} ", e.getMessage());
    }
}
