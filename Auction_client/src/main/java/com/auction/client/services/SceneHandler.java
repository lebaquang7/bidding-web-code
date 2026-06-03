package com.auction.client.services;

import com.auction.client.Properties;
import com.auction.client.controllers.ImageViewController;
import com.auction.client.utils.ThemeHandler;
import com.auction.shared.models.Item;
import java.io.IOException;
import java.net.URL;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SceneHandler {
  // Class xử lý chuyển màn hình
  private static Stage stage;
  private static Scene scene;
  private static Parent root;

  /**
   * Usage: Chuyển đến scene được nhắc đến trong Location
   *
   * @param location Địa điểm của scene cần thiết
   * @param event Sự kiện khởi đầu
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

  // Interface cho những class load vào thông tin của 1 sản phẩm
  public interface ItemLoadable {
    void setItem(Item item);
  }

  /**
   * usage: Chuyển đến view của một sản phẩm có chứa thông tin sản phẩm
   *
   * @param event
   * @param item Thông tin sản phẩm
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

  public interface ImageLoadable {
    void setImage(Image image);
  }

  /**
   * Usage: Chuyển đến image view
   *
   * @param event
   * @param image
   */
  public static void switchToImageView(MouseEvent event, Image image) {
    try {
      FXMLLoader loader =
          new FXMLLoader(SceneHandler.class.getResource(ImageViewController.getPATH_TO_VIEW()));
      Parent popupRoot = loader.load();

      ImageViewController controller = loader.getController();
      if (image == null) return;
      controller.setImage(image);

      Stage popupStage = new Stage();
      popupStage.setTitle("Zoomed in image");
      Scene popupScene = new Scene(popupRoot);
      ThemeHandler.getInstance().addActiveScene(popupScene);
      popupStage.setScene(popupScene);
      popupStage.show();
    } catch (IOException errorEvent) {
      errorEvent.printStackTrace();
    }
  }

  /**
   * Usage: Đóng scene
   *
   * @param event
   */
  public static void closeScene(ActionEvent event) {
    ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
  }
}
