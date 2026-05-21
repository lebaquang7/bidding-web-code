package com.auction.client.Controllers;

import java.math.BigDecimal;

import com.auction.client.Models.AccountEventHandler;
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

import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.ArrayList;

public class MainMenuSellItemPaneController {
    @FXML TextField mainMenuSellItemPaneItemNameField;
    @FXML TextField mainMenuSellItemPaneItemDescriptionField;
    @FXML TextField mainMenuSellItemPaneStartingPriceField;
    @FXML TextField mainMenuSellItemPanePriceIncrementField;

    @FXML private TableView<com.auction.shared.models.Item> sellerItemsTable;
    @FXML private TableColumn<com.auction.shared.models.Item, String> colName;
    @FXML private TableColumn<com.auction.shared.models.Item, String> colType;
    @FXML private TableColumn<com.auction.shared.models.Item, java.math.BigDecimal> colStartPrice;
    @FXML private TableColumn<com.auction.shared.models.Item, java.math.BigDecimal> colIncrement;
    private String editingItemId = null;

    @FXML private ComboBox<String> itemTypeComboBox;
    //TODO: Cần thêm Comobox cho phép chọn loại vật phẩm đấu giá (Artworks, Vehicle, Electronic items)

    @FXML
    public void initialize() {
        if (itemTypeComboBox != null) {
            itemTypeComboBox.getItems().addAll("Artwork", "Electronics", "Vehicle");
            itemTypeComboBox.getSelectionModel().selectFirst();
        }
        colName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colType.setCellValueFactory(cellData -> {
            String className = cellData.getValue().getClass().getSimpleName();
            if ("Art".equals(className)) return new javafx.beans.property.SimpleStringProperty("Artwork");
            return new javafx.beans.property.SimpleStringProperty(className);
        });
        colStartPrice.setCellValueFactory(new PropertyValueFactory<>("startingPrice"));
        colIncrement.setCellValueFactory(new PropertyValueFactory<>("priceIncrement"));
        handleRefreshList();
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

            BigDecimal startingPrice = new BigDecimal(mainMenuSellItemPaneStartingPriceField.getText().trim());
            BigDecimal currentPrice = startingPrice;
            BigDecimal priceIncrement = new BigDecimal(mainMenuSellItemPanePriceIncrementField.getText().trim());

            User currentUser = AccountEventHandler.getCurrentUser();
            if (currentUser == null) {
                showError("Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại!");
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
            newItem.setPriceIncrement(priceIncrement);

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

    @FXML
    private void handleRefreshList() {
        com.auction.shared.models.User currentUser = com.auction.client.Models.AccountEventHandler.getCurrentUser();
        if (currentUser != null) {
            ArrayList<com.auction.shared.models.Item> myList = com.auction.client.Models.AccountEventHandler.getSellerItems(currentUser.getId());
            sellerItemsTable.setItems(FXCollections.observableArrayList(myList));
        }
    }

    @FXML
    private void handleDeleteItem(ActionEvent event) {
        com.auction.shared.models.Item selectedItem = sellerItemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showError("Vui lòng chọn một vật phẩm trong bảng để xóa");
            return;
        }
        String result = com.auction.client.Models.AccountEventHandler.deleteItem(selectedItem.getId());
        if ("success".equals(result)) {
            showSuccess("Đã xóa vật phẩm thành công");
            handleRefreshList();
        } else {
            showError("Xóa thất bại");
        }
    }

    @FXML
    private void handleEditItem(ActionEvent event) {
        com.auction.shared.models.Item selectedItem = sellerItemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showError("Vui lòng chọn một vật phẩm để sửa");
            return;
        }
        mainMenuSellItemPaneItemNameField.setText(selectedItem.getItemName());
        mainMenuSellItemPaneItemDescriptionField.setText(selectedItem.getDescription());
        mainMenuSellItemPaneStartingPriceField.setText(selectedItem.getStartingPrice().toString());
        mainMenuSellItemPanePriceIncrementField.setText(selectedItem.getPriceIncrement().toString());

        String className = selectedItem.getClass().getSimpleName();
        itemTypeComboBox.setValue("Art".equals(className) ? "Artwork" : className);
        editingItemId = selectedItem.getId();
        showSuccess("Đã nạp dữ liệu lên Form. Sửa xong hãy bấm nút 'Lưu cập nhật'");
    }

    @FXML
    private void handleSaveUpdate(ActionEvent event) {
        if (editingItemId == null) {
            showError("Chưa chọn vật phẩm nào để cập nhật");
            return;
        }
        try {
            String name = mainMenuSellItemPaneItemNameField.getText().trim();
            String description = mainMenuSellItemPaneItemDescriptionField.getText().trim();
            java.math.BigDecimal startingPrice = new java.math.BigDecimal(mainMenuSellItemPaneStartingPriceField.getText().trim());
            java.math.BigDecimal priceIncrement = new java.math.BigDecimal(mainMenuSellItemPanePriceIncrementField.getText().trim());

            com.auction.shared.models.User currentUser = com.auction.client.Models.AccountEventHandler.getCurrentUser();
            com.auction.shared.models.Item updatedItem;
            String typeOfItem = itemTypeComboBox.getValue();
            if ("Artwork".equals(typeOfItem)) {
                updatedItem = new com.auction.shared.models.Art(name, description, startingPrice, startingPrice);
            } else if ("Electronics".equals(typeOfItem)) {
                updatedItem = new com.auction.shared.models.Electronics(name, description, startingPrice, startingPrice);
            } else {
                updatedItem = new com.auction.shared.models.Vehicle(name, description, startingPrice, startingPrice);
            }
            updatedItem.setId(editingItemId);
            updatedItem.setSellerId(currentUser.getId());
            updatedItem.setPriceIncrement(priceIncrement);

            String result = com.auction.client.Models.AccountEventHandler.updateItem(updatedItem);
            if ("success".equals(result)) {
                showSuccess("Cập nhật vật phẩm thành công");
                editingItemId = null;
                clearFields();
                handleRefreshList();
            } else {
                showError("Cập nhật thất bại");
            }
        } catch (Exception e) {
            showError("Lỗi dữ liệu nhập vào: " + e.getMessage());
        }
    }
}