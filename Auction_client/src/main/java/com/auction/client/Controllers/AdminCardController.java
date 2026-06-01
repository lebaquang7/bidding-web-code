package com.auction.client.Controllers;

import java.io.ByteArrayInputStream;

import com.auction.client.Models.AccountEventHandler;
import com.auction.client.Models.CurrencySelectorHandler;
import com.auction.client.Models.ItemsEventHandler;
import com.auction.client.Models.LabelHandler;
import com.auction.shared.models.Item;

import com.auction.shared.models.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AdminCardController {
  // mostly copied from auctionCardController
  @FXML
  Label mainMenuAdminAuctionCardNameLabel;
  @FXML
  Label mainMenuAdminAuctionCardPriceLabel;
  @FXML
  ImageView mainMenuAdminAuctionCardImageView;

  // each auction card holds the current item
  private Item currentItem;

  public void setData(Item item) {
    currentItem = item;

    mainMenuAdminAuctionCardNameLabel.setText(item.getItemName());
    LabelHandler.setDetailedTooltip(mainMenuAdminAuctionCardNameLabel);

    CurrencySelectorHandler.bindPriceLabel(
        mainMenuAdminAuctionCardPriceLabel, item.getCurrentPrice());
    LabelHandler.scaleFontSizeToFit(mainMenuAdminAuctionCardPriceLabel, 20, 12, 10, 1);

    // Tải hình ảnh lên
    if (item.getImageBytes() != null) {
      mainMenuAdminAuctionCardImageView.setImage(
          new Image(new ByteArrayInputStream(item.getImageBytes())));
    } else if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
      new Thread(
          () -> {
            byte[] bytes = ItemsEventHandler.downloadItemImage(item.getImagePath());
            if (bytes != null) {
              item.setImageBytes(bytes);
              Platform.runLater(
                  () -> {
                    mainMenuAdminAuctionCardImageView.setImage(
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
                          mainMenuAdminAuctionCardPriceLabel, newVal);
                      LabelHandler.scaleFontSizeToFit(
                          mainMenuAdminAuctionCardPriceLabel, 20, 12, 10, 1);
                    }
                  });
            });
  }

  public void mainMenuAdminAuctionCardGoToItemDetails(ActionEvent event) {
    SceneController.switchToItemView(
        "/com/auction/client/views/itemDetails_view.fxml", event, currentItem);
  }

  // TODO: make this button push an auction to the actual auction list
  @FXML
  public void mainMenuAdminAuctionCardInitializeAuction(ActionEvent event) {
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
  public void mainMenuAdminAuctionCardDenyAuction(ActionEvent event) {
  }
}
