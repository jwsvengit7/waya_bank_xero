package com.wayapay.xerointegration.payloads;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
@Builder
@Data
public class CustomerDetails {

    @JsonProperty("profileId")
    private String profileId;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("surname")
    private String surname;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("referenceCode")
    private String referenceCode;

    @JsonProperty("corporate")
    private boolean corporate;

    @JsonProperty("regDeviceType")
    private String regDeviceType;

    @JsonProperty("regDeviceIP")
    private String regDeviceIP;

    @JsonProperty("dateOfBirth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @JsonProperty("referral")
    private String referral;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("address")
    private String address;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("organisationName")
    private String organisationName;

    @JsonProperty("organisationEmail")
    private String organisationEmail;

    @JsonProperty("organisationPhone")
    private String organisationPhone;

    @JsonProperty("organizationCity")
    private String organizationCity;

    @JsonProperty("organizationAddress")
    private String organizationAddress;

    @JsonProperty("organizationState")
    private String organizationState;

    @JsonProperty("organisationType")
    private String organisationType;

    @JsonProperty("businessType")
    private String businessType;

    @JsonProperty("token")
    private String token;

    @JsonProperty("profileType")
    private String profileType;

    @JsonProperty("clientId")
    private String clientId;

    @JsonProperty("clientType")
    private String clientType;

    public static CustomerDetails createMockCustomerDetails() {
        return CustomerDetails.builder()
                .profileId("profile_001")
                .userId("user_001")
                .firstName("John")
                .surname("Doe")
                .name("John Doe")
                .email("john.doe@example.com")
                .phoneNumber("123-456-7890")
                .referenceCode("REF12345")
                .corporate(false)
                .regDeviceType("Mobile")
                .regDeviceIP("192.168.1.1")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .referral("ReferralSource")
                .gender("Male")
                .address("123 Main St")
                .city("Anytown")
                .state("CA")
                .organisationName("Example Corp")
                .organisationEmail("contact@example.com")
                .organisationPhone("098-765-4321")
                .organizationCity("Example City")
                .organizationAddress("456 Corporate Blvd")
                .organizationState("NY")
                .organisationType("LLC")
                .businessType("Retail")
                .token("token_abc123")
                .profileType("Individual")
                .clientId("client_001")
                .clientType("Regular")
                .build();
    }
}
