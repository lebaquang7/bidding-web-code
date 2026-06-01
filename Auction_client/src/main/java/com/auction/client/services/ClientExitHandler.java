package com.auction.client.services;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class ClientExitHandler {
    /**
     * Usage: When user presses X, will prompt before closing the client.
     *
     * @param event
     */
    public static void closeWithExitPrompt(Stage stage) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("You are about to log out of the client.");
        alert.setHeaderText("Are you sure you want to close the client?");
        alert.setContentText("This will close the client and signs you out.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            stage.close();
        }
    }
}
