package com.wayapay.xerointegration.controller;


import com.wayapay.xerointegration.dto.waya.request.WayaTransactionRequest;
import com.wayapay.xerointegration.dto.waya.response.WayaTransactionResponse;
import com.wayapay.xerointegration.dto.waya.response.XeroTransaction;
import com.wayapay.xerointegration.dto.xero.response.BankTransactionResponse;
import com.wayapay.xerointegration.payloads.CustomerDetails;
import com.wayapay.xerointegration.service.XeroIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.waya.producer.payload.Transaction;

import java.util.List;

@RestController
public class XeroIntegrationController {
    @Autowired
    private XeroIntegrationService xeroApiService;

    @PostMapping("/api/jounals")
    public WayaTransactionResponse handleCallback(@RequestBody WayaTransactionRequest request) {
        return xeroApiService.getTransactionFromWaya(request);
    }

    @PostMapping("/api/customer")
    public ResponseEntity<String> handleCustomer(@RequestBody CustomerDetails request) {
         xeroApiService.setXeroApiCreateAccount(request);
        return new ResponseEntity<>("Request sent to xero",HttpStatus.OK);
    }

    @PostMapping("/api/bank-transaction")
    public ResponseEntity<String> handlebankTransaction(@RequestBody Transaction request) {
        xeroApiService.setXeroApiTransaction(request);
        return new ResponseEntity<>("Request sent to xero",HttpStatus.OK);
    }
    @GetMapping("/api/get-transaction")
    public ResponseEntity<BankTransactionResponse> getTransaction(@RequestParam("xero-transaction-id")  String transactionId, @RequestParam("xero-contact-id") String contactId) {
       return  xeroApiService.getTransactionFromXero(transactionId,contactId);
 }
}
