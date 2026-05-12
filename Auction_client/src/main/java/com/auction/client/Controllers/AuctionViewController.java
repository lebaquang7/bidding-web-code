package com.auction.client.Controllers;

import com.auction.server.models.Item;

import javafx.event.ActionEvent;

public class AuctionViewController implements SceneController.ItemLoadable{
    private Item currentItem;
    
    public void auctionViewGoBackToList(ActionEvent event){
        SceneController.closeScene(event);
    }

    @Override
    public void setItem(Item item){
        this.currentItem=item;
    }
}
