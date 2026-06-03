package com.auction.client.controllers;

import com.auction.client.services.AccountEventHandler;
import com.auction.client.services.ItemFactory;
import com.auction.client.services.ItemsEventHandler;
import com.auction.client.utils.AlertMessageHandler;
import com.auction.client.utils.CurrencySelectorHandler;
import com.auction.shared.models.Item;
import com.auction.shared.models.Seller;
import com.auction.shared.models.User;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class MainMenuSellItemPaneController {
  // Controller class cho màn hình bán sản phẩm
  // Đường dẫn đến view của controller này
  private static final String PATH_TO_VIEW = "/com/auction/client/views/mainMenu_sellItemPane.fxml";

  public static String getPATH_TO_VIEW() {
    return PATH_TO_VIEW;
  }

  @FXML TextField itemNameField;
  @FXML TextField itemDescriptionField;
  @FXML TextField startingPriceField;
  @FXML TextField priceIncrementField;

  @FXML ComboBox<String> itemTypeComboBox;
  @FXML ComboBox<String> itemAuctionDuration;
  private File selectedImageFile; // Lưu file ảnh

  /** Usage: tự động chạy khi controller được gọi */
  public void initialize() {
    // Thêm phẩn từ cho combobox
    if (itemTypeComboBox != null) {
      itemTypeComboBox.getItems().addAll("Artwork", "Electronics", "Vehicle");
      itemTypeComboBox.getSelectionModel().selectFirst();
    }

    if (itemAuctionDuration != null) {
      itemAuctionDuration.getItems().addAll("2 minutes", "10 minutes", "30 minutes", "1 hour");
      itemAuctionDuration.setValue("2 minutes");
    }

    // Đặt nội dung prompt dựa trên loại tiền được chọn
    double convertedPrice =
        CurrencySelectorHandler.getInstance()
            .getConvertedPrice(BigDecimal.valueOf(100000))
            .doubleValue();
    String activeCurrency = CurrencySelectorHandler.getInstance().getActiveCurrency();
    startingPriceField.setPromptText(
        String.format(
            "Input starting price.... (Minimum of %.2f %s)", convertedPrice, activeCurrency));
  }

  @FXML
  /**
   * Usage: Đăng bán sản phẩm khi nhấn nút
   *
   * @param event
   */
  private void handleSubmitItem(ActionEvent event) {
    try {
      if (itemTypeComboBox.getValue() == null) {
        AlertMessageHandler.showError("Lỗi", "", "Chưa chọn loại vật phẩm");
        return;
      }

      int duration = 2;
      String selectedDuration = itemAuctionDuration.getValue();
      if (selectedDuration.contains("2 minutes")) { // Lựa chọn 2 phút cho video trình bày
        duration = 2;
      } else if (selectedDuration.contains("10 minutes")) {
        duration = 10;
      } else if (selectedDuration.contains("30 minutes")) {
        duration = 30;
      } else if (selectedDuration.contains("1 hour")) {
        duration = 60;
      }

      String name = itemNameField.getText();
      String description = itemDescriptionField.getText();
      BigDecimal startingPrice =
          CurrencySelectorHandler.getInstance()
              .getVNDPrice(BigDecimal.valueOf(Double.valueOf((startingPriceField.getText()))));
      if (startingPrice.doubleValue() < 100000) {
        AlertMessageHandler.showError(
            "Lỗi",
            "",
            ("Giá khởi đầu không được phép nhỏ hơn"
                + CurrencySelectorHandler.getInstance()
                    .getConvertedPrice(BigDecimal.valueOf(100000))
                + "  "
                + CurrencySelectorHandler.getInstance().getActiveCurrency()));
        return;
      }
      double percentage = Double.parseDouble(priceIncrementField.getText());
      if (percentage < 1 || percentage > 10) {
        AlertMessageHandler.showError(
            "Lỗi", "", "Bước tăng giá không được phép nhỏ hơn 1% hoặc lớn hơn 10%");
        return;
      }
      BigDecimal priceIncrement = BigDecimal.valueOf(percentage);

      User currentUser = AccountEventHandler.getCurrentUser();
      if (currentUser == null) {
        AlertMessageHandler.showError("Lỗi: ", "", "Không tìm thấy thông tin người dùng");
        return;
      } else if (!(currentUser instanceof Seller)) {
        AlertMessageHandler.showError("Lỗi: ", "", "Bạn không phải Seller");
      }

      String typeOfItem = itemTypeComboBox.getValue();
      Item newItem = ItemFactory.createItem(typeOfItem, name, description, startingPrice);

      if (selectedImageFile != null) {
        byte[] imageBytes =
            Files.readAllBytes(
                selectedImageFile.toPath()); // Chuyển file sang byte để gửi qua socket
        newItem.setImageBytes(imageBytes);
        newItem.setImagePath(selectedImageFile.getName()); // Lưu tên file để Server biết định dạng
      }
      newItem.setSellerId(currentUser.getId());
      newItem.setPriceIncrement(priceIncrement);
      newItem.setDurationTime(duration);

      String result = ItemsEventHandler.sellItem(newItem);
      if ("success".equals(result)) {
        AlertMessageHandler.showInfo("Thành công!", "", "Đưa vật phẩm lên sàn đấu giá thành công");
        clearFields();
      } else {
        AlertMessageHandler.showError("Lỗi", "", "Đăng bán thất bại");
      }

    } catch (NumberFormatException e) {
      AlertMessageHandler.showError("Lỗi", "", "Giá tiền không hợp lệ");
    } catch (Exception e) {
      AlertMessageHandler.showError("Lỗi", "", "Có lỗi xảy ra: " + e.getMessage());
    }
  }

  @FXML
  /**
   * Usage: Đăng hình ảnh khi nhấn nút
   *
   * @param event
   */
  private void handleUploadImage(ActionEvent event) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Chọn ảnh vật phẩm");

    // Lọc các file ảnh
    fileChooser
        .getExtensionFilters()
        .addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

    // Mở cửa sổ chọn file
    File file = fileChooser.showOpenDialog(itemNameField.getScene().getWindow());

    if (file != null) {
      // Kiểm tra dung lượng giới hạn 10MB
      if (file.length() > 10 * 1024 * 1024) {
        AlertMessageHandler.showError("Lỗi", "", "Ảnh phải có dung lượng dưới 10MB");
        return;
      }

      this.selectedImageFile = file;
      System.out.println("Đã chọn ảnh: " + file.getAbsolutePath());
    }
  }

  /** Usage: Dọn các trường thông tin */
  private void clearFields() {
    itemNameField.clear();
    itemDescriptionField.clear();
    startingPriceField.clear();
    priceIncrementField.clear();
    this.selectedImageFile = null;
  }
}
