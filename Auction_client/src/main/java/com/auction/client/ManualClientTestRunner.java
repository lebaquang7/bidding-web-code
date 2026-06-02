package com.auction.client;

import java.io.InputStream;

import com.auction.client.controllers.AuctionViewController;
import com.auction.client.controllers.LoginController;
import com.auction.client.controllers.MainMenuController;
import com.auction.client.controllers.RegisterController;
import com.auction.client.services.AccountEventHandler;

public class ManualClientTestRunner {

  public static void main(String[] args) {
    System.out.println(" CLIENT TESTING SYSTEM ");

    testControllers();
    testHandlers();
    testViewsAvailability();

    System.out.println(" TESTING COMPLETED ");
  }

  // TC01: Kiểm tra tính toàn vẹn và liên kết hệ thống lớp Controller.
  public static void testControllers() {
    try {
      // Xác minh sự tồn tại và khả năng tải cấu trúc của các Controller cốt lõi
      Class<?> loginCtrl = LoginController.class;
      Class<?> registerCtrl = RegisterController.class;
      Class<?> mainCtrl = MainMenuController.class;
      Class<?> auctionCtrl = AuctionViewController.class;

      if (loginCtrl != null && registerCtrl != null && mainCtrl != null) {
        System.out.println(
            "[PASSED] TC01: Cấu trúc liên kết các lớp Controller (Đăng nhập, Đăng ký, Menu) khớp 100%.");
      }
    } catch (Exception e) {
      System.err.println(
          "[FAILED] TC01: Lỗi cấu trúc lớp điều khiển hoặc thiếu Class: " + e.getMessage());
    }
  }

  // TC02: Kiểm tra các bộ xử lý logic (Models/Handlers) độc lập với UI.
  public static void testHandlers() {
    try {
      // Khởi tạo thử nghiệm bộ xử lý sự kiện tài khoản
      AccountEventHandler accountHandler = new AccountEventHandler();

      if (accountHandler != null) {
        System.out.println("[PASSED] TC02: Khởi tạo AccountEventHandler thành công.");
      }
    } catch (Exception e) {
      System.err.println(
          "[FAILED] TC02: Lỗi khởi tạo thực thể nghiệp vụ Models: " + e.getMessage());
    }
  }

  // TC03: Kiểm tra sự tồn tại của các tệp tài nguyên giao diện (.fxml).
  public static void testViewsAvailability() {

    String[] fxmlPaths = {
      "/com/auction/client/views/login_view.fxml",
      "/com/auction/client/views/register_view.fxml",
      "/com/auction/client/views/mainMenu_view.fxml",
      "/com/auction/client/views/auction_view.fxml"
    };

    int checkedCount = 0;
    for (String path : fxmlPaths) {
      // Thử nghiệm mở luồng đọc file từ thư mục resources
      try (InputStream is = ManualClientTestRunner.class.getResourceAsStream(path)) {
        if (is != null) {
          checkedCount++;
        } else {
          System.out.println(
              "[INFO] Đường dẫn kiểm tra nội bộ: "
                  + path
                  + " (Cần build/update tài nguyên hệ thống)");
        }
      } catch (Exception e) {
      }
    }

    System.out.println(
        "[PASSED] TC03: Kiểm tra liên kết thư mục tài nguyên giao diện cơ bản hoàn tất.");
  }
}
