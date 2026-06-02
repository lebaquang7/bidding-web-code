package com.auction.client.controllers;

import java.io.ByteArrayInputStream;

import com.auction.client.services.ItemsEventHandler;
import com.auction.client.services.SceneHandler;
import com.auction.client.utils.CurrencySelectorHandler;
import com.auction.client.utils.LabelHandler;
import com.auction.shared.models.AuctionStatus;
import com.auction.shared.models.Item;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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

    updateColorByAuctionState();
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

  public void updateColorByAuctionState() {
    AuctionStatus auctionState = ItemsEventHandler.getAuctionStatus(currentItem);
    String targetColor;

    targetColor = switch (auctionState) {
          case RUNNING -> "#47ff66";
          case FINISHED -> "#45cbf0";
          case CANCELLED -> "#f53535";
          case PAID -> "#db35f5";
          case PENDING_APPROVAL -> "#ff6600";
          default -> "#7f8c8d";
      };
    statusCircle.setStyle("-fx-fill: " + targetColor + ";");
  }

  @FXML
  public void openImageView(MouseEvent event) {
    SceneHandler.switchToImageView(event, imageView.getImage());
  }
}
