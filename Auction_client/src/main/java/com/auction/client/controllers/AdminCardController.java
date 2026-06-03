package com.auction.client.controllers;

import com.auction.client.services.AccountEventHandler;
import com.auction.client.services.ItemsEventHandler;
import com.auction.client.services.SceneHandler;
import com.auction.client.utils.AlertMessageHandler;
import com.auction.client.utils.CurrencySelectorHandler;
import com.auction.client.utils.LabelHandler;
import com.auction.shared.models.Item;
import com.auction.shared.models.User;
import java.io.ByteArrayInputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class AdminCardController {
  // Hầu hết giống với file AuctionCardController.java, nhưng cho thẻ sản phẩm hiện trong admin
  // panel
  // Controller class hiển thị các thẻ sản phẩm nhỏ trong Admin - Auction View
  // Đường dẫn đến view của controller này
  private static final String PATH_TO_VIEW =
      "/com/auction/client/views/mainMenu_adminAuctionCard.fxml";

  public static String getPATH_TO_VIEW() {
    return PATH_TO_VIEW;
  }

  @FXML Label nameLabel;
  @FXML Label priceLabel;
  @FXML ImageView imageView;

  private Item currentItem;

  /**
   * Usage: Truyền vào card các dữ liệu của sản phẩm
   *
   * @param item Sản phẩm được truyền vào
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
  }

  /**
   * Usage: Mở panel thông tin sản phẩm khi nhấn nút
   *
   * @param event
   */
  @FXML
  public void goToItemDetails(ActionEvent event) {
    SceneHandler.switchToItemView(ItemDetailsController.getPATH_TO_VIEW(), event, currentItem);
  }

  /**
   * Usage: Khởi tạo phiên đấu giá khi nhấn nút
   *
   * @param event
   */
  @FXML
  public void initializeAuction(ActionEvent event) {
    User currentUser = AccountEventHandler.getCurrentUser();
    String result = ItemsEventHandler.initializeAuction(currentItem.getId(), currentUser);

    if (result.equals("success")) {
      AlertMessageHandler.showInfo(
          "Thành công", "", currentItem.getItemName() + " đã được đấu giá.");
    } else if (result.equals("unauthorized")) {
      AlertMessageHandler.showError("Lỗi", "", "Bạn không phải Admin.");
    } else {
      AlertMessageHandler.showError("Lỗi", "", "Không thể khởi tạo phiên đấu giá.");
    }
  }

  /**
   * Usage: Từ chối phiên đấu giá khi nhấn nút
   *
   * @param event
   */
  @FXML
  public void denyAuction(ActionEvent event) {
    User currentUser = AccountEventHandler.getCurrentUser();
    String result = ItemsEventHandler.denyAuction(currentItem.getId(), currentUser);

    if (result.equals("success")) {
      AlertMessageHandler.showInfo("Thành công: ", "", "Vật phẩm đã được xóa khỏi hệ thống.");

      Platform.runLater(
          () -> {
            if (nameLabel.getScene() != null) {
              Node cardNode = nameLabel.getParent();
              if (cardNode.getParent() instanceof Pane) {
                ((Pane) cardNode.getParent()).getChildren().remove(cardNode);
              }
            }
          });

    } else if (result.equals("running") || result.equals("finished")) {
      AlertMessageHandler.showError(
          "Lỗi", "", "Phiên đấu giá đã được khởi tạo hoặc đã kết thúc nên không hủy được.");
    } else if (result.equals("unauthorized")) {
      AlertMessageHandler.showError(
          "Lỗi", "", "Bạn không có quyền Admin để thực hiện thao tác này.");
    } else {
      AlertMessageHandler.showError("Lỗi", "", "Không thể xóa vật phẩm do lỗi hệ thống.");
    }
  }

  /**
   * Usage: Khi nhấn vào hình ảnh, mở một panel nhỏ hiện hình ảnh phóng to
   *
   * @param event
   */
  @FXML
  public void openImageView(MouseEvent event) {
    SceneHandler.switchToImageView(event, imageView.getImage());
  }
}
