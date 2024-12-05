package com.wayapay.xerointegration.constant;

import lombok.*;

@AllArgsConstructor
@Getter
public enum AccountType {

        BANK_VAULT("0", "AF9283B3-6108-4252-9DD7-2FC3E5813BCA"),
        CASH_ACCOUNT("100", "AF9283B3-6108-4252-9DD7-2FC3E5813BCA"),
        INTER_BANK_TRANSFER("101", "Inter-Bank Transfer"),
        INTER_COMPANY_TRANSFER("102", "Inter-Company Transfer"),
        LICENSE_FEES_REGULATION("103", "License Fees/Regulation"),
        INTEREST_RECEIVABLE("104", "Interest Receivable"),
        DISBURSEMENT_ACCOUNT("105", "Disbursement Account (Cash Account)"),
        LOANS_DISBURSEMENT("106", "Loans Disbursement"),
        LOANS_REPAYMENT("107", "Loans Repayment"),
        BILLS_PAYMENT_ACCOUNT("108", "Bills Payment Account"),
        ACCOUNTS_RECEIVABLE("109", "Accounts Receivable"),
        FIXED_DEPOSITS("201", "Fixed Deposits"),
        FIXED_DEPOSITS_LIQUIDATION("202", "Fixed Deposits - Liquidation"),
        LOAN_PAYABLE("203", "Loan Payable"),
        INCOME_TAX_PAYABLE("204", "Income Tax Payable"),
        EMPLOYEE_TAX_PAYABLE("205", "Employee Tax Payable"),
        ACCOUNTS_PAYABLE("206", "Accounts Payable"),
        STAMP_DUTY("207", "Stamp Duty"),
        DEFERRED_INCOME("208", "Deferred Income"),
        SAVINGS_ACCOUNT("209", "Savings Account"),
        BUSINESS_ACCOUNT("210", "Business Account"),
        SETTLEMENT_ACCOUNT("211", "Settlement Account"),
        PAYMENT_CLEARING_ACCOUNT("212", "Payment Clearing Account"),
        DONATION_COLLECTION_ACCOUNT("213", "Donation Collection Account"),
        POLLING_COLLECTION_ACCOUNT("214", "Polling Collection Account"),
        EVENTS_COLLECTION_ACCOUNT("215", "Events Collection Account"),
        WITHHOLDING_TAX_PAYABLE("216", "Withholding Tax Payable"),
        SUSPENSE("217", "Suspense"),
        INTEREST_INCOME("301", "Interest Income"),
        INVESTMENT_INCOME("302", "Investment Income"),
        CHARGES_INCOME("303", "Charges Income"),
        CREDIT_DEBIT_CARDS("304", "Credit/Debit Cards"),
        CARD_MAINTENANCE_INCOME("305", "Card Maintenance Income"),
        POS_SALES("306", "POS Sales"),
        POS_CHARGES("307", "POS Charges"),
        POF_CHARGES("308", "POF Charges"),
        BILLS_PAYMENT_CHARGE("309", "Bills Payment Charge"),
        PENALTY_CHARGE_INCOME("310", "Penalty Charge Income"),
        BANK_GUARANTEE_INCOME("311", "Bank Guarantee Income"),
        CURRENCY_CONVERSION_INCOME("312", "Currency Conversion Income"),
        BANKS_INTEREST_INCOME("313", "Banks Interest Income"),
        TRANSACTION_FEES("314", "Transaction Fees"),
        SMS_CHARGES("315", "SMS Charges"),
        OVERDRAFT_INTEREST_INCOME("316", "Overdraft Interest Income"),
        GRANT_UNRESTRICTED_INCOME("317", "Grant-Unrestricted Income"),
        INSURANCE_CLAIM("318", "Insurance Claim"),
        MANAGEMENT_FEES("319", "Management Fees"),
        COMMISSION_RECEIVED("320", "Commission Received"),
        WITHDRAWAL_FEES("321", "Withdrawal Fees"),
        ADVERTISING_PROMOTION_FEE("322", "Advertising (Promotion) Fee"),
        DONATION_FEE("323", "Donation Fee (Income)"),
        POLLING_FEE("324", "Polling Fee (Income)"),
        EVENTS_FEE("325", "Events Fee (Income)"),
        PRODUCTS_FEE("326", "Products Fee (Income)"),
        OTHER_INCOME("327", "Other Income"),
        COST_OF_SALES("401", "Cost of Sales"),
        TELEPHONE_EXPENSE("501", "Telephone Expense"),
        SALES_COMMISSION("502", "Sales Commission"),
        LEGAL_COST("503", "Legal Cost"),
        TECH_EXPENSE("504", "Tech Expense"),
        SECURITY_EXPENSE("505", "Security Expense"),
        INTEREST_EXPENSE("506", "Interest Expense"),
        WELFARE("507", "Welfare"),
        ADMIN_GENERAL_EXPENSE("508", "Admin & General Expense"),
        REPAIRS_MAINTENANCE("509", "Repairs and Maintenance"),
        TRANSPORT_EXPENSE("510", "Transport Expense"),
        GOVERNMENT_LEVIES("511", "Government Levies"),
        LOGISTICS("512", "Logistics"),
        POWER("513", "Power"),
        RENT("514", "Rent"),
        IT_INTERNET("515", "IT & Internet"),
        DIRECTORS_EXPENSES("516", "Director's Expenses"),
        TRAVEL_EXPENSE("517", "Travel Expense"),
        DEPRECIATION("518", "Depreciation"),
        ADVERTISEMENT_PROMO("519", "Advertisement & Promo"),
        SALARIES_WAGES("520", "Salaries & Wages"),
        BENEFITS_BONUSES("521", "Benefits & Bonuses"),
        INTEREST_ON_FIXED_DEPOSITS("522", "Interest on Fixed Deposits"),
        PROVISION_FOR_BAD_DEBT("523", "Provision for Bad Debt"),
        BUSINESS_DEVELOPMENT("524", "Business Development"),
        MEDICAL_EXPENSES("525", "Medical Expenses (HMO)"),
        FX_LOSS("526", "FX Loss"),
        REFUNDS("527", "Refunds"),
        PRODUCT_DEVELOPMENT_EXPENSE("528", "Product Development Expense"),
        COMMISSION_EXPENSE("529", "Commission Expense"),
        DIVIDEND_PAYMENT("530", "Dividend Payment"),
        INSURANCE("531", "Insurance"),
        PAYE("532", "PAYE"),
        PENSION("533", "Pension"),
        INCOME_TAX("534", "Income Tax"),
        VAT("535", "VAT"),
        PROFESSIONAL_FEES("536", "Professional Fees"),
        RECOVERY_EXPENSE("537", "Recovery Expense"),
        STAFF_TRAINING("538", "Staff Training"),
        FUEL_EXPENSE("539", "Fuel Expense"),
        BANK_CHARGES("540", "Bank Charges"),
        PROCESSING_FEES("541", "Processing Fees"),
        CHARGEBACK_EXPENSE("542", "Chargeback Expense"),
        POSTAGE_COURIER("543", "Postage And Courier"),
        STATIONERY_SUPPLIES("544", "Stationery And Supplies"),
        REGULATORY_EXPENSES("545", "Regulatory Expenses"),
        OTHER_EXPENSE("546", "Other Expense"),
        SHARE_CAPITAL("601", "Share Capital"),
        EQUITY("602", "Equity"),
        RETAINED_EARNINGS("603", "Retained Earnings"),
        ACCOUNTS_RECEIVABLE6("610", "Accounts Receivable"),
        FIXED_ASSET("700", "Fixed Asset"),
        OFFICE_EQUIPMENTS("701", "Office Equipments"),
        COMPUTER_LAPTOPS("702", "Computer/Laptops"),
        ACCUMULATED_DEPRECIATION("703", "Accumulated Depreciation"),
        FURNITURE_FITTINGS("704", "Furniture & Fittings"),
        MOTOR_VEHICLES("705", "Motor Vehicles"),
        INTANGIBLE_ASSETS("706", "Intangible Assets"),
        ACCOUNTS_PAYABLE8("800", "Accounts Payable"),
        UNPAID_EXPENSE_CLAIMS("801", "Unpaid Expense Claims"),
        WAGES_PAYABLE("803", "Wages Payable"),
        SALES_TAX("820", "Sales Tax"),
        HISTORICAL_ADJUSTMENT("840", "Historical Adjustment"),
        ROUNDING("860", "Rounding"),
        TRACKING_TRANSFERS("877", "Tracking Transfers");


    private final String code;
    private final String accountCode;





    public static AccountType fromCode(String code) {
        for (AccountType type : AccountType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No AccountType found for Code: " + code);
    }
    public static AccountType fromAccountCode(String code) {
        for (AccountType type : AccountType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No AccountType found for Account CODE: " + code);
    }
}
