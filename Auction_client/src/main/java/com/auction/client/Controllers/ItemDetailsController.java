package com.auction.client.Controllers;

import com.auction.client.Models.CurrencySelectorHandler;
import com.auction.client.Models.LabelHandler;
import com.auction.shared.models.Item;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ItemDetailsController implements SceneController.ItemLoadable{


    @FXML Label itemDetailsID;
    @FXML Label itemDetailsItemName;
    @FXML Label itemDetailsDescription;
    @FXML Label itemDetailsInitialPrice;
    @FXML Label itemDetailsCurrentPrice;
    @FXML Label itemDetailsBidsMade;
    @FXML Label itemDetailsBidders;
    @FXML Label itemDetailsLastBid;
    @FXML Label itemDetailsViewers;

    private Item currentItem;

    public void itemDetailsGoBackToList(ActionEvent event){
        SceneController.closeScene(event);
    }

    //TODO: have to track based on real time price changes. same with other item labels that actively changes
    @Override
    public void setItem(Item item){
        this.currentItem=item;
        itemDetailsID.setText(currentItem.getId());
        LabelHandler.setDetailedTooltip(itemDetailsID);

        itemDetailsItemName.setText(currentItem.getItemName());
        LabelHandler.setDetailedTooltip(itemDetailsItemName);

        itemDetailsDescription.setText(currentItem.getDescription());
        LabelHandler.setDetailedTooltip(itemDetailsDescription);

        CurrencySelectorHandler.bindPriceLabel(itemDetailsInitialPrice, currentItem.getStartingPrice());
        LabelHandler.scaleFontSizeToFit(itemDetailsInitialPrice, 15, 12, 10, 1);

        CurrencySelectorHandler.bindPriceLabel(itemDetailsCurrentPrice, currentItem.getCurrentPrice());
        LabelHandler.scaleFontSizeToFit(itemDetailsCurrentPrice, 15, 12, 10, 1);
    }
}
