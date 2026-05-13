package com.auction.client.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
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
    @FXML TextField registerWindowEmailField;
    @FXML ChoiceBox<String> registerWindowAccountTypeChoiceBox;
    @FXML PasswordField registerWindowPasswordField;
    @FXML PasswordField registerWindowPasswordConfirmationField;
    @FXML Label registerWindowErrorPrompt;

    public void initialize(){
        //TODO: need works
        ObservableList<String> accountTypeChoiceList = FXCollections.observableArrayList("Bidder", "Seller");
        registerWindowAccountTypeChoiceBox.setItems(accountTypeChoiceList);
        registerWindowAccountTypeChoiceBox.setValue("Bidder");
    }
    
    //TODO: work on this
    public void registerWindowRegisterAction(ActionEvent event){}


    public void registerWindowSwitchToLogin(ActionEvent event){
        SceneController.switchToScene(getClass().getResource(LoginController.getPATH_TO_VIEW()), event);
    }
}
