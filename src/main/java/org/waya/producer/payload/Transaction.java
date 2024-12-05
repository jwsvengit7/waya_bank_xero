package org.waya.producer.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
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
            private String[] roles;
            private String[] permits;
            private String transactionLimit;
            private String userLocation;
        }

    public static Transaction createDummyTransaction() {
        // Create dummy UserToken data
        UserToken userToken = new UserToken(
                1111,
                "mailto:xxxxxxxx@gmail.com",
                "2348160004000",
                "g4mwBY17iCVxxxx",
                "Stephenc",
                "Akintodsla",
                true,
                true,
                true,
                false,
                false,
                new String[] {"ROLE_USER_OWNER"},
                new String[] {
                        "CAN_VIEW_OR_MANAGE_MY_TRANSACTION_REPORTS_AND_STATEMENTS",
                        "CAN_VIEW_OR_MANAGE_MY_ACCOUNT_AND_PROFILE",
                        "CAN_VIEW_OR_MANAGE_MY_RECONCILIATION",
                        "CAN_VIEW_OR_MANAGE_MY_TECH_CONFIGURATIONS",
                        "CAN_VIEW_OR_MANAGE_MY_DISPUTE_AND_FEEDBACK",
                        "CAN_VIEW_OR_MANAGE_MY_WAYAGRAM_COMMUNICATIONS",
                        "CAN_VIEW_OR_MANAGE_MY_CONTENTS",
                        "CAN_VIEW_OR_MANAGE_MY_ACCOUNT_CREATION"
                },
                "10000.00",
                "Lagos"
        );

        return getTransaction(userToken);

    }

    private static Transaction getTransaction(UserToken userToken) {
        Transaction transaction = new Transaction();
        transaction.setCurrentBalance(51.97);
        transaction.setUserId(111111);
        transaction.setCustName("STEPHEN AKINTOLAS");
        transaction.setAccountNo("00000540000");
        transaction.setEmail("mailto:xxxxxxxx@gmail.com");
        transaction.setPhone("2348160000000");
        transaction.setTransactionReferenceNumber("3Qpxxxxx");
        transaction.setAmount(31.75);
        transaction.setTranPart(1);
        transaction.setTranId("WAYA75839");
        transaction.setTranType("TRANSFER");
        transaction.setTranNarration("NIP_PAYOUTtxn");
        transaction.setPaymentReference("3Qpxxxx");
        transaction.setTransactionCategory("WITHDRAW");
        transaction.setSenderName("Stephen Akintola2");
        transaction.setReceiverName("xxxxx SUNDAY NWACHINAMERE");
        transaction.setUserToken(userToken);
        transaction.setSessionID(111111);
        transaction.setCommissionFee(0);
        transaction.setTransactionChannel("OTHER_CHANNELS");
        transaction.setPartTranType("DR");
        return transaction;
    }


}


