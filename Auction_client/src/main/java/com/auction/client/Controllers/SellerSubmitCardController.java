package com.auction.client.controllers;

import java.io.ByteArrayInputStream;

import com.auction.client.services.ItemsEventHandler;
import com.auction.client.utils.CurrencySelectorHandler;
import com.auction.client.utils.LabelHandler;
import com.auction.shared.models.Item;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SellerSubmitCardController {
  // mostly copied from auctioncard controller
  @FXML
  Label mainMenuSellerSubmitCardNameLabel;
  @FXML
  Label mainMenuSellerSubmitCardPriceLabel;
  @FXML
  ImageView mainMenuSellerSubmitCardImageView;

  // each auction card holds the current item
  private Item currentItem;

  public void setData(Item item) {
    currentItem = item;

    mainMenuSellerSubmitCardNameLabel.setText(item.getItemName());
    LabelHandler.setDetailedTooltip(mainMenuSellerSubmitCardNameLabel);

    CurrencySelectorHandler.bindPriceLabel(
        mainMenuSellerSubmitCardPriceLabel, item.getCurrentPrice());
    LabelHandler.scaleFontSizeToFit(mainMenuSellerSubmitCardPriceLabel, 20, 12, 10, 1);

    // Tải hình ảnh lên
    if (item.getImageBytes() != null) {
      mainMenuSellerSubmitCardImageView.setImage(
          new Image(new ByteArrayInputStream(item.getImageBytes())));
    } else if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
      new Thread(
          () -> {
            byte[] bytes = ItemsEventHandler.downloadItemImage(item.getImagePath());
            if (bytes != null) {
              item.setImageBytes(bytes);
              Platform.runLater(
                  () -> {
                    mainMenuSellerSubmitCardImageView.setImage(
                        new Image(new ByteArrayInputStream(bytes)));
                  });
            }
          })
          .start();
    }

    // Tự cập nhật giá
    item.currentPriceProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              Platform.runLater(
                  () -> {
                    if (newVal != null) {
                      CurrencySelectorHandler.bindPriceLabel(
                          mainMenuSellerSubmitCardPriceLabel, newVal);
                      LabelHandler.scaleFontSizeToFit(
                          mainMenuSellerSubmitCardPriceLabel, 20, 12, 10, 1);
                    }
                  });
            });
  }

  public void mainMenuSellerSubmitCardGoToItemDetails(ActionEvent event) {
    SceneController.switchToItemView(
        "/com/auction/client/views/itemDetails_view.fxml", event, currentItem);
  }

  // TODO: logic for recalling auction
  public void mainMenuSellerSubmitCardRecallAuction(ActionEvent event) {
  }
}
