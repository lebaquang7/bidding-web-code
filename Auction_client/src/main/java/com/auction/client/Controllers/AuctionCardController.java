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
        /*
        SceneController.switchToItemView("/com/auction/client/views/itemDetails_view.fxml", event, currentItem);
        */
        showDetailPopup(currentItem);
    }
    //TODO: restrict GoToAuction for only bidder users.
    public void mainMenuAuctionCardGoToAuction(ActionEvent event){
        SceneController.switchToItemView("/com/auction/client/views/auction_view.fxml", event, currentItem);
    }

    private void showDetailPopup(Item item) {
        // 1. Tạo cửa sổ Popup
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

        // Tách thông tin đặc trưng tùy loại sản phẩm dựa trên OOP Kế thừa
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

        // Thêm nút Đóng popup
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
