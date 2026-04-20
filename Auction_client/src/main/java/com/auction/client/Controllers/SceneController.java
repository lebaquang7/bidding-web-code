package com.auction.client.Controllers;

import java.io.IOException;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
}
