package com.auction.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageViewController {
  @FXML ImageView imageView;

  public void setImage(Image image) {
    imageView.setImage(image);
  }
}
