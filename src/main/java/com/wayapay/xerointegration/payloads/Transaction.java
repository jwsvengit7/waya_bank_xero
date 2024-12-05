package com.wayapay.xerointegration.payloads;

import lombok.Data;

import java.util.List;

@Data
public class Transaction {

        private double currentBalance; //Expected Revenue
        private long userId;
        private String custName; //Account_Name
        private String accountNo; //Account_Name(Account)
        private String email;
        private String phone; //Phone (Account)
        private String transactionReferenceNumber;
        private double amount; //Amount (Deals)
        private int tranPart;
        private String tranId;
        private String tranType;  //Type
        private String tranNarration;  //Deal Name
        private String paymentReference;
        private String transactionCategory; //Stage
        private String senderName;      //Sender Name
        private String receiverName;    //Description
        private UserToken userToken;
        private long sessionID;
        private double commissionFee; //Probability
        private String transactionChannel;
        private String partTranType;

        // Nested class for userToken
    @Data
        public static class UserToken {
            private long id;
            private String email;
            private String phoneNumber;
            private String referenceCode;
            private String firstName;
            private String surname;
            private boolean phoneVerified;
            private boolean emailVerified;
            private boolean pinCreated;
            private boolean corporate;
            private boolean admin;
            private List<String> roles;
            private List<String> permits;
            private String transactionLimit;
            private String userLocation;
        }
    }


