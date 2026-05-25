package com.auction.client.Controllers;

import java.math.BigDecimal;

import com.auction.client.Models.AccountEventHandler;
import com.auction.client.Models.ItemsEventHandler;
import com.auction.shared.models.Art;
import com.auction.shared.models.Electronics;
import com.auction.shared.models.Item;
import com.auction.shared.models.User;
import com.auction.shared.models.Vehicle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class MainMenuSellItemPaneController {
    @FXML TextField mainMenuSellItemPaneItemNameField;
    @FXML TextField mainMenuSellItemPaneItemDescriptionField;
    @FXML TextField mainMenuSellItemPaneStartingPriceField;
    @FXML TextField mainMenuSellItemPanePriceIncrementField;

    @FXML private ComboBox<String> itemTypeComboBox;

    @FXML
    public void initialize() {
        if (itemTypeComboBox != null) {
            //TODO: elements based on actual available class types?
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
            BigDecimal startingPrice = BigDecimal.valueOf(Double.valueOf((mainMenuSellItemPaneStartingPriceField.getText())));
            BigDecimal currentPrice = startingPrice;
            double percentage = Double.parseDouble(mainMenuSellItemPanePriceIncrementField.getText());
            BigDecimal priceIncrement = startingPrice.multiply(BigDecimal.valueOf(percentage / 100.0));

            User currentUser = AccountEventHandler.getCurrentUser();
            if (currentUser == null) {
                showError("Không tìm thấy thông tin người dùng");
                return;
            }

            Item newItem = null;
            String typeOfItem = itemTypeComboBox.getValue();
            if ("Artwork".equals(typeOfItem)) {
                newItem = new Art(name, description, startingPrice, currentPrice, "", true, 0, "");
            } else if ("Electronics".equals(typeOfItem)) {
                newItem = new Electronics(name, description, startingPrice, currentPrice, 24, "", "", "");
            } else {
                newItem = new Vehicle(name, description, startingPrice, currentPrice, "", 0, 0);
            }
            newItem.setSellerId(currentUser.getId());
            newItem.setPriceIncrement(priceIncrement);

            String result = ItemsEventHandler.sellItem(newItem);
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
