package com.wayapay.xerointegration.zoho.service;


import com.wayapay.xerointegration.payloads.CustomerDetails;
import com.wayapay.xerointegration.zoho.utils.DateFormatterUtil;
import lombok.extern.slf4j.Slf4j;
import com.wayapay.xerointegration.payloads.*;
//import org.waya.producer.payload.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.wayapay.xerointegration.zoho.utils.DateFormatterUtil.formatDateOfBirth;

@Slf4j
public class ZohoEntityMapper {

    public static Map<String, Object> mapCustomerToZoho(CustomerDetails customerDetails) {
        Map<String, Object> customerData = new HashMap<>();

        customerData.put("Full_Name", customerDetails.getName());
//        customerData.put("First_Name", "WAYA CUSTOMER");
        customerData.put("Code", "WAYA CUSTOMER");

        customerData.put("Profile_Id", customerDetails.getProfileId());
        customerData.put("User_Id", String.valueOf(customerDetails.getUserId()));
        customerData.put("Name", "WAYA CUSTOMER");
        customerData.put("First_Name1", customerDetails.getFirstName());
        customerData.put("First_Name", customerDetails.getFirstName());
//        customerData.put("First_Name2", customerDetails.getFirstName());
//        customerData.put("First-Name", customerDetails.getFirstName());

        customerData.put("Surname", customerDetails.getSurname());
        customerData.put("Last_Name", customerDetails.getSurname());
        customerData.put("Email", customerDetails.getEmail());
        customerData.put("Phone_Number", customerDetails.getPhoneNumber());
        customerData.put("Reference_Code", customerDetails.getReferenceCode());
        customerData.put("Corporate_User", customerDetails.isCorporate());
        customerData.put("Registration_Device_Type", customerDetails.getRegDeviceType());
        customerData.put("Date_Of_Birth", formatDateOfBirth(String.valueOf(customerDetails.getDateOfBirth())));
        customerData.put("Gender", customerDetails.getGender());
        customerData.put("Address", customerDetails.getAddress());
        customerData.put("City", customerDetails.getCity());
        customerData.put("State", customerDetails.getState());




        customerData.put("Created_Date", DateFormatterUtil.formatCurrentDate());



        customerData.put("Registration_Device_IP", customerDetails.getRegDeviceIP());
        customerData.put("Organization_Name", customerDetails.getOrganisationName());
        customerData.put("Organization_Email", customerDetails.getOrganisationEmail());
        customerData.put("Organization_Phone", customerDetails.getOrganisationPhone());
        customerData.put("Organization_City", customerDetails.getOrganizationCity());
        customerData.put("Organization_Address", customerDetails.getOrganizationAddress());
        customerData.put("Organization_State", customerDetails.getOrganizationState());
        customerData.put("Organization_Type", customerDetails.getOrganisationType());
        customerData.put("Business_Type", customerDetails.getBusinessType());

        customerData.put("Profile_Type", customerDetails.getProfileType());
        customerData.put("Client_Id", customerDetails.getClientId());
        customerData.put("Client_Type", customerDetails.getClientType());

        return customerData;
    }

    public static Map<String, Object> mapTransactionToZoho(Transaction transaction) {
        Map<String, Object> transactionData = new HashMap<>();

        transactionData.put("Current_Balance", transaction.getCurrentBalance());
        transactionData.put("User_Id", String.valueOf(transaction.getUserId()));
        transactionData.put("Customer_Name", transaction.getCustName());
        transactionData.put("Name", transaction.getCustName());
        transactionData.put("Account_Number", transaction.getAccountNo());
        transactionData.put("Email", transaction.getEmail());
        transactionData.put("Phone", transaction.getPhone());
        transactionData.put("Transaction_Reference_No", transaction.getTransactionReferenceNumber());
        transactionData.put("Transaction_Amount", transaction.getAmount());
        transactionData.put("Transaction_Part", transaction.getTranPart());
        transactionData.put("Transaction_Id", transaction.getTranId());
        transactionData.put("Transaction_Type", transaction.getTranType());
        transactionData.put("Transaction_Narration", transaction.getTranNarration());
        transactionData.put("Payment_Reference", transaction.getPaymentReference());
        transactionData.put("Transaction_Category", transaction.getTransactionCategory());
        transactionData.put("Sender_Name", transaction.getSenderName());
        transactionData.put("Receiver_Name", transaction.getReceiverName());
        transactionData.put("Transaction_Channel", transaction.getTransactionChannel());
        transactionData.put("Part_Tran_Type", transaction.getPartTranType());
        transactionData.put("Session_Id", String.valueOf(transaction.getSessionID()));
        transactionData.put("Commission_Fee", transaction.getCommissionFee());

        if (transaction.getUserToken() != null) {
            Transaction.UserToken userToken = transaction.getUserToken();
            transactionData.put("User_Token_Id", String.valueOf(userToken.getId()));
            transactionData.put("User_Email", userToken.getEmail());
            transactionData.put("Phone_Number", userToken.getPhoneNumber());
            transactionData.put("Reference_Code", userToken.getReferenceCode());
            transactionData.put("First_Name", userToken.getFirstName());
            transactionData.put("Surname", userToken.getSurname());
            transactionData.put("Phone_Verified", userToken.isPhoneVerified());
            transactionData.put("Email_Verified", userToken.isEmailVerified());
            transactionData.put("Pin_Created", userToken.isPinCreated());
            transactionData.put("Corporate_User", userToken.isCorporate());
            transactionData.put("Admin", userToken.isAdmin());
            transactionData.put("Roles", userToken.getRoles());


            String permits = String.join(",", userToken.getPermits());


            if (permits.length() > 255) {
                permits = permits.substring(0, 255);
            }


            transactionData.put("Permits", permits);
            transactionData.put("Transaction_Limit", userToken.getTransactionLimit());
            transactionData.put("User_Location", userToken.getUserLocation());
        }

        return transactionData;
    }

    public static Map<String, Object> mapKYCLimitsToZoho(KYCLimits kycLimits) {
        Map<String, Object> kycLimitsData = new HashMap<>();

        kycLimitsData.put("id", kycLimits.getProfileId());
        kycLimitsData.put("User_Id", String.valueOf(kycLimits.getUserId()));
        kycLimitsData.put("Tier_Name", kycLimits.getTierName());
        kycLimitsData.put("Name", "KYC_LIMIT");
        kycLimitsData.put("Order_Level", kycLimits.getOrderLevel());
        kycLimitsData.put("Single_Transaction_Limit", kycLimits.getSingleTransactionLimit());
        kycLimitsData.put("Daily_Transaction_Limit", kycLimits.getDailyTransactionLimit());
        kycLimitsData.put("One_Time_Transaction_Limit", kycLimits.getOneTimeTransactionLimit());
        kycLimitsData.put("Daily_Transaction_Limit_Count", kycLimits.getDailyTransactionLimitCount());

        return kycLimitsData;
    }



}