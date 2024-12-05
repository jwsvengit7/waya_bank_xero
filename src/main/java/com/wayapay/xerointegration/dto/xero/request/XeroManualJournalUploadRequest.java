package com.wayapay.xerointegration.dto.xero.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class XeroManualJournalUploadRequest {
    @JsonProperty("Date")
    public String date;
    @JsonProperty("Status")
    public String status;
    @JsonProperty("Narration")
    public String narration;
    @JsonProperty("LineAmountTypes")
    public String lineAmountTypes;
    @JsonProperty("JournalLines")
    public List<JournalLines> journalLines;
    @JsonProperty("ShowOnCashBasisReports")
    public String showOnCashBasisReports;
}
