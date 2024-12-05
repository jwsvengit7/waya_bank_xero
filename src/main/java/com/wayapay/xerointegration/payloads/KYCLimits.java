package com.wayapay.xerointegration.payloads;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class KYCLimits {

    private String profileId;
    private long userId;
    private String tierName;
    private int orderLevel;
    private BigDecimal singleTransactionLimit;
    private BigDecimal dailyTransactionLimit;
    private BigDecimal oneTimeTransactionLimit;
    private int dailyTransactionLimitCount;
}
