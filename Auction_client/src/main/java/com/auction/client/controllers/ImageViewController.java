package com.auction.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageViewController {
  // Controller class cho phần hình ảnh phóng to khi nhấn vào hình ảnh
  // Đường dẫn đến view của controller này
  private static final String PATH_TO_VIEW = "/com/auction/client/views/imageView.fxml";

  public static String getPATH_TO_VIEW() {
    return PATH_TO_VIEW;
  }

  @FXML ImageView imageView;

  /**
   * Usage: Đặt hình ảnh
   *
   * @param image Hình ảnh
   */
  public void setImage(Image image) {
    imageView.setImage(image);
  }
}
