package com.auction.client.Controllers;

import java.io.File;
import java.math.BigDecimal;

import com.auction.client.Models.AccountEventHandler;
import com.auction.client.Models.CurrencySelectorHandler;
import com.auction.client.Models.ItemsEventHandler;
import com.auction.shared.models.Art;
import com.auction.shared.models.Electronics;
import com.auction.shared.models.Item;
import com.auction.shared.models.User;
import com.auction.shared.models.Vehicle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class MainMenuSellItemPaneController {
    @FXML TextField mainMenuSellItemPaneItemNameField;
    @FXML TextField mainMenuSellItemPaneItemDescriptionField;
    @FXML TextField mainMenuSellItemPaneStartingPriceField;
    @FXML TextField mainMenuSellItemPanePriceIncrementField;
    @FXML private Button uploadImageButton;

    @FXML private ComboBox<String> itemTypeComboBox;
    private File selectedImageFile; // Lưu file ảnh

    @FXML
    public void initialize() {
        if (itemTypeComboBox != null) {
            //TODO: elements based on actual available class types?
            itemTypeComboBox.getItems().addAll("Artwork", "Electronics", "Vehicle");
            itemTypeComboBox.getSelectionModel().selectFirst();
        }

        //set prompt text based on currency type. 
        double convertedPrice = CurrencySelectorHandler.getInstance().getConvertedPrice(BigDecimal.valueOf(100000)).doubleValue();
        String activeCurrency = CurrencySelectorHandler.getInstance().getActiveCurrency();
        mainMenuSellItemPaneStartingPriceField.setPromptText(String.format("Input starting price.... (Minimum of %.2f %s)", convertedPrice , activeCurrency));
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
            BigDecimal startingPrice = CurrencySelectorHandler.getInstance().getVNDPrice(BigDecimal.valueOf(Double.valueOf((mainMenuSellItemPaneStartingPriceField.getText()))));
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

            if (selectedImageFile != null) {
                byte[] imageBytes = java.nio.file.Files.readAllBytes(selectedImageFile.toPath()); // Chuyển file sang byte để gửi qua socket
                newItem.setImageBytes(imageBytes);
                newItem.setImagePath(selectedImageFile.getName()); // Lưu tên file để Server biết định dạng
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

    @FXML
    private void handleUploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh vật phẩm");

        // Lọc các file ảnh
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        // Mở cửa sổ chọn file
        File file = fileChooser.showOpenDialog(mainMenuSellItemPaneItemNameField.getScene().getWindow());

        if (file != null) {
            // Kiểm tra dung lượng giới hạn 10MB
            if (file.length() > 10 * 1024 * 1024) {
                showError("Ảnh phải có dung lượng dưới 10MB");
                return;
            }

            this.selectedImageFile = file;
            System.out.println("Đã chọn ảnh: " + file.getAbsolutePath());
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
        this.selectedImageFile = null;
    }
}
