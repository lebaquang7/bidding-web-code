package com.auction.client;

import com.auction.client.services.ClientExitHandler;
import com.auction.client.services.ClientNotificationListener;
import com.auction.client.utils.ConfigFileHandler;
import com.auction.client.utils.CurrencySelectorHandler;
import com.auction.client.utils.ThemeHandler;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {
  private static ClientNotificationListener notificationListener;

  public static void setNotificationListener(ClientNotificationListener listener) {
    notificationListener = listener;
  }

  public static ClientNotificationListener getNotificationListener() {
    return notificationListener;
  }

  public static void main(String[] args) {
    launch(args);
  }

  int initialStageX = 600;
  int initialStageY = 400;

  @Override
  public void start(Stage primaryStage) throws IOException {

    primaryStage.setTitle(Properties.getAPPLICATION_NAME_AND_VERSION());
    Image icon =
        new Image(getClass().getResourceAsStream(Properties.getAPPLICATION_IMAGE_DIRECTORY()));
    primaryStage.getIcons().add(icon);

    Parent root =
        FXMLLoader.load(
            getClass()
                .getResource(
                    "/com/auction/client/views/login_view.fxml")); // load Login View as root node,
    // with directory to
    // login_view.fxml
    Scene scene = new Scene(root, initialStageX, initialStageY); // create scene with root
    primaryStage.setScene(scene);
    primaryStage.setResizable(false);
    primaryStage.show();

    ThemeHandler.getInstance().addActiveScene(scene);

    // displays prompt on pressing X to close client
    primaryStage.setOnCloseRequest(
        event -> {
          event.consume(); // consume the event, so it only executes closing if user presses OK
          ClientExitHandler.closeWithExitPrompt(primaryStage);
        });

    // initialize configurations
    CurrencySelectorHandler.getInstance()
        .setActiveCurrency(ConfigFileHandler.getProperty("currencyType", "VND"));
    ThemeHandler.getInstance().setTheme(ConfigFileHandler.getProperty("theme", "Default"));
  }
}
