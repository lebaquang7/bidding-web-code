package com.auction.client.controllers;

import com.auction.client.services.AccountEventHandler;
import com.auction.client.services.ClientNotificationListener;
import com.auction.client.services.SceneHandler;
import com.auction.client.utils.UIElementHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
  // Đường dẫn đến view của controller này
  private static final String PATH_TO_VIEW = "/com/auction/client/views/login_view.fxml";

  public static String getPATH_TO_VIEW() {
    return PATH_TO_VIEW;
  }

  @FXML TextField usernameField; // Load thành phần của UI trong fxml
  @FXML PasswordField passwordField;
  @FXML TextField shownPwdTextField;
  @FXML CheckBox showPwdCheckbox;
  @FXML Label errorPrompt;

  /**
   * Usage: chạy khi người dùng nhấn nút đăng nhập (login)
   *
   * @param event
   */
  @FXML
  public void loginAction(ActionEvent event) {
    String username = usernameField.getText();
    String password = passwordField.getText();

    String result = AccountEventHandler.validateAccount(username, password);

    switch (result) {
      case "loginSuccessful" -> {
        ClientNotificationListener listener = new ClientNotificationListener();
        listener.start();
        SceneHandler.switchToScene(
            getClass().getResource(MainMenuController.getPATH_TO_VIEW()), event);
      }
      case "invalidPassword" -> {
        errorPrompt.setText("Invalid password.");
      }
      case "accountDoesntExist" -> {
        errorPrompt.setText("This account does not exist.");
      }
      default -> {
        errorPrompt.setText("Unexpected error occurred: " + result);
      }
    }
  }

  /**
   * Usage: Chuyển trang khi người dùng nhấn nút đăng kí (register)
   *
   * @param event
   */
  @FXML
  public void switchToRegister(ActionEvent event) {
    SceneHandler.switchToScene(getClass().getResource(RegisterController.getPATH_TO_VIEW()), event);
  }

  /**
   * Usage: Chạy khi người dùng nhấn nút hiện mật khẩu (show password)
   *
   * @param event
   */
  @FXML
  public void showPwd(ActionEvent event) {
    shownPwdTextField
        .textProperty()
        .bindBidirectional(passwordField.textProperty()); // Nối 2 chiều với trường mật khẩu

    // Chuyển trạng thái giữa 2 phần màn hình
    if (showPwdCheckbox.isSelected() == true) {
      UIElementHandler.switchElement(shownPwdTextField, passwordField);
    } else {
      UIElementHandler.switchElement(passwordField, shownPwdTextField);
    }
  }
}
