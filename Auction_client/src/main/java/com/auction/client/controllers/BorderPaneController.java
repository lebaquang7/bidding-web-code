package com.auction.client.controllers;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class BorderPaneController {
  // Controller class xử lý thay đổi border pane trong màn hình chính
  private static BorderPane mainLayout;

  /**
   * Usage: Đặt màn hình chính
   *
   * @param layout
   */
  public static void setMainLayout(BorderPane layout) {
    mainLayout = layout;
  }

  /**
   * Usage: Thay đổi center của border pane thành pane cần thiết
   *
   * @param fxmlPath Đường dẫn đến panel cần thay đổi
   */
  public static void setCenter(String fxmlPath) {
    try {
      Parent pane = FXMLLoader.load(BorderPaneController.class.getResource(fxmlPath));
      mainLayout.setCenter(pane);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
