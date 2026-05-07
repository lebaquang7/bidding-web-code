package com.auction.client.Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.auction.server.models.Item;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;

public class MainMenuController implements Initializable {
    //Path to the view this controller is affiliated with
    private static final String PATH_TO_VIEW = "/com/auction/client/views/mainMenu_view.fxml";
    public static String getPATH_TO_VIEW(){
        return PATH_TO_VIEW;
    }
    
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
        itemList.add(new Item("item", "desc", 50.0, 70.0, 3) {
        });
        itemList.add(new Item("item2", "desc", 20.0, 60.0, 3) {
        });
        itemList.add(new Item("item3", "desc", 10.0, 30.0, 3) {
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


    //TODO: work on these
    public void mainMenuSwitchToAuctionList(ActionEvent event){}
    public void mainMenuSwitchToSellItem(ActionEvent event){}
    public void mainMenuSwitchToSettings(ActionEvent event){}

    public void mainMenuLogOut(ActionEvent event){
        SceneController.switchToScene(getClass().getResource(LoginController.getPATH_TO_VIEW()), event);
    }
}
