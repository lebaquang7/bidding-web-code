package com.auction.client.services;

import com.auction.client.Properties;
import com.auction.client.utils.ThemeHandler;
import com.auction.shared.models.Item;
import java.io.IOException;
import java.net.URL;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneHandler {
  private static Stage stage; // Declare stage-scene-root (used for switching scenes)
  private static Scene scene;
  private static Parent root;

  /**
   * Usage: switch to scene mentioned in location, upon activation of actionEvent.
   *
   * @param location
   * @param event
   */
  public static void switchToScene(URL location, ActionEvent event) {
    try {
      root = FXMLLoader.load(location);
    } catch (IOException errorEvent) {
      errorEvent.printStackTrace();
    }
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    ThemeHandler.getInstance().addActiveScene(scene);
    stage.setScene(scene);
    stage.show();
  }

  public interface ItemLoadable {
    void setItem(Item item);
  }

  /**
   * usage: switch to view of individual auction item cards
   * (itemDetailsController/auctionViewController)
   *
   * @param event
   * @param item
   */
  public static <T extends ItemLoadable> void switchToItemView(
      String target, ActionEvent event, Item item) {
    try {
      // load the view in fxmlloader
      FXMLLoader loader = new FXMLLoader(SceneHandler.class.getResource(target));
      Parent popupRoot = loader.load();

      // create controller of new scene
      T controller = loader.getController();
      controller.setItem(item);

      Stage popupStage = new Stage();
      popupStage.setTitle(Properties.getAPPLICATION_NAME_AND_VERSION());
      Scene popupScene = new Scene(popupRoot);
      ThemeHandler.getInstance().addActiveScene(popupScene);
      popupStage.setScene(popupScene);
      popupStage.show();
    } catch (IOException errorEvent) {
      errorEvent.printStackTrace();
    }
  }

  /**
   * Usage: close the scene.
   *
   * @param event
   */
  public static void closeScene(ActionEvent event) {
    ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
  }
}
