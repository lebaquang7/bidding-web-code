package com.auction.client.Controllers;

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

    @Override
    public void setItem(Item item){
        this.currentItem=item;
        itemDetailsID.setText(item.getId());
        itemDetailsItemName.setText(currentItem.getItemName());
        itemDetailsDescription.setText(currentItem.getDescription());
        itemDetailsInitialPrice.setText(Double.toString(item.getStartingPrice()));
        itemDetailsCurrentPrice.setText(Double.toString(currentItem.getCurrentPrice()));
    }
}
