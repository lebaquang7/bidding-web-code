package com.auction.client.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

public class MainMenuSettingPaneController {
    @FXML ChoiceBox<String> mainMenuSettingPaneCurrencyUnitSettingBox;
    @FXML ChoiceBox<String> mainMenuSettingPaneThemeSettingBox;

    public void initialize(){
        //TODO: custom currency unit handling, custom theme handling
        ObservableList<String> currencyUnitSetting = FXCollections.observableArrayList("Vietnamese Dong (VND)", "US Dollar (US $)");
        ObservableList<String> themeSetting = FXCollections.observableArrayList("default", "TBD");
        mainMenuSettingPaneCurrencyUnitSettingBox.setItems(currencyUnitSetting);
        mainMenuSettingPaneCurrencyUnitSettingBox.setValue("Vietnamese Dong (VND)");
        mainMenuSettingPaneThemeSettingBox.setItems(themeSetting);
        mainMenuSettingPaneThemeSettingBox.setValue("default");
    }
}
