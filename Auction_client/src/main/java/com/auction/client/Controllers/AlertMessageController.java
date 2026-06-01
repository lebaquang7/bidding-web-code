package com.auction.client.controllers;

import javafx.scene.control.Alert;

public class AlertMessageController {
  // TODO: make sure all alerts use this controller.
  public static void showError(String title, String headerText, String content) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(headerText);
    alert.setContentText(content);
    alert.showAndWait();
  }

  public static void showInfo(String title, String headerText, String content) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(headerText);
    alert.setContentText(content);
    alert.showAndWait();
  }
}
