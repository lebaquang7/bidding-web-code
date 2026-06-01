package com.auction.client.controllers;

import java.io.ByteArrayInputStream;

import com.auction.client.services.AccountEventHandler;
import com.auction.client.services.ItemsEventHandler;
import com.auction.client.services.SceneHandler;
import com.auction.client.utils.CurrencySelectorHandler;
import com.auction.client.utils.LabelHandler;
import com.auction.shared.models.Item;
import com.auction.shared.models.User;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AdminCardController {
  // mostly copied from auctionCardController
  @FXML
  Label nameLabel;
  @FXML
  Label priceLabel;
  @FXML
  ImageView imageView;

  // each auction card holds the current item
  private Item currentItem;

  public void setData(Item item) {
    currentItem = item;

    nameLabel.setText(item.getItemName());
    LabelHandler.setDetailedTooltip(nameLabel);

    CurrencySelectorHandler.bindPriceLabel(
        priceLabel, item.getCurrentPrice());
    LabelHandler.scaleFontSizeToFit(priceLabel, 20, 12, 10, 1);

    // Tải hình ảnh lên
    if (item.getImageBytes() != null) {
      imageView.setImage(
          new Image(new ByteArrayInputStream(item.getImageBytes())));
    } else if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
      new Thread(
          () -> {
            byte[] bytes = ItemsEventHandler.downloadItemImage(item.getImagePath());
            if (bytes != null) {
              item.setImageBytes(bytes);
              Platform.runLater(
                  () -> {
                    imageView.setImage(
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
                          priceLabel, newVal);
                      LabelHandler.scaleFontSizeToFit(
                          priceLabel, 20, 12, 10, 1);
                    }
                  });
            });
  }

  @FXML
  public void goToItemDetails(ActionEvent event) {
    SceneHandler.switchToItemView(
        "/com/auction/client/views/itemDetails_view.fxml", event, currentItem);
  }

  @FXML
  public void initializeAuction(ActionEvent event) {
    User currentUser = AccountEventHandler.getCurrentUser();
    String result = ItemsEventHandler.initializeAuction(currentItem.getId(), currentUser);

    if (result.equals("success")) {
      AlertMessageController.showInfo("Thành công", "", currentItem.getItemName() + " đã được đấu giá.");
    } else if (result.equals("unauthorized")) {
      AlertMessageController.showError("Lỗi", "", "Bạn không phải Admin.");
    } else {
      AlertMessageController.showError("Lỗi", "", "Không thể khởi tạo phiên đấu giá.");
    }
  }

  // TODO: make this button denies a published auction from coming to the actual
  // auction list
  @FXML
  public void denyAuction(ActionEvent event) {
  }
}
