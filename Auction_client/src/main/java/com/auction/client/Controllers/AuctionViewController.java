package com.auction.client.controllers;

import java.math.BigDecimal;

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

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class AuctionViewController implements SceneHandler.ItemLoadable {

  @FXML
  VBox auctionViewBidderFeatureBox;
  @FXML
  Label auctionViewItemName;
  @FXML
  Label auctionViewStartingBid;
  @FXML
  Label auctionViewCurrentBid;
  @FXML
  Label auctionViewRemainingTime;
  @FXML
  Label auctionViewPlaceBidErrorBox;
  @FXML
  Label auctionViewAutoBidderErrorBox;

  @FXML
  TextField auctionViewPlaceBidBox;
  @FXML
  TextField auctionViewAutoBidderMaxBidBox;
  @FXML
  TextField auctionViewAutoBidderBidIncrementBox;

  @FXML
  LineChart<Number, Number> auctionViewPriceChart;
  @FXML
  NumberAxis auctionViewPriceChartXAxis;
  @FXML
  NumberAxis auctionViewPriceChartYAxis;

  private Item currentItem;

  public void initialize() {
    if (!(AccountEventHandler.getCurrentUser() instanceof Bidder)) {
      UIElementHandler.disableElement(auctionViewBidderFeatureBox);
    }
  }

  public void auctionViewGoBackToList(ActionEvent event) {
    if (MainApp.getNotificationListener() != null) {
      MainApp.getNotificationListener().stopListener();
      MainApp.setNotificationListener(null);
    }
    SceneHandler.closeScene(event);
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
      BigDecimal minBidRequired = currentPrice
          .add(currentPrice.multiply(incrementPercent).divide(new BigDecimal("100")));

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
      BidStatus.bidStatus result = ItemsEventHandler.placeBid(currentItem.getId(), currentUser.getId(), bidAmount);

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

  @Override
  public void setItem(Item item) {
    ClientNotificationListener.setCurrentController(this);
    this.currentItem = Inventory.getItemById(item.getId());
    if (this.currentItem == null) {
      this.currentItem = item;
    } // Phòng hờ nếu Inventory rỗng

    // Hiển thị giá ban đầu
    CurrencySelectorHandler.bindPriceLabel(auctionViewCurrentBid, item.getCurrentPrice());

    // init labels
    auctionViewItemName.setText(currentItem.getItemName());
    LabelHandler.setDetailedTooltip(auctionViewItemName);
    // init time label
    auctionViewRemainingTime.setText("00:00");
    auctionViewRemainingTime.setStyle("-fx-text-fill: -theme-text-color; -fx-font-weight: normal;");

    CurrencySelectorHandler.bindPriceLabel(auctionViewStartingBid, currentItem.getStartingPrice());
    LabelHandler.scaleFontSizeToFit(auctionViewStartingBid, 20, 12, 8, 1);
    CurrencySelectorHandler.bindPriceLabel(auctionViewCurrentBid, currentItem.getCurrentPrice());
    LabelHandler.scaleFontSizeToFit(auctionViewCurrentBid, 20, 12, 8, 1);

    setupChartLayout(item);

    AuctionViewController.updateChartPrice(item, auctionViewPriceChartYAxis);
    // TODO: UNCOMMENT WHEN ACTUAL CHART DATA IS MADE
    // auctionViewPriceChart.setData(ChartDataHandler
    // .setChartDisplay(AuctionManager.getInstance().getAuctionSession(item.getId()).getBidHistory()));

    // listener for currency type changes
    CurrencySelectorHandler.getInstance()
        .getActiveCurrencyObjectProperty()
        .addListener(
            (observable, oldVal, newVal) -> updateChartBounds());

    // Cập nhật currentPrice realtime
    item.currentPriceProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              Platform.runLater(() -> {
                CurrencySelectorHandler.bindPriceLabel(auctionViewCurrentBid, newVal);
                LabelHandler.scaleFontSizeToFit(auctionViewCurrentBid, 20, 12, 8, 1);
                AuctionViewController.updateChartPrice(item, auctionViewPriceChartYAxis);
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
    BigDecimal updatedPrice = CurrencySelectorHandler.getInstance().getConvertedPrice(item.getCurrentPrice());
    yAxis.setUpperBound(MiscTools.roundUp(updatedPrice.doubleValue()));
    yAxis.setTickUnit(MiscTools.roundUp(updatedPrice.doubleValue()) / 10);
  }

  /**
   * Usage: pass on msg to auction bidding service to process
   */
  public void handleNotification(Object message) {
    Platform.runLater(() -> {
      AuctionBiddingService.processIncomingNotification(message, currentItem, this);
    });
  }

  /**
   * Usage: update remaining time label
   */
  public void updateRemainingTime(int totalSeconds) {
    auctionViewRemainingTime.setText(MiscTools.formatSecondsToMinutes(totalSeconds));
    if (totalSeconds <= 10) {
      auctionViewRemainingTime.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    } else {
      auctionViewRemainingTime.setStyle("-fx-text-fill: -theme-text-color; -fx-font-weight: normal;");
    }
  }

  /**
   * Usage: disable feature box, print winner on auction win
   */
  public void handleAuctionEndEvent(String winnerName) {
    auctionViewBidderFeatureBox.setDisable(true);
    auctionViewRemainingTime.setText("00:00");
    auctionViewPlaceBidErrorBox.setText("PHIÊN KẾT THÚC. NGƯỜI THẮNG: " + winnerName);
  }

  /**
   * Usage: initialize chart layout
   */
  private void setupChartLayout(Item item) {
    auctionViewPriceChart.setTitle("Auction price for Item " + item.getItemName());
    auctionViewPriceChartXAxis.setLabel("Time");
    auctionViewPriceChartYAxis.setLabel("Price");
    auctionViewPriceChartXAxis.setAutoRanging(false);
    auctionViewPriceChartYAxis.setAutoRanging(false);
    // TODO: track based on time. HALF COMPLETED, NEED FETCHIN TIME LINKED WITH ITEM
    // X label format based on time
    auctionViewPriceChartXAxis.setTickLabelFormatter(new ChartTimeLabelFormatter(auctionViewPriceChartXAxis));
    updateChartBounds();
  }

  /**
   * Usage: update chart bounds to fit price
   */
  private void updateChartBounds() {
    if (currentItem == null)
      return;
    auctionViewPriceChartYAxis.setLowerBound(0);
    BigDecimal updatedPrice = CurrencySelectorHandler.getInstance().getConvertedPrice(currentItem.getCurrentPrice());
    double uproundedMaxPrice = MiscTools.roundUp(updatedPrice.doubleValue());
    auctionViewPriceChartYAxis.setUpperBound(uproundedMaxPrice);
    auctionViewPriceChartYAxis.setTickUnit(uproundedMaxPrice / 10);
  }

  /**
   * Usage: update UI when new bid is pushed
   */
  private void updateUiFromBidStatus(BidStatus.bidStatus status) {
    switch (status) {
      case SUCCESS -> {
        auctionViewPlaceBidErrorBox.setStyle("-fx-text-fill: green;");
        auctionViewPlaceBidErrorBox.setText("Đặt giá thành công!");
        auctionViewPlaceBidBox.clear();
      }
      case INVALID -> auctionViewPlaceBidErrorBox.setText("Giá chưa đạt bước giá tối thiểu quy định!");
      case ALREADY_HIGHEST -> auctionViewPlaceBidErrorBox.setText("Bạn đang là người giữ giá cao nhất!");
      default -> auctionViewPlaceBidErrorBox.setText("Phiên đấu giá chưa bắt đầu");
    }
  }

  // TODO: work on these
  public void auctionViewEnableAutoBid(ActionEvent event) {
  }

  public void auctionViewStopAutoBid(ActionEvent event) {
  }
}
