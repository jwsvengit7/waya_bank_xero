package com.wayapay.xerointegration.service;


import com.wayapay.xerointegration.dto.waya.request.WayaTransactionRequest;
import com.wayapay.xerointegration.dto.waya.response.TransactionData;
import com.wayapay.xerointegration.dto.waya.response.WayaTransactionResponse;
import com.wayapay.xerointegration.dto.waya.response.XeroTransaction;
import com.wayapay.xerointegration.dto.xero.response.BankTransactionResponse;
import com.wayapay.xerointegration.payloads.CustomerDetails;
import org.springframework.http.ResponseEntity;
import org.waya.producer.payload.KYCLimits;
import org.waya.producer.payload.Transaction;

import java.util.List;

public interface XeroIntegrationService {
    WayaTransactionResponse getTransactionFromWaya(WayaTransactionRequest wayaTransactionRequest);
    void uploadToXero(WayaTransactionResponse wayaTransactionResponse);
    void setXeroApiCreateAccount(CustomerDetails registrationData);
    void createXeroUploadPayload(TransactionData data);
    void setXeroApiTransaction(Transaction transactionData);

    ResponseEntity<BankTransactionResponse> getTransactionFromXero(String transactionId, String contactId);
}
