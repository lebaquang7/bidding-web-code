package com.auction.client.Controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.auction.server.models.Item;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

public class MainMenuController implements Initializable {
    //Path to the view this controller is affiliated with
    private static final String PATH_TO_VIEW = "/com/auction/client/views/mainMenu_view.fxml";
    public static String getPATH_TO_VIEW(){
        return PATH_TO_VIEW;
    }
    
    @FXML BorderPane mainMenuMainBorderPane;
    @FXML FlowPane mainMenuAuctionListFlowPane;
    ObservableList<Item> itemList = FXCollections.observableArrayList();

    /**
     * Override initialize, to load in cellfact
     * @param url
     * @param res
     */
    @Override
    public void initialize(URL url, ResourceBundle res){

        BorderPaneController.setMainLayout(mainMenuMainBorderPane);
        BorderPaneController.setCenter("/com/auction/client/views/mainMenu_auctionListPane.fxml");
    }

    //Size of center pane: 700 W, 600 H (full pane + side pane is 900 W, 600 H) (for reference when creating side panes)
    public void mainMenuSwitchToAuctionList(ActionEvent event){
        BorderPaneController.setCenter("/com/auction/client/views/mainMenu_auctionListPane.fxml");
    }
    public void mainMenuSwitchToSellItem(ActionEvent event){
        BorderPaneController.setCenter("/com/auction/client/views/mainMenu_sellItemPane.fxml");
    }
    public void mainMenuSwitchToSettings(ActionEvent event){
        BorderPaneController.setCenter("/com/auction/client/views/mainMenu_settingsPane.fxml");
    }

    public void mainMenuLogOut(ActionEvent event){
        SceneController.switchToScene(getClass().getResource(LoginController.getPATH_TO_VIEW()), event);
    }
}
