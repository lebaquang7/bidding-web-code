package com.auction.client.Models;

import com.auction.client.Properties;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;

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
    public static void bindPriceLabel(Label label, double price) {
        label.textProperty().bind(Bindings.createStringBinding(()->{
            String currencyUnit = CurrencySelectorHandler.getInstance().getActiveCurrency();
            double convertedPrice;
            switch (currencyUnit) {
                case "VND" -> convertedPrice = price;
                case "USD" -> convertedPrice = price / Properties.getUSD_TO_VND_RATE();
                default -> convertedPrice = price;
            }
            return String.format("%.2f %s", convertedPrice, currencyUnit);
            //^ custom formatting, display first variable (price) with two decimals max, then second variable (currency unit)
        }, CurrencySelectorHandler.getInstance().getActiveCurrencyObjectProperty())); //Dependency on active currency object property changing
    }
}
