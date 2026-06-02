package com.auction.client.controllers;

import com.auction.client.services.AccountEventHandler;
import com.auction.client.services.SceneHandler;
import com.auction.shared.models.Admin;
import com.auction.shared.models.Seller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class MainMenuController {
  // Path to the view this controller is affiliated with
  private static final String PATH_TO_VIEW = "/com/auction/client/views/mainMenu_view.fxml";

  public static String getPATH_TO_VIEW() {
    return PATH_TO_VIEW;
  }

  @FXML BorderPane mainBorderPane;
  @FXML Button sellItemButton;
  @FXML Button adminAuctionButton;
  @FXML Button sellerListButton;

  public void initialize() {
    BorderPaneController.setMainLayout(mainBorderPane);
    BorderPaneController.setCenter("/com/auction/client/views/mainMenu_auctionListPane.fxml");

    // hides buttons depending on user type
    if (!(AccountEventHandler.getCurrentUser() instanceof Seller
        || AccountEventHandler.getCurrentUser() instanceof Admin)) {
      sellItemButton.setVisible(false);
      sellerListButton.setVisible(false);
    }
    if (!(AccountEventHandler.getCurrentUser() instanceof Admin)) {
      adminAuctionButton.setVisible(false);
    }
  }

  // Size of center pane: 700 W, 600 H (full pane + side pane is 900 W, 600 H)
  // (for reference when
  // creating side panes)
  @FXML
  public void switchToAuctionList(ActionEvent event) {
    BorderPaneController.setCenter("/com/auction/client/views/mainMenu_auctionListPane.fxml");
  }

  @FXML
  public void switchToSellItem(ActionEvent event) {
    BorderPaneController.setCenter("/com/auction/client/views/mainMenu_sellItemPane.fxml");
  }

  @FXML
  public void switchToSellerList(ActionEvent event) {
    BorderPaneController.setCenter("/com/auction/client/views/mainMenu_sellerSubmitListPane.fxml");
  }

  @FXML
  public void switchToSettings(ActionEvent event) {
    BorderPaneController.setCenter("/com/auction/client/views/mainMenu_settingsPane.fxml");
  }

  @FXML
  public void switchToAdminFunctions(ActionEvent event) {
    BorderPaneController.setCenter("/com/auction/client/views/mainMenu_adminPane.fxml");
  }

  @FXML
  public void logOut(ActionEvent event) {
    SceneHandler.switchToScene(getClass().getResource(LoginController.getPATH_TO_VIEW()), event);
  }
}
