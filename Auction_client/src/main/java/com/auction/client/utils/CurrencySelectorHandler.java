package com.auction.client.utils;

import com.auction.client.Properties;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

public class CurrencySelectorHandler {
  // Singleton class xử lý thông tin liên quan đến đơn vị tiền tệ
  private static CurrencySelectorHandler instance = null;

  private CurrencySelectorHandler() {}
  ;

  public static synchronized CurrencySelectorHandler getInstance() {
    if (instance == null) {
      instance = new CurrencySelectorHandler();
    }
    return instance;
  }

  // Dùng SimpleObjectProperty để có thể observe trạng thái của active currency
  private final SimpleObjectProperty<String> activeCurrency = new SimpleObjectProperty<>("VND");

  // Getter setters
  public SimpleObjectProperty<String> getActiveCurrencyObjectProperty() {
    return activeCurrency;
  }

  public String getActiveCurrency() {
    return activeCurrency.get();
  }

  public void setActiveCurrency(String newCurrency) {
    ConfigFileHandler.setProperty("currencyType", newCurrency);
    activeCurrency.set(newCurrency);
  }

  /**
   * Usage: Chuyển giá từ VND sang đơn vị tiền tệ của giá tiền đang được chọn
   *
   * @param price Giá ở VND
   * @return Giá ở đơn vị được chọn
   */
  public BigDecimal getConvertedPrice(BigDecimal price) {
    BigDecimal convertedPrice;
    switch (CurrencySelectorHandler.getInstance().getActiveCurrency()) {
      case "VND" -> convertedPrice = price;
      case "USD" ->
          convertedPrice =
              price.divide(
                  Properties.getUSD_TO_VND_RATE(),
                  2, // 2 chữ số thập phân
                  RoundingMode.HALF_UP);
      default -> convertedPrice = price;
    }
    return convertedPrice;
  }

  /**
   * Usage: Chuyển giá từ đơn vị được chọn sang tiền VND
   *
   * @param price Giá thành ở đơn vị được chọn
   * @return Giá ở VND
   */
  public BigDecimal getVNDPrice(BigDecimal price) {
    BigDecimal convertedPrice;
    switch (CurrencySelectorHandler.getInstance().getActiveCurrency()) {
      case "VND" -> convertedPrice = price;
      case "USD" -> convertedPrice = price.multiply(Properties.getUSD_TO_VND_RATE());
      default -> convertedPrice = price;
    }
    return convertedPrice;
  }

  /**
   * Bind label với loại đơn vị tiền tệ nhằm tự cập nhật khi đơn vị thay đổi
   *
   * @param label
   * @param price Giá thành
   */
  public static void bindPriceLabel(Label label, BigDecimal price) {
    Tooltip tooltip = new Tooltip();
    label.setTooltip(tooltip);
    Runnable updateUI =
        () -> {
          String currencyUnit = CurrencySelectorHandler.getInstance().getActiveCurrency();
          BigDecimal convertedPrice =
              CurrencySelectorHandler.getInstance().getConvertedPrice(price);
          tooltip.setText(
              convertedPrice.toString() + " " + currencyUnit); // Tooltip để thấy tất cả giá
          label.setText(
              String.format(
                  "%s %s",
                  CurrencySelectorHandler.abbreviateCurrency(convertedPrice), currencyUnit));
          // ^ custom formatting, hiện giá với 2 chữ số thập phân, đơn vị tiền tệ
        };
    updateUI.run();
    // Chạy 1 lần khi chương trình bắt đầu
    CurrencySelectorHandler.getInstance()
        .getActiveCurrencyObjectProperty()
        .addListener((observable) -> Platform.runLater(updateUI));
    // Listener nếu cài đặt thay đổi
  }

  // Hằng số chuyển đổi đơn vị tiền tệ
  private static final BigDecimal TRILLION = new BigDecimal("1000000000000");
  private static final BigDecimal BILLION = new BigDecimal("1000000000");
  private static final BigDecimal MILLION = new BigDecimal("1000000");
  private static final BigDecimal THOUSAND = new BigDecimal("1000");

  /**
   * Usage: Format giá thành dài dưới dạng ngắn hơn
   *
   * @param price Giá thành
   * @return
   */
  public static String abbreviateCurrency(BigDecimal price) {
    if (price == null || price.compareTo(new BigDecimal("0")) < 0) {
      return "0.00";
    } else {
      DecimalFormat decimalFormatter = new DecimalFormat("#,##0.00");
      if (price.compareTo(TRILLION) >= 0) {
        return decimalFormatter.format(price.divide(TRILLION, 2, RoundingMode.HALF_UP)) + "T";
      } else if (price.compareTo(BILLION) >= 0) {
        return decimalFormatter.format(price.divide(BILLION, 2, RoundingMode.HALF_UP)) + "B";
      } else if (price.compareTo(MILLION) >= 0) {
        return decimalFormatter.format(price.divide(MILLION, 2, RoundingMode.HALF_UP)) + "M";
      } else if (price.compareTo(THOUSAND) >= 0) {
        return decimalFormatter.format(price.divide(THOUSAND, 2, RoundingMode.HALF_UP)) + "K";
      } else {
        return decimalFormatter.format(price);
      }
    }
  }
}
