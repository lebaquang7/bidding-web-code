package com.auction.client.utils;

import javafx.scene.control.Alert;

public class AlertMessageHandler {
  // Class xử lý các popup hiển thị cảnh báo (lỗi/thông tin)

  /**
   * Usage: Hiển thị thông báo lỗi
   *
   * @param title Tên đề thông báo lỗi
   * @param headerText Header thông báo lỗi
   * @param content Nội dung thông báo lỗi
   */
  public static void showError(String title, String headerText, String content) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(headerText);
    alert.setContentText(content);
    alert.showAndWait();
  }

  /**
   * Usage: Hiển thị thông báo thông tin
   *
   * @param title Tên đề thông báo
   * @param headerText Header thông báo
   * @param content Nội dung thông báo
   */
  public static void showInfo(String title, String headerText, String content) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(headerText);
    alert.setContentText(content);
    alert.showAndWait();
  }
}
