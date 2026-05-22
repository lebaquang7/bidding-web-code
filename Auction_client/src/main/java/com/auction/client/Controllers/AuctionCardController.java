package com.auction.client.Controllers;

import com.auction.client.Models.CurrencySelectorHandler;
import com.auction.client.Models.LabelHandler;
import com.auction.shared.models.Item;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class AuctionCardController {

    @FXML Button mainMenuAuctionCardGoToAuctionButton;
    @FXML Label mainMenuAuctionCardNameLabel;
    @FXML Label mainMenuAuctionCardPriceLabel;
    @FXML ImageView mainMenuAuctionCardImageView;

    //each auction card holds the current item
    private Item currentItem;

    public void setData(Item item){
        currentItem=item;
        
        mainMenuAuctionCardNameLabel.setText(item.getItemName());
        LabelHandler.setDetailedTooltip(mainMenuAuctionCardNameLabel);

        CurrencySelectorHandler.bindPriceLabel(mainMenuAuctionCardPriceLabel, item.getCurrentPrice());
        LabelHandler.scaleFontSizeToFit(mainMenuAuctionCardPriceLabel, 20, 12, 10, 1);
    }

    public void mainMenuAuctionCardGoToItemDetails(ActionEvent event){
        showDetailPopup(currentItem);
    }
    //TODO: restrict GoToAuction for only bidder users.
    public void mainMenuAuctionCardGoToAuction(ActionEvent event){
        SceneController.switchToItemView("/com/auction/client/views/auction_view.fxml", event, currentItem);
    }

    private void showDetailPopup(Item item) {
        // Tạo cửa sổ Popup
        javafx.stage.Stage detailStage = new javafx.stage.Stage();
        detailStage.setTitle("Chi tiết Sản phẩm");
        detailStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        // Tạo khung chứa (VBox)
        javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(15);
        root.setPadding(new javafx.geometry.Insets(25));
        root.setStyle("-fx-background-color: white; -fx-font-size: 15px;");

        // Tạo các nhãn thông tin chung
        javafx.scene.control.Label titleLabel = new javafx.scene.control.Label(item.getItemName());
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        javafx.scene.control.Label descLabel = new javafx.scene.control.Label("Mô tả: " + item.getDescription());
        descLabel.setWrapText(true);

        javafx.scene.control.Label priceLabel = new javafx.scene.control.Label("Giá hiện tại: " + item.getCurrentPrice() + " VND");
        priceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #d35400;");

        javafx.scene.control.Label incLabel = new javafx.scene.control.Label("Bước giá: " + item.getPriceIncrement() + " VND");

        root.getChildren().addAll(titleLabel, new javafx.scene.control.Separator(), descLabel, priceLabel, incLabel, new javafx.scene.control.Separator());

        if (item instanceof com.auction.shared.models.Art) {
            com.auction.shared.models.Art art = (com.auction.shared.models.Art) item;
            root.getChildren().addAll(
                    new javafx.scene.control.Label(" Họa sĩ: " + art.getArtistName()),
                    new javafx.scene.control.Label(" Năm sáng tác: " + art.getCreationYear()),
                    new javafx.scene.control.Label(" Chất liệu: " + art.getMedium()),
                    new javafx.scene.control.Label(" Bản gốc: " + (art.getIsOriginal() ? "Có" : "Không"))
            );
        } else if (item instanceof com.auction.shared.models.Electronics) {
            com.auction.shared.models.Electronics elec = (com.auction.shared.models.Electronics) item;
            root.getChildren().addAll(
                    new javafx.scene.control.Label(" Hãng SX: " + elec.getBrand()),
                    new javafx.scene.control.Label(" Model: " + elec.getModel()),
                    new javafx.scene.control.Label(" Bảo hành: " + elec.getWarrantyMonths() + " tháng"),
                    new javafx.scene.control.Label(" Tình trạng: " + elec.getCondition())
            );
        } else if (item instanceof com.auction.shared.models.Vehicle) {
            com.auction.shared.models.Vehicle veh = (com.auction.shared.models.Vehicle) item;
            root.getChildren().addAll(
                    new javafx.scene.control.Label(" Biển số: " + veh.getLicensePlate()),
                    new javafx.scene.control.Label(" Số KM đã đi: " + veh.getMileage() + " km"),
                    new javafx.scene.control.Label(" Năm sản xuất: " + veh.getManufacturingYear())
            );
        }

        javafx.scene.layout.VBox autoBidBox = new javafx.scene.layout.VBox(10);
        autoBidBox.setStyle("-fx-border-color: #3498db; -fx-border-width: 2px; -fx-padding: 10; -fx-border-radius: 5;");
        javafx.scene.control.Label abTitle = new javafx.scene.control.Label(" Chế độ Auto-Bidding");
        abTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #3498db;");

        javafx.scene.control.TextField maxBidInput = new javafx.scene.control.TextField();
        maxBidInput.setPromptText("Nhập giá tối đa bạn trả");
        javafx.scene.control.TextField incrementInput = new javafx.scene.control.TextField();
        incrementInput.setPromptText("Nhập bước giá nhảy");

        javafx.scene.control.Button btnSaveAutoBid = new javafx.scene.control.Button("Bật tự động trả giá");
        btnSaveAutoBid.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");

        btnSaveAutoBid.setOnAction(event -> {
            try {
                double max = Double.parseDouble(maxBidInput.getText());
                double inc = Double.parseDouble(incrementInput.getText());

                com.auction.shared.models.BidTransaction autoBidData = new com.auction.shared.models.BidTransaction(
                        item.getCurrentPrice().doubleValue(),
                        java.time.LocalDateTime.now(),
                        null,
                        null
                );

                autoBidData.setMaxBid(max);
                autoBidData.setIncrement(inc);

                autoBidData.setTempUsername("Bot_Cua_Toi");
                autoBidData.setTempAuctionId("AUC_123");

                com.auction.shared.models.NetworkRequest request = new com.auction.shared.models.NetworkRequest(
                        com.auction.shared.models.NetworkRequest.requestType.Bid,
                        autoBidData
                );

                System.out.println(" Đã gửi cấu hình Auto-Bid lên Server!");
                detailStage.close();
            } catch (Exception ex) {
                maxBidInput.setText("");
                maxBidInput.setPromptText("Lỗi: Vui lòng nhập số! ");
            }
        });
        autoBidBox.getChildren().addAll(abTitle, maxBidInput, incrementInput, btnSaveAutoBid);
        root.getChildren().add(autoBidBox);

        // Thêm nút đóng popup
        javafx.scene.control.Button closeBtn = new javafx.scene.control.Button("Đóng");
        closeBtn.setOnAction(e -> detailStage.close());
        closeBtn.setStyle("-fx-background-color: #bdc3c7; -fx-text-fill: black; -fx-cursor: hand;");
        root.getChildren().add(closeBtn);

        // Hiển thị ra màn hình
        javafx.scene.Scene scene = new javafx.scene.Scene(root, 400, 450);
        detailStage.setScene(scene);
        detailStage.show();
    }
}
