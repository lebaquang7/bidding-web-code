package com.auction.client.controllers;

import com.auction.client.MainApp;
import com.auction.client.services.AccountEventHandler;
import com.auction.client.services.AuctionBiddingService;
import com.auction.client.services.ClientNotificationListener;
import com.auction.client.services.ItemsEventHandler;
import com.auction.client.services.SceneHandler;
import com.auction.client.utils.ChartTimeLabelFormatter;
import com.auction.client.utils.CurrencySelectorHandler;
import com.auction.client.utils.LabelHandler;
import com.auction.client.utils.MiscTools;
import com.auction.client.utils.UIElementHandler;
import com.auction.shared.models.BidStatus;
import com.auction.shared.models.Bidder;
import com.auction.shared.models.Inventory;
import com.auction.shared.models.Item;
import com.auction.shared.models.User;
import java.math.BigDecimal;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class AuctionViewController implements SceneHandler.ItemLoadable {

  @FXML VBox bidderFeatureBox;
  @FXML Label itemNameLabel;
  @FXML Label startingBidLabel;
  @FXML Label currentBidLabel;
  @FXML Label remainingTimeLabel;
  @FXML Label placeBidErrorBox;
  @FXML Label autoBidderErrorBox;

  @FXML TextField placeBidBox;
  @FXML TextField autoBidderMaxBidBox;
  @FXML TextField autoBidderBidIncrementBox;

  @FXML LineChart<Number, Number> priceChart;
  @FXML NumberAxis priceChartXAxis;
  @FXML NumberAxis priceChartYAxis;

  private Item currentItem;

  public void initialize() {
    if (!(AccountEventHandler.getCurrentUser() instanceof Bidder)) {
      UIElementHandler.disableElement(bidderFeatureBox);
    }
  }

  @FXML
  public void goBackToList(ActionEvent event) {
    if (MainApp.getNotificationListener() != null) {
      MainApp.getNotificationListener().stopListener();
      MainApp.setNotificationListener(null);
    }
    SceneHandler.closeScene(event);
  }

  @FXML
  public void placeBid(ActionEvent event) {
    // 1. Xóa thông báo lỗi/thành công cũ
    placeBidErrorBox.setStyle("-fx-text-fill: red;");
    placeBidErrorBox.setText("");

    String bidInput = placeBidBox.getText();

    // 2. Kiểm tra rỗng
    if (bidInput == null || bidInput.trim().isEmpty()) {
      placeBidErrorBox.setText("Vui lòng nhập số tiền muốn trả!");
      return;
    }

    try {
      BigDecimal bidAmount = new BigDecimal(placeBidBox.getText());
      BigDecimal currentPrice = currentItem.getCurrentPrice();
      BigDecimal incrementPercent = currentItem.getPriceIncrement();
      BigDecimal minBidRequired =
          currentPrice.add(currentPrice.multiply(incrementPercent).divide(new BigDecimal("100")));

      if (bidAmount.compareTo(minBidRequired) < 0) {
        placeBidErrorBox.setText("Tối thiểu: " + minBidRequired.toPlainString());
        return;
      }

      // 4. Lấy thông tin User hiện tại đang đăng nhập
      User currentUser = AccountEventHandler.getCurrentUser();
      if (currentUser == null) {
        placeBidErrorBox.setText("Lỗi: Không tìm thấy thông tin phiên đăng nhập!");
        return;
      }

      // Trả về enum BidStatus từ Server
      BidStatus.bidStatus result =
          ItemsEventHandler.placeBid(currentItem.getId(), currentUser.getId(), bidAmount);
      updateUiFromBidStatus(result);

      // 6. Xử lý UI dựa trên phản hồi của Server
      if (result == BidStatus.bidStatus.SUCCESS) {
        placeBidErrorBox.setStyle("-fx-text-fill: green;");
        placeBidErrorBox.setText("Đặt giá thành công!");
        placeBidBox.clear();
      } else if (result == BidStatus.bidStatus.INVALID) {
        placeBidErrorBox.setText("Giá chưa đạt bước giá tối thiểu quy định!");
      } else if (result == BidStatus.bidStatus.ALREADY_HIGHEST) {
        placeBidErrorBox.setText("Bạn đang là người giữ giá cao nhất!");
      } else {
        placeBidErrorBox.setText("Phiên đấu giá chưa bắt đầu");
      }

    } catch (NumberFormatException e) {
      placeBidErrorBox.setText("Vui lòng nhập đúng định dạng số tiền!");
    }
  }

  @Override
  public void setItem(Item item) {
    ClientNotificationListener.setCurrentController(this);
    this.currentItem = Inventory.getItemById(item.getId());
    if (this.currentItem == null) {
      this.currentItem = item;
    } // Phòng hờ nếu Inventory rỗng

    // Hiển thị giá ban đầu
    CurrencySelectorHandler.bindPriceLabel(currentBidLabel, item.getCurrentPrice());

    // init labels
    itemNameLabel.setText(currentItem.getItemName());
    LabelHandler.setDetailedTooltip(itemNameLabel);
    // init time label
    remainingTimeLabel.setText("00:00");
    remainingTimeLabel.setStyle("-fx-text-fill: -theme-text-color; -fx-font-weight: normal;");

    CurrencySelectorHandler.bindPriceLabel(startingBidLabel, currentItem.getStartingPrice());
    LabelHandler.scaleFontSizeToFit(startingBidLabel, 20, 12, 8, 1);
    CurrencySelectorHandler.bindPriceLabel(currentBidLabel, currentItem.getCurrentPrice());
    LabelHandler.scaleFontSizeToFit(currentBidLabel, 20, 12, 8, 1);

    setupChartLayout(item);

    AuctionViewController.updateChartPrice(item, priceChartYAxis);
    // TODO: UNCOMMENT WHEN ACTUAL CHART DATA IS MADE
    // auctionViewPriceChart.setData(ChartDataHandler
    // .setChartDisplay(AuctionManager.getInstance().getAuctionSession(item.getId()).getBidHistory()));

    // listener for currency type changes
    CurrencySelectorHandler.getInstance()
        .getActiveCurrencyObjectProperty()
        .addListener((observable, oldVal, newVal) -> updateChartBounds());

    // Cập nhật currentPrice realtime
    item.currentPriceProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              Platform.runLater(
                  () -> {
                    CurrencySelectorHandler.bindPriceLabel(currentBidLabel, newVal);
                    LabelHandler.scaleFontSizeToFit(currentBidLabel, 20, 12, 8, 1);
                    AuctionViewController.updateChartPrice(item, priceChartYAxis);
                    updateChartBounds();
                  });
            });
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

  /** Usage: pass on msg to auction bidding service to process */
  public void handleNotification(Object message) {
    Platform.runLater(
        () -> {
          AuctionBiddingService.processIncomingNotification(message, currentItem, this);
        });
  }

  /** Usage: update remaining time label */
  public void updateRemainingTime(int totalSeconds) {
    remainingTimeLabel.setText(MiscTools.formatSecondsToMinutes(totalSeconds));
    if (totalSeconds <= 10) {
      remainingTimeLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    } else {
      remainingTimeLabel.setStyle("-fx-text-fill: -theme-text-color; -fx-font-weight: normal;");
    }
  }

  /** Usage: disable feature box, print winner on auction win */
  public void handleAuctionEndEvent(String winnerName) {
    bidderFeatureBox.setDisable(true);
    remainingTimeLabel.setText("00:00");
    placeBidErrorBox.setText("PHIÊN KẾT THÚC. NGƯỜI THẮNG: " + winnerName);
  }

  /** Usage: initialize chart layout */
  private void setupChartLayout(Item item) {
    priceChart.setTitle("Auction price for Item " + item.getItemName());
    priceChartXAxis.setLabel("Time");
    priceChartYAxis.setLabel("Price");
    priceChartXAxis.setAutoRanging(false);
    priceChartYAxis.setAutoRanging(false);
    // TODO: track based on time. HALF COMPLETED, NEED FETCHIN TIME LINKED WITH ITEM
    // X label format based on time
    priceChartXAxis.setTickLabelFormatter(new ChartTimeLabelFormatter(priceChartXAxis));
    updateChartBounds();
  }

  /** Usage: update chart bounds to fit price */
  private void updateChartBounds() {
    if (currentItem == null) return;
    priceChartYAxis.setLowerBound(0);
    BigDecimal updatedPrice =
        CurrencySelectorHandler.getInstance().getConvertedPrice(currentItem.getCurrentPrice());
    double uproundedMaxPrice = MiscTools.roundUp(updatedPrice.doubleValue());
    priceChartYAxis.setUpperBound(uproundedMaxPrice);
    priceChartYAxis.setTickUnit(uproundedMaxPrice / 10);
  }

  /** Usage: update UI when new bid is pushed */
  private void updateUiFromBidStatus(BidStatus.bidStatus status) {
    switch (status) {
      case SUCCESS -> {
        placeBidErrorBox.setStyle("-fx-text-fill: green;");
        placeBidErrorBox.setText("Đặt giá thành công!");
        placeBidBox.clear();
      }
      case INVALID -> placeBidErrorBox.setText("Giá chưa đạt bước giá tối thiểu quy định!");
      case ALREADY_HIGHEST -> placeBidErrorBox.setText("Bạn đang là người giữ giá cao nhất!");
      default -> placeBidErrorBox.setText("Phiên đấu giá chưa bắt đầu");
    }
  }

  // TODO: work on these
  @FXML
  public void enableAutoBid(ActionEvent event) {}

  @FXML
  public void stopAutoBid(ActionEvent event) {}
}
