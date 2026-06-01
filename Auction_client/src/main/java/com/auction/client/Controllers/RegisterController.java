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

  @FXML
  TextField registerWindowUsernameField;
  @FXML
  TextField registerWindowEmailField;
  @FXML
  ChoiceBox<String> registerWindowAccountTypeChoiceBox;
  @FXML
  PasswordField registerWindowPasswordField;
  @FXML
  PasswordField registerWindowPasswordConfirmationField;
  @FXML
  Label registerWindowErrorPrompt;

  @FXML
  public void initialize() {
    if (registerWindowAccountTypeChoiceBox != null) {
      registerWindowAccountTypeChoiceBox.getItems().addAll("Bidder", "Seller");
      registerWindowAccountTypeChoiceBox.setValue("Bidder");
    }
  }

  @FXML
  void registerWindowRegisterAction(ActionEvent event) {
    String username = registerWindowUsernameField.getText();
    String email = registerWindowEmailField.getText();
    String password = registerWindowPasswordField.getText();
    String confirmPassword = registerWindowPasswordConfirmationField.getText();
    String accountType = registerWindowAccountTypeChoiceBox.getValue();

    if (username.isEmpty() || password.isEmpty()) {
      registerWindowErrorPrompt.setText("Vui lòng điền đầy đủ Username và Password");
      return;
    }

    if (!password.equals(confirmPassword)) {
      registerWindowErrorPrompt.setText("Mật khẩu xác nhận không khớp");
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

      if ("success".equals(result)) {
        registerWindowErrorPrompt.setText("Đăng ký thành công. Quay lại đăng nhập");
        registerWindowErrorPrompt.setStyle("-fx-text-fill: green;");
      } else if ("duplicate".equals(result)) {
        registerWindowErrorPrompt.setText("Tên đăng nhập đã tồn tại!");
        registerWindowErrorPrompt.setStyle("-fx-text-fill: red;");
      } else {
        registerWindowErrorPrompt.setText("Lỗi: " + result);
        registerWindowErrorPrompt.setStyle("-fx-text-fill: red;");
      }

    } catch (Exception e) {
      e.printStackTrace();
      registerWindowErrorPrompt.setText("Không thể kết nối Server!");
    }
  }

  public void registerWindowSwitchToLogin(ActionEvent event) {
    SceneHandler.switchToScene(getClass().getResource(LoginController.getPATH_TO_VIEW()), event);
  }
}
