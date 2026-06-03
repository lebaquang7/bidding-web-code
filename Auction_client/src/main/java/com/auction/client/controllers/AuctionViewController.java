package com.auction.client.controllers;

import com.auction.client.MainApp;
import com.auction.client.services.AccountEventHandler;
import com.auction.client.services.AuctionBiddingService;
import com.auction.client.services.AutoBidManager;
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
  // Controller class cho màn hình đấu giá.
  // Đường dẫn đến view của controller này
  private static final String PATH_TO_VIEW = "/com/auction/client/views/auction_view.fxml";

  public static String getPATH_TO_VIEW() {
    return PATH_TO_VIEW;
  }

  @FXML VBox bidderFeatureBox;
  @FXML Label itemNameLabel;
  @FXML Label startingBidLabel;
  @FXML Label currentBidLabel;
  @FXML Label remainingTimeLabel;
  @FXML Label placeBidErrorBox;
  @FXML Label autoBidderErrorBox;
  @FXML Label highestBidderLabel;

  @FXML TextField placeBidBox;
  @FXML TextField autoBidderMaxBidBox;
  @FXML TextField autoBidderBidIncrementBox;

  @FXML LineChart<Number, Number> priceChart;
  @FXML NumberAxis priceChartXAxis;
  @FXML NumberAxis priceChartYAxis;

  private Item currentItem;

  public void initialize() {
    // Tắt phần màn hình cho chức năng đấu giá nếu người dùng không phải là bidder
    if (!(AccountEventHandler.getCurrentUser() instanceof Bidder)) {
      UIElementHandler.disableElement(bidderFeatureBox);
    }
  }

  /**
   * Usage: Đóng màn hình khi nhần nút
   *
   * @param event
   */
  @FXML
  public void goBackToList(ActionEvent event) {
    if (MainApp.getNotificationListener() != null) {
      MainApp.getNotificationListener().stopListener();
      MainApp.setNotificationListener(null);
    }
    SceneHandler.closeScene(event);
  }

  /**
   * Usage: Đặt bid khi nhấn nút.
   *
   * @param event
   */
  @FXML
  public void placeBid(ActionEvent event) {
    // Xóa thông báo cũ
    placeBidErrorBox.setStyle("-fx-text-fill: red;");
    placeBidErrorBox.setText("");

    String bidInput = placeBidBox.getText();

    // Kiểm tra rỗng
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

      // Lấy thông tin User hiện tại đang đăng nhập
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

      // Xử lý UI dựa trên phản hồi của Server
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
    this.currentItem = Inventory.getItemById(item.getId());

    if (this.currentItem != null) {
      this.currentItem = item;
    }

    // Nối label hiển thị giá với bộ chuyển đổi giá theo đơn vị tiền tệ
    CurrencySelectorHandler.bindPriceLabel(currentBidLabel, item.getCurrentPrice());

    // Khởi động label tên
    itemNameLabel.setText(currentItem.getItemName());
    LabelHandler.setDetailedTooltip(itemNameLabel);
    // Khởi động label thời gian
    remainingTimeLabel.setText("00:00");
    remainingTimeLabel.setStyle("-fx-text-fill: -theme-text-color; -fx-font-weight: normal;");

    // Nối label với bộ chuyển đổi tiền, tự đổi kích cỡ label nếu ko vừa
    CurrencySelectorHandler.bindPriceLabel(startingBidLabel, currentItem.getStartingPrice());
    LabelHandler.scaleFontSizeToFit(startingBidLabel, 20, 12, 8, 1);
    CurrencySelectorHandler.bindPriceLabel(currentBidLabel, currentItem.getCurrentPrice());
    LabelHandler.scaleFontSizeToFit(currentBidLabel, 20, 12, 8, 1);

    String bidderName = item.getHighestBidderName();
    if (bidderName == null || bidderName.isEmpty()) {
      highestBidderLabel.setText("------");
    } else {
      highestBidderLabel.setText(bidderName);
    }
    LabelHandler.scaleFontSizeToFit(highestBidderLabel, 20, 12, 8, 1);

    // Setup biểu đồ tg thực
    setupChartLayout(item);

    // Cập nhật biểu đồ dựa theo giá
    AuctionViewController.updateChartPrice(item, priceChartYAxis);

    // Listener cho thay đổi đơn vị tiền tệ
    CurrencySelectorHandler.getInstance()
        .getActiveCurrencyObjectProperty()
        .addListener(
            (observable, oldVal, newVal) -> {
              updateChartBounds();
              updateChart();
            });

    // Listener cho giá thành sản phẩm
    item.currentPriceProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              Platform
                  .runLater( // Sử dụng Platform.runLater() để xử lý những phần có thay đổi riêng từ
                      // server
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
   * Usage: Thay đổi biên Y của biểu đồ dựa trên giá hiện tại
   *
   * @param item Sản phẩm
   * @param yAxis Trục Y
   */
  public static void updateChartPrice(Item item, NumberAxis yAxis) {
    yAxis.setLowerBound(0);
    BigDecimal updatedPrice =
        CurrencySelectorHandler.getInstance().getConvertedPrice(item.getCurrentPrice());
    yAxis.setUpperBound(MiscTools.roundUp(updatedPrice.doubleValue()));
    yAxis.setTickUnit(MiscTools.roundUp(updatedPrice.doubleValue()) / 10);
  }

  /**
   * Usage: Thay đổi biên X của biểu đồ dựa trên danh sách lịch sử phiên đấu giá
   *
   * @param bidHistory Danh sách lịch sử BidTransaction
   * @param xAxis Trục X
   */
  public static void updateChartTime(List<BidTransaction> bidHistory, NumberAxis xAxis) {
    if (bidHistory == null || bidHistory.isEmpty()) {
      return;
    }

    // Lấy thời gian từ bidHistory
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

    // Trong trường hợp lower bound = upper bound thì thay đổi biên để vừa màn hình
    if (lowerBound == upperBound) {
      upperBound += 60;
    }

    xAxis.setLowerBound(lowerBound);
    xAxis.setUpperBound(upperBound);
    xAxis.setTickUnit(Math.max(1, (upperBound - lowerBound) / 5));
  }

  /**
   * Usage: Gửi notification đến auction bidding service để xử lý
   *
   * @param message
   */
  public void handleNotification(Object message) {
    Platform.runLater(
        () -> {
          AuctionBiddingService.processIncomingNotification(message, currentItem, this);

          if (message instanceof BidTransaction) {
            AutoBidManager.wakeUpWorker();
          }
        });
  }

  /**
   * Usage: cập nhật hiển thị cho label thời gian còn lại
   *
   * @param totalSeconds thời gian còn lại
   */
  public void updateRemainingTime(int totalSeconds) {
    remainingTimeLabel.setText(MiscTools.formatSecondsToMinutes(totalSeconds));
    if (totalSeconds <= 10) {
      remainingTimeLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    } else {
      remainingTimeLabel.setStyle("-fx-text-fill: -theme-text-color; -fx-font-weight: normal;");
    }
  }

  public void updateHighestBidderUI(String bidderName) {
    Platform.runLater(() -> {
      if (highestBidderLabel != null) {
        String displayName = (bidderName != null && !bidderName.isEmpty())
                ? bidderName
                : "------";
        highestBidderLabel.setText(displayName);
      }
    });
  }
  /**
   * Usage: Ghi người thắng khi phiên kết thúc.
   *
   * @param winnerName tên người thắng
   */
  public void handleAuctionEndEvent(String winnerName) {
    remainingTimeLabel.setText("00:00");
    placeBidErrorBox.setText(winnerName + " đã thắng phiên đấu giá.");
  }

  /**
   * Usage: Khởi động nội dung biểu đồ
   *
   * @param item
   */
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

  /** Usage: cập nhật biên của biểu đồ để chứa giá sản phẩm */
  private void updateChartBounds() {
    if (currentItem == null) return;
    priceChartYAxis.setLowerBound(0);
    BigDecimal updatedPrice =
        CurrencySelectorHandler.getInstance().getConvertedPrice(currentItem.getCurrentPrice());
    double uproundedMaxPrice = MiscTools.roundUp(updatedPrice.doubleValue());
    priceChartYAxis.setUpperBound(uproundedMaxPrice);
    priceChartYAxis.setTickUnit(uproundedMaxPrice / 10);
  }

  /** Usage: Cập nhật biểu đồ */
  private void updateChart() {
    List<BidTransaction> bidHistory = ItemsEventHandler.fetchBidTransactionsForItem(currentItem);
    updateChartTime(bidHistory, priceChartXAxis);
    ObservableList<XYChart.Series<Number, Number>> chartData =
        ChartDataHandler.setChartDisplay(bidHistory);
    priceChart.setData(chartData);
  }

  /**
   * Usage: Cập nhật UI khi có bid mới được đẩy
   *
   * @param status
   */
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

  /**
   * Usage: Khởi động trình auto bid khi nhấn nút
   *
   * @param event
   */
  @FXML
  public void enableAutoBid(ActionEvent event) {
    try {
      BigDecimal maxBidVND =
          CurrencySelectorHandler.getInstance()
              .getVNDPrice(new BigDecimal(autoBidderMaxBidBox.getText()));
      BigDecimal incrementVND =
          CurrencySelectorHandler.getInstance()
              .getVNDPrice(new BigDecimal(autoBidderBidIncrementBox.getText()));
      User currentUser = AccountEventHandler.getCurrentUser();

      if (currentUser == null || currentItem == null) return;

      AutoBidManager.AutoBidState result =
          AutoBidManager.startAutoBid(currentItem, currentUser, maxBidVND, incrementVND);

      autoBidderErrorBox.setText(result.getMessage());
      autoBidderErrorBox.setStyle("-fx-text-fill: " + result.getStyleClass() + ";");

      autoBidderMaxBidBox.setDisable(true);
      autoBidderBidIncrementBox.setDisable(true);

    } catch (NumberFormatException e) {
      autoBidderErrorBox.setStyle("-fx-text-fill: red;");
      autoBidderErrorBox.setText("Vui lòng nhập số hợp lệ!");
    }
  }

  /**
   * Usage: Dừng auto bid khi nhấn nút
   *
   * @param event
   */
  @FXML
  public void stopAutoBid(ActionEvent event) {
    AutoBidManager.stopAutoBid();

    autoBidderErrorBox.setStyle("-fx-text-fill: blue;");
    autoBidderErrorBox.setText("Đã tắt hệ thống Auto-Bid.");

    autoBidderMaxBidBox.setDisable(false);
    autoBidderBidIncrementBox.setDisable(false);
  }
}
