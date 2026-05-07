package com.auction.client.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {
    //Path to the view this controller is affiliated with
    private static final String PATH_TO_VIEW = "/com/auction/client/views/register_view.fxml";
    public static String getPATH_TO_VIEW(){
        return PATH_TO_VIEW;
    }

    @FXML TextField registerWindowUsernameField;
    @FXML PasswordField registerWIndowPasswordField;
    @FXML PasswordField registerWindowPasswordConfirmationField;
    @FXML Label registerWindowErrorPrompt;

    //TODO: work on this
    public void registerWindowRegisterAction(ActionEvent event){}


    public void registerWindowSwitchToLogin(ActionEvent event){
        SceneController.switchToScene(getClass().getResource(LoginController.getPATH_TO_VIEW()), event);
    }
}
