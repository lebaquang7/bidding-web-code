package com.auction.client.Controllers;

import java.io.IOException;

import com.auction.client.Models.AccountEventHandler; // Import model : AccountEventHandler
 
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML TextField loginWindowUsernameField; //LOAD fxml UI elements
    @FXML PasswordField loginWindowPasswordField;
    @FXML TextField loginWindowShownPwdTextField;
    @FXML CheckBox loginWindowShowPwdCheckbox;
    @FXML Label loginWindowErrorPrompt;

    private Stage stage; //Declare stage-scene-root (used for switching scenes)
    private Scene scene;
    private Parent root;

    /**
     * Usage: called when user presses the login button.
     * @param event
     */
    public void loginWindowLoginAction(ActionEvent event){
        String username = loginWindowUsernameField.getText();
        String password = loginWindowPasswordField.getText();

        String result = AccountEventHandler.validateAccount(username, password);

        switch (result) { //switch (essentially mass if-else) over end cases
            case "loginSuccessful" -> { //if login successful, switch to main menu.
                //TODO: make all this scene switch into a method
                try {
                    root = FXMLLoader.load(getClass().getResource("/com/auction/client/views/mainMenu_view.fxml"));
                } catch (IOException errorEvent) {
                    errorEvent.printStackTrace();
                }

                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
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
        try {
            root = FXMLLoader.load(getClass().getResource("/com/auction/client/views/register_view.fxml"));
        } catch (IOException errorEvent) {
            errorEvent.printStackTrace();
        }
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    /**
     * @param event
     * Usage: Called when user presses the show password checkbox.
     */
    public void loginWindowShowPwd(ActionEvent event){
        loginWindowShownPwdTextField.textProperty().bindBidirectional(loginWindowPasswordField.textProperty()); //bidirectional binding with pwd field text

        if (loginWindowShowPwdCheckbox.isSelected() == true){
            /**  
            * When show pwd checkbox is ticked...
            * set managed, visibility of the text field of password to true,
            * and vice versa for password field
            */
            loginWindowShownPwdTextField.setManaged(true); 
            loginWindowShownPwdTextField.setVisible(true);
            loginWindowPasswordField.setManaged(false);
            loginWindowPasswordField.setVisible(false);
        } else {
            //reverse
            loginWindowShownPwdTextField.setManaged(false);
            loginWindowShownPwdTextField.setVisible(false);
            loginWindowPasswordField.setManaged(true);
            loginWindowPasswordField.setVisible(true);
        }
    }
}
