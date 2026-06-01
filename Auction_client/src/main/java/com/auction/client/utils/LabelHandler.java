package com.auction.client.utils;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

public class LabelHandler {
  /**
   * Usage: set tooltip for text. Useful for long texts which gets partly cut off
   * by javafx.
   *
   * @param label
   */
  public static void setDetailedTooltip(Label label) {
    label.setTooltip(new Tooltip(label.getText()));
  }

  /**
   * Usage: decrease label font size in case text in label is too large to fit
   *
   * @param label
   * @param baseSize
   * @param minSize
   * @param thresholdChars max threshold of characters before decreasing font size
   * @param decreaseRate   rate of font size decline per char
   */
  public static void scaleFontSizeToFit(
      Label label, double baseSize, double minSize, int thresholdChars, double decreaseRate) {
    ChangeListener<String> labelTextListener = (observable, oldText, newText) -> {
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
