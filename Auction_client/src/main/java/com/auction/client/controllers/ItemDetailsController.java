package com.auction.client.controllers;

import com.auction.client.services.AuctionBiddingService;
import com.auction.client.services.ClientNotificationListener;
import com.auction.client.services.ItemsEventHandler;
import com.auction.client.services.SceneHandler;
import com.auction.client.utils.CurrencySelectorHandler;
import com.auction.client.utils.LabelHandler;
import com.auction.client.utils.MiscTools;
import com.auction.shared.models.Inventory;
import com.auction.shared.models.Item;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ItemDetailsController implements SceneHandler.ItemLoadable {
  // Controller class cho màn hình hiển thị thông tin sản phẩm
  // Đường dẫn đến view của controller này
  private static final String PATH_TO_VIEW = "/com/auction/client/views/itemDetails_view.fxml";

  public static String getPATH_TO_VIEW() {
    return PATH_TO_VIEW;
  }

  @FXML Label idLabel;
  @FXML Label itemNameLabel;
  @FXML Label descriptionLabel;
  @FXML Label initialPriceLabel;
  @FXML Label currentPriceLabel;
  @FXML Label winnerLabel;
  @FXML Label timeRemainingLabel;
  @FXML ImageView imageView;

  private Item currentItem;

  /**
   * Usage: Đóng màn hình khi nhấn thoát
   *
   * @param event
   */
  public void goBackToList(ActionEvent event) {
    SceneHandler.closeScene(event);
  }

  /**
   * Usage: Xử lý thông tin
   *
   * @param message
   */
  public void handleNotification(Object message) {
    Platform.runLater(
        () -> {
          // Gọi hàm overload đã chốt trong AuctionBiddingService
          AuctionBiddingService.processIncomingNotification(message, currentItem, this);
        });
  }

  /**
   * Usage: Cập nhật thời gian còn lại
   *
   * @param totalSeconds Thời gian còn lại
   */
  public void updateRemainingTime(int totalSeconds) {
    timeRemainingLabel.setText(MiscTools.formatSecondsToMinutes(totalSeconds));
    if (totalSeconds <= 10) {
      timeRemainingLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    } else {
      timeRemainingLabel.setStyle("-fx-text-fill: -theme-text-color; -fx-font-weight: normal;");
    }
  }

  /**
   * Usage: Thay đổi màn hình khi phiên kết thúc
   *
   * @param winnerName Tên người thắng phiên
   */
  public void handleAuctionEndEvent(String winnerName) {
    timeRemainingLabel.setText("00:00");
    winnerLabel.setText(winnerName);
  }

  @Override
  public void setItem(Item item) {
    ClientNotificationListener.setCurrentController(this);
    this.currentItem = Inventory.getItemById(item.getId());
    LocalDateTime now = LocalDateTime.now();
    String bidderName = currentItem.getHighestBidderName();

    timeRemainingLabel.setText("00:00");

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

    if (bidderName == null || bidderName.trim().isEmpty()) {
      winnerLabel.setText("------");
    } else {
      if (currentItem.getEndTime() != null && now.isBefore(currentItem.getEndTime())) {
        winnerLabel.setText("------");
      } else {
        winnerLabel.setText(bidderName);
      }
    }

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
