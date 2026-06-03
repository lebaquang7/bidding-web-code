package com.auction.client;

import com.auction.client.controllers.LoginController;
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
  // Main Class của Client
  public static void main(String[] args) {
    launch(args);
  }

  // Listener cho thông tin về client
  private static ClientNotificationListener notificationListener;

  public static void setNotificationListener(ClientNotificationListener listener) {
    notificationListener = listener;
  }

  public static ClientNotificationListener getNotificationListener() {
    return notificationListener;
  }

  // Thông số khởi tạo Stage
  int initialStageX = 600;
  int initialStageY = 400;

  @Override
  public void start(Stage primaryStage) throws IOException {

    primaryStage.setTitle(Properties.getAPPLICATION_NAME_AND_VERSION());
    Image icon =
        new Image(getClass().getResourceAsStream(Properties.getAPPLICATION_IMAGE_DIRECTORY()));
    primaryStage.getIcons().add(icon);

    Parent root = FXMLLoader.load(getClass().getResource(LoginController.getPATH_TO_VIEW()));
    // Load root controller
    Scene scene = new Scene(root, initialStageX, initialStageY);
    // Tạo scene với root
    primaryStage.setScene(scene);
    primaryStage.setResizable(false);
    primaryStage.show();

    // Thêm scene vào theme handler
    ThemeHandler.getInstance().addActiveScene(scene);

    // Hiện prompt khi muốn đóng app
    primaryStage.setOnCloseRequest(
        event -> {
          event.consume(); // consume sự kiện để không đóng nếu không chấp thuận đóng
          ClientExitHandler.closeWithExitPrompt(primaryStage);
        });

    // Khởi động cài đặt của client
    CurrencySelectorHandler.getInstance()
        .setActiveCurrency(ConfigFileHandler.getProperty("currencyType", "VND"));
    ThemeHandler.getInstance().setTheme(ConfigFileHandler.getProperty("theme", "Default"));
  }
}
