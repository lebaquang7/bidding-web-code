package com.auction.client.Controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import com.auction.shared.models.Item;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;

public class MainMenuAuctionListPaneController {
    @FXML FlowPane mainMenuAuctionListFlowPane;
    ObservableList<Item> itemList = FXCollections.observableArrayList();

    public void initialize(){
        //Placeholder item
        //TODO: wait for other's works on this, and then link it with their item list
        /*
        itemList.add(new Item("item", "desc", BigDecimal.valueOf(500000.0), BigDecimal.valueOf(9000000.0)) {
        });
        itemList.add(new Item("item2", "desc", BigDecimal.valueOf(200000.0), BigDecimal.valueOf(6000000.0)) {
        });
        itemList.add(new Item("item3", "desc", BigDecimal.valueOf(100000.0), BigDecimal.valueOf(300000.0)) {
        });
        */
        java.util.ArrayList<Item> serverItems = com.auction.client.Models.AccountEventHandler.getAllItems();
        itemList.setAll(serverItems);

        renderItem(itemList);
    }

    /**
     * render list of pane items
     * @param list
     */
    public void renderItem(List<Item> list){
        //clear old panes
        mainMenuAuctionListFlowPane.getChildren().clear();

        for (Item item : list) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/auction/client/views/mainMenu_auctionCard.fxml"));
              
                //load data of the card
                Parent card = loader.load();
                //get loader controller, set data to each item's data
                AuctionCardController controller = loader.getController();
                controller.setData(item);
            
                // add card to flowpane
                mainMenuAuctionListFlowPane.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
