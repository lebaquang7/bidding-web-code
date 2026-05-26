package com.auction.client.Controllers;

import com.auction.client.Models.ConfigFileHandler;
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
        mainMenuSettingPaneCurrencyUnitSettingBox.setValue(ConfigFileHandler.getProperty("currencyType", "VND"));
        //listener for choice box
        mainMenuSettingPaneCurrencyUnitSettingBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            CurrencySelectorHandler.getInstance().setActiveCurrency(newValue);
        });
        


        //Custom theme handling
        ObservableList<String> themeSetting = FXCollections.observableArrayList("Default", "Dark", "Modern Blue", "Mint");
        mainMenuSettingPaneThemeSettingBox.setItems(themeSetting);
        mainMenuSettingPaneThemeSettingBox.setValue(ConfigFileHandler.getProperty("theme", "Default"));
        mainMenuSettingPaneThemeSettingBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            ThemeHandler.getInstance().setTheme(newValue);
        });
    }

    /**
     * Used to load active configs for currency, theme, etc (if implemented) on startup
     */
    public static void loadStoredConfigs(){
        CurrencySelectorHandler.getInstance().setActiveCurrency(ConfigFileHandler.getProperty("currencyType", "VND"));
        ThemeHandler.getInstance().setTheme(ConfigFileHandler.getProperty("theme", "Default"));
    }
}
