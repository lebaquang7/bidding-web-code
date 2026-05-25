package com.auction.client.Controllers;

import com.auction.client.Models.CurrencySelectorHandler;
import com.auction.client.Models.ThemeHandler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;

public class MainMenuSettingPaneController {
    @FXML ChoiceBox<String> mainMenuSettingPaneCurrencyUnitSettingBox;
    @FXML ChoiceBox<String> mainMenuSettingPaneThemeSettingBox;

    public void initialize(){
        //Currency unit setting handling
        ObservableList<String> currencyUnitSetting = FXCollections.observableArrayList("VND", "USD");
        //Converter so that the choicebox displays long name from internal name
        mainMenuSettingPaneCurrencyUnitSettingBox.setConverter(new StringConverter<String>() {
            //Display long name from short internal name
            @Override
            public String toString(String shortCurrencyName){
                return switch (shortCurrencyName) {
                    case "VND" -> "Vietnamese Dong (VND)";
                    case "USD" -> "US Dollar (USD)";
                    default -> shortCurrencyName;
                };
            }

            //Extract short name from long name
            @Override 
            public String fromString(String longCurrencyName){
                if (longCurrencyName==null || longCurrencyName.contains("(") || longCurrencyName.contains(")")){
                    return longCurrencyName;
                } else {
                    return longCurrencyName.substring(longCurrencyName.indexOf("(")+1, longCurrencyName.indexOf(")"));
                }
            }
        });
        mainMenuSettingPaneCurrencyUnitSettingBox.setItems(currencyUnitSetting);
        mainMenuSettingPaneCurrencyUnitSettingBox.setValue(CurrencySelectorHandler.getInstance().getActiveCurrency());
        //listener for choice box
        mainMenuSettingPaneCurrencyUnitSettingBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            CurrencySelectorHandler.getInstance().setActiveCurrency(newValue);
        });
        


        //Custom theme handling
        ObservableList<String> themeSetting = FXCollections.observableArrayList("Default", "Dark", "Modern Blue", "Mint");
        mainMenuSettingPaneThemeSettingBox.setItems(themeSetting);
        mainMenuSettingPaneThemeSettingBox.setValue(ThemeHandler.getInstance().getActiveTheme());
        mainMenuSettingPaneThemeSettingBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            ThemeHandler.getInstance().setTheme(newValue);
        });
    }
}
