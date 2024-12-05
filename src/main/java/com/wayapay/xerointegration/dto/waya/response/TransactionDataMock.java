package com.wayapay.xerointegration.dto.waya.response;

import lombok.Data;

@Data
public class TransactionDataMock {

    public static TransactionData createMockTransactionData() {
        return TransactionData.builder()
                .version(1)
                .id(123)
                .del_flg(false)
                .posted_flg(true)
                .tranId("tran_001")
                .acctNum("123456789")
                .tranAmount(1500.00)
                .tranType("Debit")
                .partTranType("Part")
                .tranNarrate("Payment")
                .tranDate("2024-10-07")
                .tranCrncyCode("USD")
                .paymentReference("PAY-001")
                .tranGL("GL-123")
                .tranPart(1)
                .relatedTransId(0)
                .createdAt("2024-10-01T10:00:00Z")
                .updatedAt("2024-10-01T10:00:00Z")
                .tranCategory("Services")
                .createdBy("user@example.com")
                .createdEmail("user@example.com")
                .senderName("John Doe")
                .receiverName("Jane Smith")
                .transChannel("Online")
                .channel_flg(true)
                .build();
    }
}
