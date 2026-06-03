package com.auction.client.utils;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

public class LabelHandler {
  // Class xử lý các hoạt động liên quan đến label thông tin
  /**
   * Usage: Đặt tooltip cho các label để hiện toàn bộ nội dung nếu label không đủ chứa
   *
   * @param label
   */
  public static void setDetailedTooltip(Label label) {
    label.setTooltip(new Tooltip(label.getText()));
  }

  /**
   * Usage: Giảm cỡ chữ của label nếu quá dài
   *
   * @param label
   * @param baseSize Cỡ chữ cơ bản
   * @param minSize Cỡ chữ tối thiểu
   * @param thresholdChars Số chữ tối đa trước khi giảm
   * @param decreaseRate Mức giảm cỡ font
   */
  public static void scaleFontSizeToFit(
      Label label, double baseSize, double minSize, int thresholdChars, double decreaseRate) {
    ChangeListener<String> labelTextListener =
        (observable, oldText, newText) -> {
          if (newText == null || newText.isEmpty()) {
            label.setStyle("-fx-font-size: " + baseSize + "px;");
          } else {
            double targetFontSize = baseSize;

            if (newText.length() > thresholdChars) {
              int extraChars = newText.length() - thresholdChars;
              double calculatedSize = baseSize - (extraChars * decreaseRate);
              targetFontSize = Math.max(minSize, calculatedSize);
            }

            label.setStyle("-fx-font-size: " + targetFontSize + "px;");
          }
        };

    label.textProperty().addListener(labelTextListener);

    labelTextListener.changed(label.textProperty(), null, label.getText());
  }
}
