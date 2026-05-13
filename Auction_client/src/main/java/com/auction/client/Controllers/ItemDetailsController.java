package com.auction.client.Controllers;

import com.auction.shared.models.Item;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ItemDetailsController{


    @FXML Label itemDetailsItemName;
    @FXML Label itemDetailsItemPrice;

    private Item currentItem;

    public void itemDetailsGoBackToList(ActionEvent event){
        SceneController.switchToScene(getClass().getResource("/com/auction/client/views/mainMenu_view.fxml"), event);
    }

    public void setItem(Item item){
        this.currentItem=item;
        itemDetailsItemName.setText(currentItem.getItemName());
        itemDetailsItemPrice.setText(Double.toString(currentItem.getCurrentPrice())+" $");
    }
}
