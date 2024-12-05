package com.wayapay.xerointegration.dto.xero.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class Tracking {
    public String Name;
    public String Option;
    public String TrackingCategoryID;
    public String TrackingOptionID;

    public Tracking(String name, String option) {
        Name = name;
        Option = option;
    }
}
