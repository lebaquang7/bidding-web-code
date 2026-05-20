package com.auction.client.Models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class LabelFormatHandler {
    //constants for conversion
    private static final BigDecimal TRILLION = new BigDecimal("1000000000000");
    private static final BigDecimal BILLION = new BigDecimal("1000000000");
    private static final BigDecimal MILLION = new BigDecimal("1000000");
    private static final BigDecimal THOUSAND = new BigDecimal("1000");

    /**
     * Usage: format big price into small price. will need to add a tooltip to labels that uses this. (potential todo?)
     * @param price
     * @return
     */
    public static String abbreviateCurrency(BigDecimal price) {
        if (price==null || price.compareTo(new BigDecimal("0"))<0) { 
            return "0.00";
        } else {
            DecimalFormat decimalFormatter = new DecimalFormat("#,##0.00");
            if (price.compareTo(TRILLION)>=0){
                return decimalFormatter.format(price.divide(TRILLION, RoundingMode.HALF_UP)) + "T";
            } else if (price.compareTo(BILLION)>=0){
                return decimalFormatter.format(price.divide(BILLION, RoundingMode.HALF_UP)) + "B";
            } else if (price.compareTo(MILLION)>=0){
                return decimalFormatter.format(price.divide(MILLION, RoundingMode.HALF_UP)) + "M";
            } else if (price.compareTo(THOUSAND)>=0){
                return decimalFormatter.format(price.divide(THOUSAND, RoundingMode.HALF_UP)) + "K";
            } else {
                return decimalFormatter.format(price);
            }
        }
    }
}
