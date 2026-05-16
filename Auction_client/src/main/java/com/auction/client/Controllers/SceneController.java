package com.auction.client.Controllers;

import java.io.IOException;
import java.net.URL;

import com.auction.client.Properties;
import com.auction.shared.models.Item;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class SceneController {
    private static Stage stage; //Declare stage-scene-root (used for switching scenes)
    private static Scene scene;
    private static Parent root;

    /**
     * Usage: switch to scene mentioned in location, upon activation of actionEvent.
     * @param location
     * @param event
     */
    public static void switchToScene(URL location, ActionEvent event){
        try {
            root = FXMLLoader.load(location);
        } catch (IOException errorEvent) {
            errorEvent.printStackTrace();
        }
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Makes node 1 visible, while hiding node 2 via managed and visible 
     * Do use bidirectional binding if value of node 1 and 2 need to be tied
     * Usage: pwd hide/show checkbox, etc
     * @param node1
     * @param node2
     */
    public static void switchElement(Node node1, Node node2){
        node1.setManaged(true); 
        node1.setVisible(true);
        node2.setManaged(false);
        node2.setVisible(false);
    }



    //Interface for controllers that "loads" an item in, rn covers itemdetails and auctionview
    public interface ItemLoadable {
        void setItem(Item item);
    }


    /**
     * usage: switch to view of individual auction item cards (itemDetailsController/auctionViewController)
     * @param event
     * @param item
     */
    public static <T extends ItemLoadable> void switchToItemView(String target, ActionEvent event, Item item){
        try {
            //load the view in fxmlloader
            FXMLLoader loader = new FXMLLoader(SceneController.class.getResource(target));
            Parent popupRoot = loader.load();

            //create controller of new scene
            T controller = loader.getController();
            controller.setItem(item);

            Stage popupStage = new Stage();
            popupStage.setTitle(Properties.getAPPLICATION_NAME_AND_VERSION());
            Scene popupScene = new Scene(popupRoot);
            popupStage.setScene(popupScene);
            popupStage.show();
        } catch (IOException errorEvent) {
            errorEvent.printStackTrace();
        }
    }

    /**
     * Usage: close the scene.
     * @param event
     */
    public static void closeScene(ActionEvent event){
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }


    /**
     * Usage: When user presses X, will prompt before closing the client.
     * @param event
     */
    public static void closeWithExitPrompt(Stage stage){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("You are about to log out of the client.");
        alert.setHeaderText("Are you sure you want to close the client?");
        alert.setContentText("This will close the client and signs you out.");

        if (alert.showAndWait().get() == ButtonType.OK){
            stage.close();
        }
    }
}
