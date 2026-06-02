package com.auction.client.controllers;

import com.auction.client.services.AccountEventHandler;
import com.auction.client.services.SceneHandler;
import com.auction.shared.models.Bidder;
import com.auction.shared.models.Seller;
import com.auction.shared.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {
  // Path to the view this controller is affiliated with
  private static final String PATH_TO_VIEW = "/com/auction/client/views/register_view.fxml";

  public static String getPATH_TO_VIEW() {
    return PATH_TO_VIEW;
  }

  @FXML TextField usernameField;
  @FXML TextField emailField;
  @FXML ChoiceBox<String> accountTypeChoiceBox;
  @FXML PasswordField passwordField;
  @FXML PasswordField passwordConfirmationField;
  @FXML Label errorPrompt;

  @FXML
  public void initialize() {
    if (accountTypeChoiceBox != null) {
      accountTypeChoiceBox.getItems().addAll("Bidder", "Seller");
      accountTypeChoiceBox.setValue("Bidder");
    }
  }

  @FXML
  void registerAction(ActionEvent event) {
    String username = usernameField.getText();
    String email = emailField.getText();
    String password = passwordField.getText();
    String confirmPassword = passwordConfirmationField.getText();
    String accountType = accountTypeChoiceBox.getValue();

    if (username.isEmpty() || password.isEmpty()) {
      errorPrompt.setText("Vui lòng điền đầy đủ Username và Password");
      return;
    }

    if (!password.equals(confirmPassword)) {
      errorPrompt.setText("Mật khẩu xác nhận không khớp");
      return;
    }

    User newUser;
    switch (accountType) {
      case "Seller" -> {
        newUser = new Seller(username, password);
      }
      default -> {
        newUser = new Bidder(username, password, "", null, 0);
      }
    }

    try {
      String result = AccountEventHandler.registerAccount(newUser);

      switch (result) {
        case "success":
          errorPrompt.setText("Đăng ký thành công. Quay lại đăng nhập");
          errorPrompt.setStyle("-fx-text-fill: green;");
          break;
        case "duplicate":
          errorPrompt.setText("Tên đăng nhập đã tồn tại!");
          errorPrompt.setStyle("-fx-text-fill: red;");
          break;
        default:
          errorPrompt.setText("Lỗi: " + result);
          errorPrompt.setStyle("-fx-text-fill: red;");
          break;
      }

    } catch (Exception e) {
      e.printStackTrace();
      errorPrompt.setText("Không thể kết nối Server!");
    }
  }

  @FXML
  public void switchToLogin(ActionEvent event) {
    SceneHandler.switchToScene(getClass().getResource(LoginController.getPATH_TO_VIEW()), event);
  }
}
