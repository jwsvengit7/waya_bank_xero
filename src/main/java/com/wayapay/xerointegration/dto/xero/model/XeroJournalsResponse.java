package com.wayapay.xerointegration.dto.xero.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class XeroJournalsResponse {
    private double amount;
    private String creditAccountCode;
    private String description;
    private String narration;
}
