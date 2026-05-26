package com.auction.client.Models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import com.auction.client.Properties;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

public class CurrencySelectorHandler {
    //Singleton stuffs, since this only needs one instance
    private static CurrencySelectorHandler instance = null;
    private CurrencySelectorHandler(){};
    public static synchronized CurrencySelectorHandler getInstance(){
        if (instance == null){
            instance = new CurrencySelectorHandler();
        }
        return instance;
    }
    //SimpleObjectProperty wrap a class value so it can be observed
    private SimpleObjectProperty<String> activeCurrency = new SimpleObjectProperty<>("VND");
    public SimpleObjectProperty<String> getActiveCurrencyObjectProperty(){
        return activeCurrency;
    }
    public String getActiveCurrency(){
        return activeCurrency.get();
    }
    public void setActiveCurrency(String newCurrency){
        ConfigFileHandler.setProperty("currencyType", newCurrency);
        activeCurrency.set(newCurrency);
    }

    /**
     * Bind labels that displays price with currency type.
     * Bind still functions after method finishes
     * @param label
     * @param price
     */
    public static void bindPriceLabel(Label label, BigDecimal price) {
        Tooltip tooltip = new Tooltip();
        label.setTooltip(tooltip);
        Runnable updateUI = () -> {
            String currencyUnit = CurrencySelectorHandler.getInstance().getActiveCurrency();
            BigDecimal convertedPrice;
            switch (currencyUnit) {
                case "VND" -> convertedPrice = price;
                case "USD" -> convertedPrice = price.divide(Properties.getUSD_TO_VND_RATE(), RoundingMode.HALF_UP);
                default -> convertedPrice = price;
            }
            tooltip.setText(convertedPrice.toString()+" "+currencyUnit);
            label.setText(String.format("%s %s", CurrencySelectorHandler.abbreviateCurrency(convertedPrice), currencyUnit));
            //^ custom formatting, display first variable (price) with two decimals max (abbreviated, tooltip reveals full), then second variable (currency unit)
        };
        updateUI.run();
        //runs once upon start
        CurrencySelectorHandler.getInstance().getActiveCurrencyObjectProperty().addListener((observable) -> Platform.runLater(updateUI));;
        //Listens to active currency object property changing and runs updateUI
    }


    //constants for conversion
    private static final BigDecimal TRILLION = new BigDecimal("1000000000000");
    private static final BigDecimal BILLION = new BigDecimal("1000000000");
    private static final BigDecimal MILLION = new BigDecimal("1000000");
    private static final BigDecimal THOUSAND = new BigDecimal("1000");
    /**
     * Usage: format big price into abbreviated price strings
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
