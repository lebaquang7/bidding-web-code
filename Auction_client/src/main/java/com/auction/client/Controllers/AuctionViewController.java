package com.auction.client.Controllers;

import java.math.BigDecimal;

import com.auction.client.Models.AccountEventHandler;
import com.auction.client.Models.CurrencySelectorHandler;
import com.auction.client.Models.ItemsEventHandler;
import com.auction.client.Models.LabelHandler;
import com.auction.client.Models.MiscTools;
import com.auction.client.Models.TestChartData;
import com.auction.shared.models.BidStatus;
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

public class AuctionViewController implements SceneController.ItemLoadable {
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

  public void auctionViewGoBackToList(ActionEvent event) {
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
      } else if (result == BidStatus.bidStatus.EXPIRED) {
        auctionViewPlaceBidErrorBox.setText("Phiên đấu giá này đã kết thúc!");
      } else {
        auctionViewPlaceBidErrorBox.setText("Giao dịch bị từ chối, vui lòng thử lại.");
      }

    } catch (NumberFormatException e) {
      auctionViewPlaceBidErrorBox.setText("Vui lòng nhập đúng định dạng số tiền!");
    }
  }

  // TODO: work on these
  public void auctionViewEnableAutoBid(ActionEvent event) {
  }

  public void auctionViewStopAutoBid(ActionEvent event) {
  }

  @Override
  public void setItem(Item item) {
    this.currentItem = Inventory.getItemById(item.getId());
    if (this.currentItem == null)
      this.currentItem = item; // Phòng hờ nếu Inventory rỗng

    this.currentItem
        .currentPriceProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              Platform.runLater(
                  () -> {
                    if (newVal != null) {
                      CurrencySelectorHandler.bindPriceLabel(auctionViewCurrentBid, newVal);
                      ;

                      LabelHandler.scaleFontSizeToFit(auctionViewCurrentBid, 20, 12, 8, 1);
                      System.out.println("UI đã cập nhật giá mới: " + newVal);
                    }
                  });
            });

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

    // TODO: track based on time
    auctionViewPriceChartXAxis.setLowerBound(0);
    auctionViewPriceChartXAxis.setUpperBound(150);

    auctionViewPriceChartYAxis.setLowerBound(item.getStartingPrice().doubleValue());

    AuctionViewController.updatePrice(item, auctionViewPriceChartYAxis);
    auctionViewPriceChart.setData(TestChartData.getSalesData(item));

    CurrencySelectorHandler.getInstance()
        .getActiveCurrencyObjectProperty()
        .addListener(
            (observable, oldVal, newVal) -> {
              AuctionViewController.updatePrice(item, auctionViewPriceChartYAxis);
              auctionViewPriceChart.setData(TestChartData.getSalesData(item));
            });

    item.currentPriceProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              AuctionViewController.updatePrice(item, auctionViewPriceChartYAxis);
              auctionViewPriceChart.setData(TestChartData.getSalesData(item));
            });
  }

  /**
   * Usage: update Y axis's chart position based on price.
   *
   * @param item
   * @param yAxis
   */
  public static void updatePrice(Item item, NumberAxis yAxis) {
    System.out.println(item.getCurrentPrice().doubleValue());
    yAxis.setLowerBound(
        CurrencySelectorHandler.getInstance()
            .getConvertedPrice(item.getStartingPrice())
            .doubleValue());
    BigDecimal updatedPrice = CurrencySelectorHandler.getInstance().getConvertedPrice(item.getCurrentPrice());
    yAxis.setUpperBound(MiscTools.roundUp(updatedPrice.doubleValue()));
    yAxis.setTickUnit(MiscTools.roundUp(updatedPrice.doubleValue()) / 10);
  }
}
