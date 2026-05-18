package com.auction.client.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import com.auction.client.Models.AccountEventHandler;
import com.auction.shared.models.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;

public class MainMenuSellItemPaneController {
    @FXML TextField mainMenuSellItemPaneItemNameField;
    @FXML TextField mainMenuSellItemPaneItemDescriptionField;
    @FXML TextField mainMenuSellItemPaneStartingPriceField;
    @FXML TextField mainMenuSellItemPanePriceIncrementField;

    @FXML private ComboBox<String> itemTypeComboBox;
    //Cần thêm Comobox cho phép chọn loại vật phẩm đấu giá (Artworks, Vehicle, Electronic items)

    @FXML
    public void initialize() {
        if (itemTypeComboBox != null) {
            itemTypeComboBox.getItems().addAll("Artwork", "Electronics", "Vehicle");
            itemTypeComboBox.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleSubmitItem(ActionEvent event) {
        try {
            if (itemTypeComboBox.getValue() == null) {
                showError("Chưa chọn loại vật phẩm");
                return;
            }

            String name = mainMenuSellItemPaneItemNameField.getText();
            String description = mainMenuSellItemPaneItemDescriptionField.getText();
            double startingPrice = Double.parseDouble(mainMenuSellItemPaneStartingPriceField.getText());
            double currentPrice = startingPrice;

            User currentUser = AccountEventHandler.getCurrentUser();
            if (currentUser == null) {
                showError("Không tìm thấy thông tin người dùng");
                return;
            }

            Item newItem = null;
            String typeOfItem = itemTypeComboBox.getValue();
            if ("Artwork".equals(typeOfItem)) {
                newItem = new Art(name, description, startingPrice, currentPrice);
            } else if ("Electronics".equals(typeOfItem)) {
                newItem = new Electronics(name, description, startingPrice, currentPrice);
            } else {
                newItem = new Vehicle(name, description, startingPrice, currentPrice);
            }
            newItem.setSellerId(currentUser.getId());

            String result = AccountEventHandler.sellItem(newItem);
            if ("success".equals(result)) {
                showSuccess("Đưa vật phẩm lên sàn đấu giá thành công");
                clearFields();
            } else {showError("Đăng bán thất bại");}

        } catch (NumberFormatException e) {
            showError("Giá tiền không hợp lệ");
        } catch (Exception e) {
            showError("Có lỗi xảy ra: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

    private void clearFields() {
        mainMenuSellItemPaneItemNameField.clear();
        mainMenuSellItemPaneItemDescriptionField.clear();
        mainMenuSellItemPaneStartingPriceField.clear();
        mainMenuSellItemPanePriceIncrementField.clear();
    }
}
