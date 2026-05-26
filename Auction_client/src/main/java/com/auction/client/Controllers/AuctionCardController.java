package com.auction.client.Controllers;

import com.auction.client.Models.CurrencySelectorHandler;
import com.auction.client.Models.LabelHandler;
import com.auction.shared.models.Item;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.application.Platform;

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

        item.currentPriceProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                if (newVal != null) {
                    // Tự động cập nhật lại nhãn giá trên thẻ khi có người trả giá cao hơn
                    CurrencySelectorHandler.bindPriceLabel(mainMenuAuctionCardPriceLabel, newVal);
                    LabelHandler.scaleFontSizeToFit(mainMenuAuctionCardPriceLabel, 20, 12, 10, 1);
                }
            });
        });
    }

    public void mainMenuAuctionCardGoToItemDetails(ActionEvent event){
        SceneController.switchToItemView("/com/auction/client/views/itemDetails_view.fxml", event, currentItem);
    }
    //TODO: restrict GoToAuction for only bidder users.
    public void mainMenuAuctionCardGoToAuction(ActionEvent event){
        SceneController.switchToItemView("/com/auction/client/views/auction_view.fxml", event, currentItem);
    }
}
