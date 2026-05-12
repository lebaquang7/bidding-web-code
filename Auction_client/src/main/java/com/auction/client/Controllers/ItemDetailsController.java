package com.auction.client.Controllers;

import com.auction.server.models.Item;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ItemDetailsController implements SceneController.ItemLoadable{


    @FXML Label itemDetailsItemName;
    @FXML Label itemDetailsItemPrice;

    private Item currentItem;

    public void itemDetailsGoBackToList(ActionEvent event){
        SceneController.closeScene(event);
    }

    @Override
    public void setItem(Item item){
        this.currentItem=item;
        itemDetailsItemName.setText(currentItem.getItemName());
        itemDetailsItemPrice.setText(Double.toString(currentItem.getCurrentPrice())+" $");
    }
}
