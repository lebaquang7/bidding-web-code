package com.auction.client.Controllers;

import com.auction.client.MainApp;
import com.auction.client.Models.*;
import com.auction.shared.models.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class AuctionViewController implements SceneController.ItemLoadable {

  @FXML VBox auctionViewBidderFeatureBox;
  @FXML Label auctionViewItemName;
  @FXML Label auctionViewStartingBid;
  @FXML Label auctionViewCurrentBid;
  @FXML Label auctionViewRemainingTime;
  @FXML Label auctionViewPlaceBidErrorBox;
  @FXML Label auctionViewAutoBidderErrorBox;

  @FXML TextField auctionViewPlaceBidBox;
  @FXML TextField auctionViewAutoBidderMaxBidBox;
  @FXML TextField auctionViewAutoBidderBidIncrementBox;

  @FXML LineChart<Number, Number> auctionViewPriceChart;
  @FXML NumberAxis auctionViewPriceChartXAxis;
  @FXML NumberAxis auctionViewPriceChartYAxis;

  private Item currentItem;

  public void initialize() {
    if (!(AccountEventHandler.getCurrentUser() instanceof Bidder)) {
      SceneController.disableElement(auctionViewBidderFeatureBox);
    }
  }

  public void auctionViewGoBackToList(ActionEvent event) {
    if (MainApp.getNotificationListener() != null) {
      MainApp.getNotificationListener().stopListener();
      MainApp.setNotificationListener(null);
    }
    SceneController.closeScene(event);
  }

  @FXML
  public void auctionViewPlaceBid(ActionEvent event) {
    // 1. Xóa thông báo lỗi/thành công cũ
    auctionViewPlaceBidErrorBox.setStyle("-fx-text-fill: red;");
    auctionViewPlaceBidErrorBox.setText("");

    String bidInput = auctionViewPlaceBidBox.getText();

    // 2. Kiểm tra rỗng
    if (bidInput == null || bidInput.trim().isEmpty()) {
      auctionViewPlaceBidErrorBox.setText("Vui lòng nhập số tiền muốn trả!");
      return;
    }

    try {
      BigDecimal bidAmount = new BigDecimal(auctionViewPlaceBidBox.getText());
      BigDecimal currentPrice = currentItem.getCurrentPrice();
      BigDecimal incrementPercent = currentItem.getPriceIncrement();
      BigDecimal minBidRequired =
          currentPrice.add(currentPrice.multiply(incrementPercent).divide(new BigDecimal("100")));

      if (bidAmount.compareTo(minBidRequired) < 0) {
        auctionViewPlaceBidErrorBox.setText("Tối thiểu: " + minBidRequired.toPlainString());
        return;
      }

      // 4. Lấy thông tin User hiện tại đang đăng nhập
      User currentUser = AccountEventHandler.getCurrentUser();
      if (currentUser == null) {
        auctionViewPlaceBidErrorBox.setText("Lỗi: Không tìm thấy thông tin phiên đăng nhập!");
        return;
      }

      // Trả về enum BidStatus từ Server
      BidStatus.bidStatus result =
          ItemsEventHandler.placeBid(currentItem.getId(), currentUser.getId(), bidAmount);

      // 6. Xử lý UI dựa trên phản hồi của Server
      if (result == BidStatus.bidStatus.SUCCESS) {
        auctionViewPlaceBidErrorBox.setStyle("-fx-text-fill: green;");
        auctionViewPlaceBidErrorBox.setText("Đặt giá thành công!");
        auctionViewPlaceBidBox.clear();
      } else if (result == BidStatus.bidStatus.INVALID) {
        auctionViewPlaceBidErrorBox.setText("Giá chưa đạt bước giá tối thiểu quy định!");
      } else if (result == BidStatus.bidStatus.ALREADY_HIGHEST) {
        auctionViewPlaceBidErrorBox.setText("Bạn đang là người giữ giá cao nhất!");
      } else {
        auctionViewPlaceBidErrorBox.setText("Phiên đấu giá chưa bắt đầu");
      }

    } catch (NumberFormatException e) {
      auctionViewPlaceBidErrorBox.setText("Vui lòng nhập đúng định dạng số tiền!");
    }
  }

  // TODO: work on these
  public void auctionViewEnableAutoBid(ActionEvent event) {}

  public void auctionViewStopAutoBid(ActionEvent event) {}

  @Override
  public void setItem(Item item) {
    ClientNotificationListener.setCurrentController(this);
    this.currentItem = Inventory.getItemById(item.getId());
    if (this.currentItem == null) {this.currentItem = item;} // Phòng hờ nếu Inventory rỗng

    // Hiển thị giá ban đầu
    CurrencySelectorHandler.bindPriceLabel(auctionViewCurrentBid, item.getCurrentPrice());

    // init labels
    auctionViewItemName.setText(currentItem.getItemName());
    LabelHandler.setDetailedTooltip(auctionViewItemName);

    CurrencySelectorHandler.bindPriceLabel(auctionViewStartingBid, currentItem.getStartingPrice());
    LabelHandler.scaleFontSizeToFit(auctionViewStartingBid, 20, 12, 8, 1);

    CurrencySelectorHandler.bindPriceLabel(auctionViewCurrentBid, currentItem.getCurrentPrice());
    LabelHandler.scaleFontSizeToFit(auctionViewCurrentBid, 20, 12, 8, 1);

    // init chart
    // TODO: link time with actual datas
    auctionViewPriceChart.setTitle("Auction price for Item " + item.getItemName());
    auctionViewPriceChartXAxis.setLabel("Time");
    auctionViewPriceChartYAxis.setLabel("Price");

    // toggle off auto ranging so one can manually set bounds. bounds will be linked
    // with observable
    // to track price.
    auctionViewPriceChartXAxis.setAutoRanging(false);
    auctionViewPriceChartYAxis.setAutoRanging(false);

    // TODO: track based on time. HALF COMPLETED, NEED FETCHIN TIME LINKED WITH ITEM
    // X label format based on time
    auctionViewPriceChartXAxis.setTickLabelFormatter(
        new ChartTimeLabelFormatter(auctionViewPriceChartXAxis));

    AuctionViewController.updateChartPrice(item, auctionViewPriceChartYAxis);
    // TODO: UNCOMMENT WHEN ACTUAL CHART DATA IS MADE
    // auctionViewPriceChart.setData(ChartDataHandler
    // .setChartDisplay(AuctionManager.getInstance().getAuctionSession(item.getId()).getBidHistory()));

    // listener for currency type changes
    CurrencySelectorHandler.getInstance()
        .getActiveCurrencyObjectProperty()
        .addListener(
            (observable, oldVal, newVal) -> {
              AuctionViewController.updateChartPrice(item, auctionViewPriceChartYAxis);
              // auctionViewPriceChart.setData(ChartDataHandler
              // .setChartDisplay(AuctionManager.getInstance().getAuctionSession(item.getId()).getBidHistory()));
            });

    // Cập nhật currentPrice realtime
    item.currentPriceProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              Platform.runLater(() -> {
                CurrencySelectorHandler.bindPriceLabel(auctionViewCurrentBid, newVal);

                LabelHandler.scaleFontSizeToFit(auctionViewCurrentBid, 20, 12, 8, 1);

                AuctionViewController.updateChartPrice(item, auctionViewPriceChartYAxis);

                System.out.println("UI đã cập nhật giá mới và biểu đồ: " + newVal);
              });
            });

    // Khởi tạo thời gian
    auctionViewRemainingTime.setText("00:00");
    auctionViewRemainingTime.setStyle("-fx-text-fill: black; -fx-font-weight: normal;");
  }

  public void handleNotification(Object message) {
    if (message instanceof BidTransaction) {
      BidTransaction tx = (BidTransaction) message;

      if (currentItem != null && tx.getItemId().equals(currentItem.getId())) {
        Platform.runLater(() -> {
          currentItem.setCurrentPrice(tx.getBidAmount());
          currentItem.setHighestBidderId(tx.getBidderId());
        });
      }
    }

    else if (message instanceof Map) {
      Map<String, Object> data = (Map<String, Object>) message;
      String type = (String) data.get("type");

      if ("TIME_UPDATE".equals(type)) {
        String sessionId = String.valueOf(data.get("sessionId")).trim();

        if (currentItem != null && currentItem.getId().equals(sessionId)) {
          Object valObj = data.get("value");
          if (valObj instanceof Number) {
            int totalSeconds = ((Number) valObj).intValue();
            Platform.runLater(() -> {
              auctionViewRemainingTime.setText(formatTime(totalSeconds));

              if (totalSeconds <= 10) {
                auctionViewRemainingTime.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
              } else {
                auctionViewRemainingTime.setStyle("-fx-text-fill: black; -fx-font-weight: normal;");
              }
            });
          }
        }
      }

      if ("END_AUCTION".equals(data.get("type"))) {
        Platform.runLater(() -> {
          String winner = (String) data.get("winnerName");

          auctionViewBidderFeatureBox.setDisable(true);
          auctionViewRemainingTime.setText("00:00");
          auctionViewPlaceBidErrorBox.setText("PHIÊN KẾT THÚC. NGƯỜI THẮNG: " + winner);

          System.out.println("Kết thúc phiên. Người thắng là " + winner);
        });
      }
    }
  }

  /**
   * Usage: update Y axis's chart position based on price.
   *
   * @param item
   * @param yAxis
   */
  public static void updateChartPrice(Item item, NumberAxis yAxis) {
    yAxis.setLowerBound(0);
    BigDecimal updatedPrice =
        CurrencySelectorHandler.getInstance().getConvertedPrice(item.getCurrentPrice());
    yAxis.setUpperBound(MiscTools.roundUp(updatedPrice.doubleValue()));
    yAxis.setTickUnit(MiscTools.roundUp(updatedPrice.doubleValue()) / 10);
  }

  // Giúp remaining time hiển thị cả phút
  private String formatTime(int totalSeconds) {
    int minutes = totalSeconds / 60;
    int seconds = totalSeconds % 60;
    return String.format("%02d:%02d", minutes, seconds);
  }
}
