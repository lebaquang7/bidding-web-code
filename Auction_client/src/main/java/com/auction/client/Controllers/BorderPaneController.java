package com.auction.client.Controllers;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class BorderPaneController {
    //Class handling switching specific border pane views
    private static BorderPane mainLayout;

    public static void setMainLayout(BorderPane layout){
        mainLayout = layout;
    }

    /**
     * Usage: switch center pane to pane mentioned in fxmlPath
     * @param fxmlPath
     */
    public static void setCenter(String fxmlPath){
        try {
            Parent pane = FXMLLoader.load(BorderPaneController.class.getResource(fxmlPath));
            mainLayout.setCenter(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
