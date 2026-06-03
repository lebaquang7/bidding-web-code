package com.auction.client.controllers;

import com.auction.client.services.AccountEventHandler;
import com.auction.client.services.SceneHandler;
import com.auction.shared.models.Admin;
import com.auction.shared.models.Seller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class MainMenuController {
  // Controller Class cho màn hình chính
  // Đường dẫn đến view của controller này
  private static final String PATH_TO_VIEW = "/com/auction/client/views/mainMenu_view.fxml";

  public static String getPATH_TO_VIEW() {
    return PATH_TO_VIEW;
  }

  @FXML BorderPane mainBorderPane;
  @FXML Button sellItemButton;
  @FXML Button adminAuctionButton;

  /** Usage: chạy khi controller được gọi */
  public void initialize() {
    BorderPaneController.setMainLayout(mainBorderPane);
    BorderPaneController.setCenter(MainMenuAuctionListPaneController.getPATH_TO_VIEW());

    // Ẩn nút dựa trên loại người dùng
    if (!(AccountEventHandler.getCurrentUser() instanceof Seller
        || AccountEventHandler.getCurrentUser() instanceof Admin)) {
      sellItemButton.setVisible(false);
    }
    if (!(AccountEventHandler.getCurrentUser() instanceof Admin)) {
      adminAuctionButton.setVisible(false);
    }
  }

  // Kích cỡ trang trung tâm: 600H, 700W

  /**
   * Usage: Chuyển đến màn hình danh sách sản phẩm
   *
   * @param event
   */
  @FXML
  public void switchToAuctionList(ActionEvent event) {
    BorderPaneController.setCenter(MainMenuAuctionListPaneController.getPATH_TO_VIEW());
  }

  /**
   * Usage: Chuyển đến màn hình bán sản phẩm
   *
   * @param event
   */
  @FXML
  public void switchToSellItem(ActionEvent event) {
    BorderPaneController.setCenter(MainMenuSellItemPaneController.getPATH_TO_VIEW());
  }

  /**
   * Usage: Chuyển đến màn hình cài đặt
   *
   * @param event
   */
  @FXML
  public void switchToSettings(ActionEvent event) {
    BorderPaneController.setCenter(MainMenuSettingPaneController.getPATH_TO_VIEW());
  }

  /**
   * Usage: Chuyển đến màn hình chức năng admin
   *
   * @param event
   */
  @FXML
  public void switchToAdminFunctions(ActionEvent event) {
    BorderPaneController.setCenter(MainMenuAdminPaneController.getPATH_TO_VIEW());
  }

  /**
   * Usage: Đăng xuất
   *
   * @param event
   */
  @FXML
  public void logOut(ActionEvent event) {
    SceneHandler.switchToScene(getClass().getResource(LoginController.getPATH_TO_VIEW()), event);
  }
}
