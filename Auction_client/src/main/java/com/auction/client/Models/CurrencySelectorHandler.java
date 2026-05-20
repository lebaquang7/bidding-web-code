package com.auction.client.Models;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
            label.setText(String.format("%s %s", LabelFormatHandler.abbreviateCurrency(convertedPrice), currencyUnit));
            //^ custom formatting, display first variable (price) with two decimals max (abbreviated, tooltip reveals full), then second variable (currency unit)
        };
        updateUI.run();
        //runs once upon start
        CurrencySelectorHandler.getInstance().getActiveCurrencyObjectProperty().addListener((observable) -> Platform.runLater(updateUI));;
        //Listens to active currency object property changing and runs updateUI
    }
}
