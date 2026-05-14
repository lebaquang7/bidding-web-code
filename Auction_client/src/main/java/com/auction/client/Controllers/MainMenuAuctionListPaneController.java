package com.auction.client.Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.auction.shared.models.Item;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;

public class MainMenuAuctionListPaneController implements Initializable{
    @FXML FlowPane mainMenuAuctionListFlowPane;
    ObservableList<Item> itemList = FXCollections.observableArrayList();

    /**
     * Override initialize, to load in cellfact
     * @param url
     * @param res
     */
    @Override
    public void initialize(URL url, ResourceBundle res){

        //Placeholder item
        //TODO: wait for other's works on this, and then link it with their item list
        itemList.add(new Item("item", "desc", 50.0, 70.0, "3") {
        });
        itemList.add(new Item("item2", "desc", 20.0, 60.0, "3") {
        });
        itemList.add(new Item("item3", "desc", 10.0, 30.0, "3") {
        });

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
