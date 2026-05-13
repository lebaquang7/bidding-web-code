package com.auction.client.Controllers;

import com.auction.client.Models.AccountEventHandler;
import com.auction.server.models.Bidder;
import com.auction.server.models.NetworkRequest;
import com.auction.server.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.Socket;

import static com.auction.server.models.NetworkRequest.requestType.Register;

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

    @FXML
    void registerWindowRegisterAction(ActionEvent event) {
        String name = registerWindowUsernameField.getText();
        String password = registerWindowPasswordField.getText();

        // Phải khai báo và gán giá trị rõ ràng TRƯỚC khi sử dụng
        User newUser = new Bidder(0.0, name, password, 0);

        try {
            // Truyền biến newUser vừa tạo vào hàm
            String result = AccountEventHandler.registerAccount(newUser);

            if ("success".equals(result)) {
                registerWindowErrorPrompt.setText("Đăng ký thành công!");
                registerWindowErrorPrompt.setStyle("-fx-text-fill: green;");
            } else {
                registerWindowErrorPrompt.setText("Lỗi: " + result);
                registerWindowErrorPrompt.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            e.printStackTrace();
            registerWindowErrorPrompt.setText("Không thể kết nối Server!");
        }
    }

    public void registerWindowSwitchToLogin(ActionEvent event){
        SceneController.switchToScene(getClass().getResource(LoginController.getPATH_TO_VIEW()), event);
    }
}
