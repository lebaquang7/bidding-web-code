package com.auction.client.controllers;

import com.auction.client.utils.ConfigFileHandler;
import com.auction.client.utils.CurrencySelectorHandler;
import com.auction.client.utils.ThemeHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;

public class MainMenuSettingPaneController {
  @FXML ChoiceBox<String> currencyUnitSettingBox;
  @FXML ChoiceBox<String> themeSettingBox;

  public void initialize() {
    // Currency unit setting handling
    ObservableList<String> currencyUnitSetting = FXCollections.observableArrayList("VND", "USD");
    // Converter so that the choicebox displays long name from internal name
    currencyUnitSettingBox.setConverter(
        new StringConverter<String>() {
          // Display long name from short internal name
          @Override
          public String toString(String shortCurrencyName) {
            return switch (shortCurrencyName) {
              case "VND" -> "Vietnamese Dong (VND)";
              case "USD" -> "US Dollar (USD)";
              default -> shortCurrencyName;
            };
          }

          // Extract short name from long name
          @Override
          public String fromString(String longCurrencyName) {
            if (longCurrencyName == null
                || longCurrencyName.contains("(")
                || longCurrencyName.contains(")")) {
              return longCurrencyName;
            } else {
              return longCurrencyName.substring(
                  longCurrencyName.indexOf("(") + 1, longCurrencyName.indexOf(")"));
            }
          }
        });
    currencyUnitSettingBox.setItems(currencyUnitSetting);
    currencyUnitSettingBox.setValue(ConfigFileHandler.getProperty("currencyType", "VND"));
    // listener for choice box
    currencyUnitSettingBox
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              CurrencySelectorHandler.getInstance().setActiveCurrency(newValue);
            });

    // Custom theme handling
    ObservableList<String> themeSetting =
        FXCollections.observableArrayList("Default", "Dark", "Modern Blue", "Mint");
    themeSettingBox.setItems(themeSetting);
    themeSettingBox.setValue(ConfigFileHandler.getProperty("theme", "Default"));
    themeSettingBox
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              ThemeHandler.getInstance().setTheme(newValue);
            });
  }
}
