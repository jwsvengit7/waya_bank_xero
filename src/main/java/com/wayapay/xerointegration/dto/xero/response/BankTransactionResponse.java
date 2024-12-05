package com.wayapay.xerointegration.dto.xero.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BankTransactionResponse {

    @JsonProperty("Id")
    private String id;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("ProviderName")
    private String providerName;

    @JsonProperty("DateTimeUTC")
    private String dateTimeUTC;

    @JsonProperty("BankTransactions")
    private List<BankTransaction> bankTransactions;

    @Data
    public static class BankTransaction {

        @JsonProperty("BankTransactionID")
        private String bankTransactionID;

        @JsonProperty("BankAccount")
        private BankAccount bankAccount;

        @JsonProperty("Type")
        private String type;

        @JsonProperty("Reference")
        private String reference;

        @JsonProperty("IsReconciled")
        private boolean isReconciled;

        @JsonProperty("HasAttachments")
        private boolean hasAttachments;

        @JsonProperty("Contact")
        private Contact contact;

        @JsonProperty("DateString")
        private String dateString;

        @JsonProperty("Date")
        private String date; // Adjust type if you want to parse the date

        @JsonProperty("Status")
        private String status;

        @JsonProperty("LineAmountTypes")
        private String lineAmountTypes;

        @JsonProperty("LineItems")
        private List<Object> lineItems;

        @JsonProperty("SubTotal")
        private double subTotal;

        @JsonProperty("TotalTax")
        private double totalTax;

        @JsonProperty("Total")
        private double total;

        @JsonProperty("UpdatedDateUTC")
        private String updatedDateUTC;

        @JsonProperty("CurrencyCode")
        private String currencyCode;

        @Data
        public static class BankAccount {
            @JsonProperty("AccountID")
            private String accountID;

            @JsonProperty("Code")
            private String code;

            @JsonProperty("Name")
            private String name;
        }

        @Data
        public static class Contact {
            @JsonProperty("ContactID")
            private String contactID;

            @JsonProperty("Name")
            private String name;

            @JsonProperty("Addresses")
            private List<Object> addresses;

            @JsonProperty("Phones")
            private List<Object> phones;

            @JsonProperty("ContactGroups")
            private List<Object> contactGroups; // Replace Object with appropriate class if available

            @JsonProperty("ContactPersons")
            private List<Object> contactPersons; // Replace Object with appropriate class if available

            @JsonProperty("HasValidationErrors")
            private boolean hasValidationErrors;


        }
    }
}

