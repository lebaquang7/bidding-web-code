package com.auction.client.Controllers;

import java.math.BigDecimal;

import com.auction.client.Models.AccountEventHandler;
import com.auction.client.Models.CurrencySelectorHandler;
import com.auction.client.Models.ItemsEventHandler;
import com.auction.client.Models.LabelHandler;
import com.auction.client.Models.MiscTools;
import com.auction.client.Models.TestChartData;
import com.auction.shared.models.BidStatus;
import com.auction.shared.models.Item;
import com.auction.shared.models.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AuctionViewController implements SceneController.ItemLoadable{
    @FXML Label auctionViewItemName;
    @FXML Label auctionViewStartingBid;
    @FXML Label auctionViewCurrentBid;
    @FXML Label auctionViewRemainingTime;
    @FXML Label auctionViewPlaceBidErrorBox;
    @FXML Label auctionViewAutoBidderErrorBox;

    @FXML TextField auctionViewPlaceBidBox;
    @FXML TextField auctionViewAutoBidderMaxBidBox;
    @FXML TextField auctionViewAutoBidderBidIncrementBox;

    @FXML LineChart<Number,Number> auctionViewPriceChart;
    @FXML NumberAxis auctionViewPriceChartXAxis;
    @FXML NumberAxis auctionViewPriceChartYAxis;
    
    private Item currentItem;
    
    public void auctionViewGoBackToList(ActionEvent event){
        SceneController.closeScene(event);
    }

    @FXML
    public void auctionViewPlaceBid(ActionEvent event) {
        // 1. Xóa thông báo lỗi/thành công cũ (reset màu chữ về đỏ mặc định)
        auctionViewPlaceBidErrorBox.setStyle("-fx-text-fill: red;");
        auctionViewPlaceBidErrorBox.setText("");

        String bidInput = auctionViewPlaceBidBox.getText();

        // 2. Kiểm tra rỗng
        if (bidInput == null || bidInput.trim().isEmpty()) {
            auctionViewPlaceBidErrorBox.setText("Vui lòng nhập số tiền muốn trả!");
            return;
        }

        try {
            BigDecimal bidAmount = new BigDecimal(bidInput);

            // 3. Kiểm tra logic cơ bản ở Client (Giảm tải cho Server)
            if (bidAmount.compareTo(currentItem.getCurrentPrice()) <= 0) {
                auctionViewPlaceBidErrorBox.setText("Giá trả phải lớn hơn giá hiện tại!");
                return;
            }

            // 4. Lấy thông tin User hiện tại đang đăng nhập
            User currentUser = AccountEventHandler.getCurrentUser();
            if (currentUser == null) {
                auctionViewPlaceBidErrorBox.setText("Lỗi: Không tìm thấy thông tin phiên đăng nhập!");
                return;
            }

            // 5. Gửi Request lên Server thông qua Handler
            // Trả về enum BidStatus từ Server
            BidStatus.bidStatus result = ItemsEventHandler.placeBid(currentItem.getId(), currentUser.getId(), bidAmount);

            // 6. Xử lý UI dựa trên phản hồi của Server
            if (result == BidStatus.bidStatus.SUCCESS) {
                auctionViewPlaceBidErrorBox.setStyle("-fx-text-fill: green;");
                auctionViewPlaceBidErrorBox.setText("Đặt giá thành công!");
                auctionViewPlaceBidBox.clear();

                // Lưu ý: Label currentBid sẽ tự nhảy số khi bạn làm xong phần Real-time Broadcast.
                // Tạm thời chưa cần gọi update Label ở đây để tránh bị lệch dữ liệu nếu Server chưa lưu.

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

    //TODO: work on these
    public void auctionViewEnableAutoBid(ActionEvent event){}
    public void auctionViewStopAutoBid(ActionEvent event){}

    @Override
    public void setItem(Item item){
        this.currentItem=item;

        //init labels
        auctionViewItemName.setText(currentItem.getItemName());
        LabelHandler.setDetailedTooltip(auctionViewItemName);

        CurrencySelectorHandler.bindPriceLabel(auctionViewStartingBid, currentItem.getStartingPrice());
        LabelHandler.scaleFontSizeToFit(auctionViewStartingBid, 20, 12, 8, 1);

        CurrencySelectorHandler.bindPriceLabel(auctionViewCurrentBid, currentItem.getCurrentPrice());
        LabelHandler.scaleFontSizeToFit(auctionViewCurrentBid, 20, 12, 8, 1);

        //init chart
        //TODO: link chart with actual infos.
        auctionViewPriceChart.setTitle("Auction price for Item "+item.getItemName());
        auctionViewPriceChartXAxis.setLabel("Time");
        auctionViewPriceChartYAxis.setLabel("Price");

        //toggle off auto ranging so one can manually set bounds. bounds will be linked with observable to track price.
        auctionViewPriceChartXAxis.setAutoRanging(false);
        auctionViewPriceChartYAxis.setAutoRanging(false);

        //TODO: track based on time
        auctionViewPriceChartXAxis.setLowerBound(0);
        auctionViewPriceChartXAxis.setUpperBound(150);


        auctionViewPriceChartYAxis.setLowerBound(item.getStartingPrice().doubleValue());
        CurrencySelectorHandler.getInstance().getActiveCurrencyObjectProperty().addListener((observable, oldVal, newVal) -> {
            BigDecimal currentPrice = CurrencySelectorHandler.getInstance().getConvertedPrice(item.getCurrentPrice());
            auctionViewPriceChartYAxis.setUpperBound(MiscTools.roundUp(currentPrice.doubleValue()));
            auctionViewPriceChartYAxis.setTickUnit(MiscTools.roundUp(currentPrice.doubleValue()/10));
        });
        //TODO: add observer for price. also to do this with other infos that dynamically changes.
        auctionViewPriceChartYAxis.setLowerBound(item.getStartingPrice().doubleValue());
        auctionViewPriceChartYAxis.setUpperBound(item.getCurrentPrice().doubleValue());

        //TODO: tick mark auto adjust based on lower and upper bound
        auctionViewPriceChartYAxis.setTickUnit(500000);

        auctionViewPriceChart.setData(TestChartData.getSalesData(item));
    }
}
