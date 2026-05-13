package com.auction.client.Controllers;

import com.auction.server.models.Item;

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
        mainMenuAuctionCardPriceLabel.setText(Double.toString(item.getCurrentPrice())+" $");
    }

    public void mainMenuAuctionCardGoToItemDetails(ActionEvent event){
        SceneController.switchToItemView("/com/auction/client/views/itemDetails_view.fxml", event, currentItem);
    }
    //TODO: restrict GoToAuction for only bidder users.
    public void mainMenuAuctionCardGoToAuction(ActionEvent event){
        SceneController.switchToItemView("/com/auction/client/views/auction_view.fxml", event, currentItem);
    }
}
