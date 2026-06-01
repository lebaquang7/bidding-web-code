package com.auction.client.controllers;

import com.auction.client.services.ItemsEventHandler;
import com.auction.client.services.SceneHandler;
import com.auction.client.utils.CurrencySelectorHandler;
import com.auction.client.utils.LabelHandler;
import com.auction.shared.models.Item;
import java.io.ByteArrayInputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class AuctionCardController {
  @FXML Label nameLabel;
  @FXML Label priceLabel;
  @FXML ImageView imageView;

  // each auction card holds the current item
  private Item currentItem;

  public void setData(Item item) {
    currentItem = item;

    nameLabel.setText(item.getItemName());
    LabelHandler.setDetailedTooltip(nameLabel);

    CurrencySelectorHandler.bindPriceLabel(priceLabel, item.getCurrentPrice());
    LabelHandler.scaleFontSizeToFit(priceLabel, 20, 12, 10, 1);

    // Tải hình ảnh lên
    if (item.getImageBytes() != null) {
      imageView.setImage(new Image(new ByteArrayInputStream(item.getImageBytes())));
    } else if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
      new Thread(
              () -> {
                byte[] bytes = ItemsEventHandler.downloadItemImage(item.getImagePath());
                if (bytes != null) {
                  item.setImageBytes(bytes);
                  Platform.runLater(
                      () -> {
                        imageView.setImage(new Image(new ByteArrayInputStream(bytes)));
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
                      CurrencySelectorHandler.bindPriceLabel(priceLabel, newVal);
                      LabelHandler.scaleFontSizeToFit(priceLabel, 20, 12, 10, 1);
                    }
                  });
            });

    // TODO: initialize and listenner for updateColorByAuctionState method to update
    // auction status
    // in real time
  }

  @FXML
  public void goToItemDetails(ActionEvent event) {
    SceneHandler.switchToItemView(
        "/com/auction/client/views/itemDetails_view.fxml", event, currentItem);
  }

  @FXML
  public void goToAuction(ActionEvent event) {
    SceneHandler.switchToItemView(
        "/com/auction/client/views/auction_view.fxml", event, currentItem);
  }

  @FXML private Circle statusCircle;

  public void updateColorByAuctionState(String auctionState) {
    String targetColor;

    switch (auctionState.toUpperCase()) {
      case "RUNNING":
        targetColor = "#47ff66";
        break;
      case "FINISHED":
        targetColor = "#45cbf0";
        break;
      case "CANCELLED":
        targetColor = "#f53535";
        break;
      case "PAID":
        targetColor = "#db35f5";
        break;
      default:
        targetColor = "#7f8c8d";
    }
    statusCircle.setStyle("-fx-auction-status-color: " + targetColor + ";");
  }
}
