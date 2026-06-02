package com.auction.client.controllers;

import com.auction.client.MainApp;
import com.auction.client.services.AccountEventHandler;
import com.auction.client.services.AuctionBiddingService;
import com.auction.client.services.ClientNotificationListener;
import com.auction.client.services.ItemsEventHandler;
import com.auction.client.services.SceneHandler;
import com.auction.client.utils.ChartDataHandler;
import com.auction.client.utils.ChartTimeLabelFormatter;
import com.auction.client.utils.CurrencySelectorHandler;
import com.auction.client.utils.LabelHandler;
import com.auction.client.utils.MiscTools;
import com.auction.client.utils.UIElementHandler;
import com.auction.shared.models.BidStatus;
import com.auction.shared.models.BidTransaction;
import com.auction.shared.models.Bidder;
import com.auction.shared.models.Inventory;
import com.auction.shared.models.Item;
import com.auction.shared.models.User;
import java.math.BigDecimal;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
      BigDecimal currentPrice =
          CurrencySelectorHandler.getInstance().getConvertedPrice(currentItem.getCurrentPrice());
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
          ItemsEventHandler.placeBid(
              currentItem.getId(),
              currentUser.getId(),
              CurrencySelectorHandler.getInstance().getVNDPrice(bidAmount));
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
      } else if (result == BidStatus.bidStatus.NOT_STARTED) {
        placeBidErrorBox.setText("Phiên đấu giá chưa bắt đầu");
      } else {
        placeBidErrorBox.setText("Phiên đấu giá chưa bắt đầu/ đã kết thúc");
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
    }

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

    // listener for currency type changes
    CurrencySelectorHandler.getInstance()
        .getActiveCurrencyObjectProperty()
        .addListener(
            (observable, oldVal, newVal) -> {
              updateChartBounds();
              updateChart();
            });

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
                    updateChart();
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

  public static void updateChartTime(List<BidTransaction> bidHistory, NumberAxis xAxis) {
    if (bidHistory == null || bidHistory.isEmpty()) {
      return;
    }

    long lowerBound =
        bidHistory.stream()
            .mapToLong(
                bt ->
                    bt.getBidTime()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toInstant()
                        .getEpochSecond())
            .min()
            .getAsLong();

    long upperBound =
        bidHistory.stream()
            .mapToLong(
                bt ->
                    bt.getBidTime()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toInstant()
                        .getEpochSecond())
            .max()
            .getAsLong();

    // prevent empty x axis if bounds are similar
    if (lowerBound == upperBound) {
      lowerBound -= 60;
      upperBound += 60;
    }

    xAxis.setLowerBound(lowerBound);
    xAxis.setUpperBound(upperBound);
    xAxis.setTickUnit(Math.max(1, (upperBound - lowerBound) / 5));
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
    priceChartXAxis.setTickLabelFormatter(new ChartTimeLabelFormatter(priceChartXAxis));
    updateChartBounds();
    updateChart();
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

  private void updateChart() {
    List<BidTransaction> bidHistory = ItemsEventHandler.fetchBidTransactionsForItem(currentItem);
    updateChartTime(bidHistory, priceChartXAxis);
    ObservableList<XYChart.Series<Number, Number>> chartData =
        ChartDataHandler.setChartDisplay(bidHistory);
    priceChart.setData(chartData);
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
  public void enableAutoBid(ActionEvent event) {
    autoBidderErrorBox.setStyle("-fx-text-fill: red;");
    autoBidderErrorBox.setText("");

    String maxBidInput = autoBidderMaxBidBox.getText();
    String incrementInput = autoBidderBidIncrementBox.getText();

    if (maxBidInput == null
        || maxBidInput.trim().isEmpty()
        || incrementInput == null
        || incrementInput.trim().isEmpty()) {
      autoBidderErrorBox.setText("Vui lòng nhập đầy đủ Giá tối đa và Bước giá!");
      return;
    }

    try {
      BigDecimal maxBid = new BigDecimal(maxBidInput);
      BigDecimal increment = new BigDecimal(incrementInput);
      BigDecimal currentPrice =
          CurrencySelectorHandler.getInstance().getConvertedPrice(currentItem.getCurrentPrice());

      if (maxBid.compareTo(currentPrice) <= 0) {
        autoBidderErrorBox.setText("Giá tối đa phải lớn hơn giá hiện tại!");
        return;
      }
      if (increment.compareTo(BigDecimal.ZERO) <= 0) {
        autoBidderErrorBox.setText("Bước giá phải lớn hơn 0!");
        return;
      }

      User currentUser = AccountEventHandler.getCurrentUser();
      if (currentUser == null) {
        autoBidderErrorBox.setText("Lỗi: Không tìm thấy thông tin phiên đăng nhập!");
        return;
      }

      // Convert về VND trước khi gửi xuống Server
      BigDecimal maxBidVND = CurrencySelectorHandler.getInstance().getVNDPrice(maxBid);
      BigDecimal incrementVND = CurrencySelectorHandler.getInstance().getVNDPrice(increment);

      // Gọi qua ItemsEventHandler để ném xuống mạng
      boolean isSuccess =
          ItemsEventHandler.registerAutoBid(
              currentItem.getId(), currentUser.getId(), maxBidVND, incrementVND);

      if (isSuccess) {
        autoBidderErrorBox.setStyle("-fx-text-fill: green;");
        autoBidderErrorBox.setText("Đã kích hoạt Auto-Bid thành công!");
        autoBidderMaxBidBox.setDisable(true);
        autoBidderBidIncrementBox.setDisable(true);
      } else {
        autoBidderErrorBox.setText("Lỗi: Server từ chối cấu hình Auto-Bid này.");
      }
    } catch (NumberFormatException e) {
      autoBidderErrorBox.setText("Vui lòng chỉ nhập số hợp lệ!");
    }
  }

  @FXML
  public void stopAutoBid(ActionEvent event) {
    autoBidderErrorBox.setStyle("-fx-text-fill: red;");
    autoBidderErrorBox.setText("");

    User currentUser = AccountEventHandler.getCurrentUser();
    if (currentUser == null) return;

    boolean isSuccess = ItemsEventHandler.cancelAutoBid(currentItem.getId(), currentUser.getId());

    if (isSuccess) {
      autoBidderErrorBox.setStyle("-fx-text-fill: green;");
      autoBidderErrorBox.setText("Đã tắt hệ thống Auto-Bid.");
      autoBidderMaxBidBox.setDisable(false);
      autoBidderBidIncrementBox.setDisable(false);
      autoBidderMaxBidBox.clear();
      autoBidderBidIncrementBox.clear();
    } else {
      autoBidderErrorBox.setText("Lỗi: Không thể tắt Auto-Bid lúc này.");
    }
  }
}
