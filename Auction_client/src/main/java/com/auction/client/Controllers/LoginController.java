package com.auction.client.Controllers;


import com.auction.client.Models.AccountEventHandler; // Import model : AccountEventHandler
import com.auction.client.Models.ClientNotificationListener;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    //Path to the view this controller is affiliated with
    private static final String PATH_TO_VIEW = "/com/auction/client/views/login_view.fxml";
    public static String getPATH_TO_VIEW(){
        return PATH_TO_VIEW;
    }

    @FXML TextField loginWindowUsernameField; //load fxml UI elements
    @FXML PasswordField loginWindowPasswordField;
    @FXML TextField loginWindowShownPwdTextField;
    @FXML CheckBox loginWindowShowPwdCheckbox;
    @FXML Label loginWindowErrorPrompt;

    /**
     * Usage: called when user presses the login button.
     */
    public void loginWindowLoginAction(ActionEvent event){
        String username = loginWindowUsernameField.getText();
        String password = loginWindowPasswordField.getText();

        String result = AccountEventHandler.validateAccount(username, password);

        switch (result) { //switch (essentially mass if-else) over end cases
            case "loginSuccessful" -> { //if login successful, switch to main menu.
                ClientNotificationListener listener = new ClientNotificationListener();
                listener.start();

                System.out.println("Path: " + MainMenuController.getPATH_TO_VIEW());
                System.out.println("Resource: " + getClass().getResource(MainMenuController.getPATH_TO_VIEW()));
                SceneController.switchToScene(getClass().getResource(MainMenuController.getPATH_TO_VIEW()), event);
            }
            case "invalidPassword" -> {
                loginWindowErrorPrompt.setText("Invalid password.");
            }
            case "accountDoesntExist" -> {
                loginWindowErrorPrompt.setText("This account does not exist.");
            }
            default -> {
                loginWindowErrorPrompt.setText("Unexpected error occurred: "+result);
            }
        }
    }


    /**
     * Usage: called when user presses the register button.
     * @param event
     */
    public void loginWindowSwitchToRegister(ActionEvent event){
        SceneController.switchToScene(getClass().getResource("/com/auction/client/views/register_view.fxml"), event);
    }


    /**
     * @param event
     * Usage: Called when user presses the show password checkbox.
     */
    public void loginWindowShowPwd(ActionEvent event){
        loginWindowShownPwdTextField.textProperty().bindBidirectional(loginWindowPasswordField.textProperty()); //bidirectional binding with pwd field text

        if (loginWindowShowPwdCheckbox.isSelected() == true){
            //make pwd shown
            SceneController.switchElement(loginWindowShownPwdTextField, loginWindowPasswordField);
        } else {
            //reverse
            SceneController.switchElement(loginWindowPasswordField, loginWindowShownPwdTextField);
        }
    }
}
