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
  // Path to the view this controller is affiliated with
  private static final String PATH_TO_VIEW = "/com/auction/client/views/login_view.fxml";

  public static String getPATH_TO_VIEW() {
    return PATH_TO_VIEW;
  }

  @FXML TextField usernameField; // load fxml UI elements
  @FXML PasswordField passwordField;
  @FXML TextField shownPwdTextField;
  @FXML CheckBox showPwdCheckbox;
  @FXML Label errorPrompt;

  /** Usage: called when user presses the login button. */
  @FXML
  public void loginAction(ActionEvent event) {
    String username = usernameField.getText();
    String password = passwordField.getText();

    String result = AccountEventHandler.validateAccount(username, password);

    switch (result) { // switch (essentially mass if-else) over end cases
      case "loginSuccessful" -> { // if login successful, switch to main menu.
        ClientNotificationListener listener = new ClientNotificationListener();
        listener.start();

        System.out.println("Path: " + MainMenuController.getPATH_TO_VIEW());
        System.out.println(
            "Resource: " + getClass().getResource(MainMenuController.getPATH_TO_VIEW()));
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
   * Usage: called when user presses the register button.
   *
   * @param event
   */
  @FXML
  public void switchToRegister(ActionEvent event) {
    SceneHandler.switchToScene(
        getClass().getResource("/com/auction/client/views/register_view.fxml"), event);
  }

  /**
   * @param event Usage: Called when user presses the show password checkbox.
   */
  @FXML
  public void showPwd(ActionEvent event) {
    shownPwdTextField
        .textProperty()
        .bindBidirectional(
            passwordField.textProperty()); // bidirectional binding with pwd field text

    if (showPwdCheckbox.isSelected() == true) {
      // make pwd shown
      UIElementHandler.switchElement(shownPwdTextField, passwordField);
    } else {
      // reverse
      UIElementHandler.switchElement(passwordField, shownPwdTextField);
    }
  }
}
