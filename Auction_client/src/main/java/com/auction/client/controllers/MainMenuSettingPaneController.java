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
  // Controller Class cho màn hình chức năng cài đặt
  // Đường dẫn đến view của controller này
  private static final String PATH_TO_VIEW = "/com/auction/client/views/mainMenu_settingsPane.fxml";

  public static String getPATH_TO_VIEW() {
    return PATH_TO_VIEW;
  }

  @FXML ChoiceBox<String> currencyUnitSettingBox;
  @FXML ChoiceBox<String> themeSettingBox;

  /** Usage: Tự chạy khi controller được gọi */
  public void initialize() {
    // Danh sách loại đơn vị tiền tệ
    ObservableList<String> currencyUnitSetting = FXCollections.observableArrayList("VND", "USD");
    // Converter để choicebox hiển thị tên dài từ tên đơn vị trong dữ liệu
    currencyUnitSettingBox.setConverter(
        new StringConverter<String>() {
          @Override
          public String toString(String shortCurrencyName) {
            return switch (shortCurrencyName) {
              case "VND" -> "Vietnamese Dong (VND)";
              case "USD" -> "US Dollar (USD)";
              default -> shortCurrencyName;
            };
          }

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
    // Listener để nghe thay đổi từ lựa chọn trong choicebox
    currencyUnitSettingBox
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observableValue, oldValue, newValue) -> {
              CurrencySelectorHandler.getInstance().setActiveCurrency(newValue);
            });

    // Cài đặt màu hình nền
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
