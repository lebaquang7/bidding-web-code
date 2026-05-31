package com.auction.client.Controllers;

import com.auction.client.Models.CurrencySelectorHandler;
import com.auction.client.Models.ItemsEventHandler;
import com.auction.client.Models.LabelHandler;
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
  @FXML Label mainMenuAuctionCardNameLabel;
  @FXML Label mainMenuAuctionCardPriceLabel;
  @FXML ImageView mainMenuAuctionCardImageView;

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

    // TODO: initialize and listenner for updateColorByAuctionState method to update auction status
    // in real time
  }

  public void mainMenuAuctionCardGoToItemDetails(ActionEvent event) {
    SceneController.switchToItemView(
        "/com/auction/client/views/itemDetails_view.fxml", event, currentItem);
  }

  public void mainMenuAuctionCardGoToAuction(ActionEvent event) {
    SceneController.switchToItemView(
        "/com/auction/client/views/auction_view.fxml", event, currentItem);
  }

  @FXML private Circle mainMenuAuctionCardStatusCircle;

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

  public void handleAutoBidSetup(ActionEvent event) {
    if (currentItem == null) return;

    // Hiện bảng hỏi giá trần
    javafx.scene.control.TextInputDialog maxBidDialog =
        new javafx.scene.control.TextInputDialog("1000000");
    maxBidDialog.setTitle("Cài đặt Auto-Bid");
    maxBidDialog.setHeaderText("Cài đặt trả giá tự động cho" + currentItem.getItemName());
    maxBidDialog.setContentText("Nhập giá tối đa bạn muốn trả:");

    java.util.Optional<String> maxBidResult = maxBidDialog.showAndWait();
    if (maxBidResult.isPresent()) {

      // Hiện bảng hỏi bước giá tự động
      javafx.scene.control.TextInputDialog incrementDialog =
          new javafx.scene.control.TextInputDialog(currentItem.getPriceIncrement().toString());
      incrementDialog.setTitle("Cài đặt bước giá");
      incrementDialog.setHeaderText("Bước giá mỗi lần hệ thống thay bạn đè lên đối thủ");
      incrementDialog.setContentText("Nhập bước giá:");

      java.util.Optional<String> incResult = incrementDialog.showAndWait();
      if (incResult.isPresent()) {
        try {
          java.math.BigDecimal maxBid = new java.math.BigDecimal(maxBidResult.get());
          java.math.BigDecimal increment = new java.math.BigDecimal(incResult.get());

          java.util.Map<String, Object> autoBidMap = new java.util.HashMap<>();
          autoBidMap.put("itemId", currentItem.getId());

          String myBidderId =
              com.auction.client.Models.AccountEventHandler.getCurrentUser().getId();
          autoBidMap.put("bidderId", myBidderId);

          autoBidMap.put("maxBid", maxBid);
          autoBidMap.put("increment", increment);

          com.auction.shared.models.NetworkRequest request =
              new com.auction.shared.models.NetworkRequest(
                  com.auction.shared.models.NetworkRequest.requestType.Bid, autoBidMap);

          // Gửi cấu hình Bot lên Server
          try (java.net.Socket socket = new java.net.Socket("localhost", 1234);
              java.io.ObjectOutputStream out =
                  new java.io.ObjectOutputStream(socket.getOutputStream())) {
            out.flush();
            out.writeObject(request);
            System.out.println("Đã gửi cấu hình Bot Auto-Bid lên Server thành công");
          } catch (Exception e) {
            System.err.println("Lỗi kết nối khi gửi Auto-Bid" + e.getMessage());
          }

        } catch (Exception e) {
          System.out.println("Nhập đúng định dạng số:" + e.getMessage());
        }
      }
    }
  }
}
