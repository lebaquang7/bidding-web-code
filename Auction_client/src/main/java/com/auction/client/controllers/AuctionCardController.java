package com.auction.client.controllers;

import com.auction.client.services.ItemsEventHandler;
import com.auction.client.services.SceneHandler;
import com.auction.client.utils.CurrencySelectorHandler;
import com.auction.client.utils.LabelHandler;
import com.auction.shared.models.AuctionStatus;
import com.auction.shared.models.Item;
import java.io.ByteArrayInputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

public class AuctionCardController {
  // Controller class của các thẻ sản phẩm nhỏ trong Auction List
  // Đường dẫn đến view của controller này
  private static final String PATH_TO_VIEW = "/com/auction/client/views/mainMenu_auctionCard.fxml";

  public static String getPATH_TO_VIEW() {
    return PATH_TO_VIEW;
  }

  @FXML Label nameLabel;
  @FXML Label priceLabel;
  @FXML ImageView imageView;
  @FXML Circle statusCircle;

  // Mỗi thẻ đấu giá giữ thông tin của sản phẩm hiện tại
  private Item currentItem;

  /**
   * Usage: Truyền thông tin vào thẻ sản phẩm, khởi tạo sản phẩm
   *
   * @param item Sản phẩm truyền vào
   */
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

    // Tự cập nhật giá với listener.
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

    // Cập nhật màu của chấm hiển thị trạng thái phiên đấu
    updateColorByAuctionState();
  }

  /**
   * Usage: Mở panel thông tin sản phẩm khi nhán nút
   *
   * @param event
   */
  @FXML
  public void goToItemDetails(ActionEvent event) {
    SceneHandler.switchToItemView(ItemDetailsController.getPATH_TO_VIEW(), event, currentItem);
  }

  /**
   * Usage: Mở panel đấu giá khi nhấn nút
   *
   * @param event
   */
  @FXML
  public void goToAuction(ActionEvent event) {
    SceneHandler.switchToItemView(AuctionViewController.getPATH_TO_VIEW(), event, currentItem);
  }

  /** Usage: Cập nhật màu hiển thị trạng thái đấu giá */
  public void updateColorByAuctionState() {
    AuctionStatus auctionState = ItemsEventHandler.getAuctionStatus(currentItem);
    String targetColor;

    targetColor =
        switch (auctionState) {
          case RUNNING -> "#47ff66";
          case FINISHED -> "#45cbf0";
          case CANCELLED -> "#f53535";
          case PAID -> "#db35f5";
          case PENDING_APPROVAL -> "#ff6600";
          default -> "#7f8c8d";
        };
    statusCircle.setStyle("-fx-fill: " + targetColor + ";");
  }

  /**
   * Usage: Khi nhấn vào hình ảnh, mở một popup phóng to hình ảnh
   *
   * @param event
   */
  @FXML
  public void openImageView(MouseEvent event) {
    SceneHandler.switchToImageView(event, imageView.getImage());
  }
}
