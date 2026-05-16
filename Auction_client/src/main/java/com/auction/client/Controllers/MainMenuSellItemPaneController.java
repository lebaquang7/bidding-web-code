package com.auction.client.Controllers;

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
            itemTypeComboBox.getItems().addAll("Art", "Electronics", "Vehicle");
        }
    }

    @FXML
    private void handleSubmitItem() {
        try {
            String name = mainMenuSellItemPaneItemNameField.getText();
            String description = mainMenuSellItemPaneItemDescriptionField.getText();
            double startingPrice = Double.parseDouble(mainMenuSellItemPaneStartingPriceField.getText());
            double currentPrice = startingPrice;
        } catch (NumberFormatException e) {
            showError("Giá tiền phải là con số hợp lệ!");
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


    //TODO: work on sell item pane
    //TODO: restrict sell item pane to only seller users
}
