package com.wayapay.xerointegration.zoho.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Slf4j
public class DateFormatterUtil {

    // Date-only format (example: "yyyy-MM-dd")
    private static final String ZOHO_DATE_FORMAT = "yyyy-MM-dd";
    private static final SimpleDateFormat zohoDateFormat = new SimpleDateFormat(ZOHO_DATE_FORMAT, Locale.ENGLISH);

    // Method to format current date in Zoho accepted format
    public static String formatCurrentDate() {
        return formatDate(new Date());
    }

    // Method to format a given date in Zoho accepted format
    public static String formatDate(Date date) {
        return zohoDateFormat.format(date);
    }

    // Method to format a given date string in a specific format to Zoho accepted format
    public static String formatDateString(String dateStr, String currentFormat) throws Exception {
        SimpleDateFormat currentDateFormat = new SimpleDateFormat(currentFormat, Locale.ENGLISH);
        Date date = currentDateFormat.parse(dateStr);
        return formatDate(date);
    }



    public static String formatDateOfBirth(String dateOfBirth) {
        if (dateOfBirth == null || dateOfBirth.isEmpty()) {
            return null;
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = inputFormat.parse(dateOfBirth);
            return outputFormat.format(date);
        } catch (ParseException e) {
            log.error("Error parsing date of birth: {}", dateOfBirth, e);
            return null;
        }
    }
}