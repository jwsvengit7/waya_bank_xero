package com.wayapay.xerointegration.dto.xero.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JournalLines {
    @JsonProperty("Description")
    public String description;
    @JsonProperty("LineAmount")
    public int lineAmount;
    @JsonProperty("AccountCode")
    public String accountCode;
    @JsonProperty("TaxType")
    public String taxType;
    @JsonProperty("Tracking")
    public List<Tracking> tracking;
}
