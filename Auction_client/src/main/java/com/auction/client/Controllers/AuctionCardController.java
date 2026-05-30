package com.auction.client.Controllers;

import java.io.ByteArrayInputStream;

import com.auction.client.Models.CurrencySelectorHandler;
import com.auction.client.Models.ItemsEventHandler;
import com.auction.client.Models.LabelHandler;
import com.auction.shared.models.Item;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class AuctionCardController {
  @FXML
  Label mainMenuAuctionCardNameLabel;
  @FXML
  Label mainMenuAuctionCardPriceLabel;
  @FXML
  ImageView mainMenuAuctionCardImageView;

  // each auction card holds the current item
  private Item currentItem;

  public void setData(Item item) {
    currentItem = item;

    mainMenuAuctionCardNameLabel.setText(item.getItemName());
    LabelHandler.setDetailedTooltip(mainMenuAuctionCardNameLabel);

    CurrencySelectorHandler.bindPriceLabel(mainMenuAuctionCardPriceLabel, item.getCurrentPrice());
    LabelHandler.scaleFontSizeToFit(mainMenuAuctionCardPriceLabel, 20, 12, 10, 1);

    // Tải hình ảnh lên
    if (item.getImageBytes() != null) {
      mainMenuAuctionCardImageView.setImage(
          new Image(new ByteArrayInputStream(item.getImageBytes())));
    } else if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
      new Thread(
          () -> {
            byte[] bytes = ItemsEventHandler.downloadItemImage(item.getImagePath());
            if (bytes != null) {
              item.setImageBytes(bytes);
              Platform.runLater(
                  () -> {
                    mainMenuAuctionCardImageView.setImage(
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
                      CurrencySelectorHandler.bindPriceLabel(mainMenuAuctionCardPriceLabel, newVal);
                      LabelHandler.scaleFontSizeToFit(mainMenuAuctionCardPriceLabel, 20, 12, 10, 1);
                    }
                  });
            });

    //TODO: initialize and listenner for updateColorByAuctionState method to update auction status in real time
  }

  public void mainMenuAuctionCardGoToItemDetails(ActionEvent event) {
    SceneController.switchToItemView(
        "/com/auction/client/views/itemDetails_view.fxml", event, currentItem);
  }

  public void mainMenuAuctionCardGoToAuction(ActionEvent event) {
    SceneController.switchToItemView(
        "/com/auction/client/views/auction_view.fxml", event, currentItem);
  }

  @FXML
  private Circle mainMenuAuctionCardStatusCircle;

  public void updateColorByAuctionState(String auctionState) {
    String targetColor;

    switch (auctionState.toUpperCase()) {
      case "OPEN":
        targetColor = "#47ff66"; // ?? change if theres difference between open and running status?
        break;
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
    mainMenuAuctionCardStatusCircle.setStyle("-fx-auction-status-color: " + targetColor + ";");
  }
}
