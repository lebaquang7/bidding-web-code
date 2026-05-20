package com.auction.client.Controllers;

import com.auction.client.Models.CurrencySelectorHandler;
import com.auction.client.Models.LabelHandler;
import com.auction.shared.models.Item;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
    
    private Item currentItem;
    
    public void auctionViewGoBackToList(ActionEvent event){
        SceneController.closeScene(event);
    }

    //TODO: work on these
    public void auctionViewPlaceBid(ActionEvent event){}
    public void auctionViewEnableAutoBid(ActionEvent event){}
    public void auctionViewStopAutoBid(ActionEvent event){}

    @Override
    public void setItem(Item item){
        this.currentItem=item;

        auctionViewItemName.setText(currentItem.getItemName());
        LabelHandler.setDetailedTooltip(auctionViewItemName);

        CurrencySelectorHandler.bindPriceLabel(auctionViewStartingBid, currentItem.getStartingPrice());
        LabelHandler.scaleFontSizeToFit(auctionViewStartingBid, 20, 12, 8, 1);

        CurrencySelectorHandler.bindPriceLabel(auctionViewCurrentBid, currentItem.getCurrentPrice());
        LabelHandler.scaleFontSizeToFit(auctionViewCurrentBid, 20, 12, 8, 1);
    }
}
