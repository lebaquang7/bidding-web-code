package com.auction.client.controllers;

import com.auction.client.services.AuctionBiddingService;
import com.auction.client.services.ClientNotificationListener;
import com.auction.client.services.ItemsEventHandler;
import com.auction.client.services.SceneHandler;
import com.auction.client.utils.CurrencySelectorHandler;
import com.auction.client.utils.LabelHandler;
import com.auction.client.utils.MiscTools;
import com.auction.shared.models.Item;
import java.io.ByteArrayInputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ItemDetailsController implements SceneHandler.ItemLoadable {

  @FXML Label idLabel;
  @FXML Label itemNameLabel;
  @FXML Label descriptionLabel;
  @FXML Label initialPriceLabel;
  @FXML Label currentPriceLabel;
  @FXML Label winnerLabel;
  @FXML Label timeRemainingLabel;
  @FXML ImageView imageView;

  private Item currentItem;

  public void goBackToList(ActionEvent event) {
    SceneHandler.closeScene(event);
  }

  // TODO: opens a small panel where user can input details. does nothing with the
  // details given. removes the auction when info in small panel is given.
  public void proceedToPayment(ActionEvent event) {
    SceneHandler.closeScene(event);
  }

  public void handleNotification(Object message) {
    Platform.runLater(
        () -> {
          // Gọi hàm overload đã chốt trong AuctionBiddingService
          AuctionBiddingService.processIncomingNotification(message, currentItem, this);
        });
  }

  public void updateRemainingTime(int totalSeconds) {
    timeRemainingLabel.setText(MiscTools.formatSecondsToMinutes(totalSeconds));
    if (totalSeconds <= 10) {
      timeRemainingLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    } else {
      timeRemainingLabel.setStyle("-fx-text-fill: -theme-text-color; -fx-font-weight: normal;");
    }
  }

  public void handleAuctionEndEvent(String winnerName) {
    timeRemainingLabel.setText("00:00");
    winnerLabel.setText(winnerName);
  }

  @Override
  public void setItem(Item item) {
    ClientNotificationListener.setCurrentController(this);
    this.currentItem = item;

    idLabel.setText(currentItem.getId());
    LabelHandler.setDetailedTooltip(idLabel);

    itemNameLabel.setText(currentItem.getItemName());
    LabelHandler.setDetailedTooltip(itemNameLabel);

    descriptionLabel.setText(currentItem.getDescription());
    LabelHandler.setDetailedTooltip(descriptionLabel);

    CurrencySelectorHandler.bindPriceLabel(initialPriceLabel, currentItem.getStartingPrice());
    LabelHandler.scaleFontSizeToFit(initialPriceLabel, 15, 12, 10, 1);

    CurrencySelectorHandler.bindPriceLabel(currentPriceLabel, currentItem.getCurrentPrice());
    LabelHandler.scaleFontSizeToFit(currentPriceLabel, 15, 12, 10, 1);

    if (currentItem.getImageBytes() != null) {
      imageView.setImage(new Image(new ByteArrayInputStream(currentItem.getImageBytes())));
    } else if (currentItem.getImagePath() != null && !currentItem.getImagePath().isEmpty()) {
      new Thread(
              () -> {
                byte[] bytes = ItemsEventHandler.downloadItemImage(currentItem.getImagePath());
                if (bytes != null) {
                  currentItem.setImageBytes(bytes);
                  Platform.runLater(
                      () -> {
                        imageView.setImage(new Image(new ByteArrayInputStream(bytes)));
                      });
                }
              })
          .start();
    }

    item.currentPriceProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              Platform.runLater(
                  () -> {
                    if (newVal != null) {
                      CurrencySelectorHandler.bindPriceLabel(currentPriceLabel, newVal);
                      LabelHandler.scaleFontSizeToFit(currentPriceLabel, 20, 12, 10, 1);
                    }
                  });
            });
  }
}
