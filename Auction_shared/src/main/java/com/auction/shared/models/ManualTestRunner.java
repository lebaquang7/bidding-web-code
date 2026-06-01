package com.auction.shared.models;

import com.auction.shared.factory.ArtFactory;
import com.auction.shared.factory.ElectronicsFactory;
import com.auction.shared.factory.ItemFactory;
import com.auction.shared.factory.VehicleFactory;
import java.math.BigDecimal;

public class ManualTestRunner {

  public static void main(String[] args) {
    System.out.println(" SHARED TESTING SYSTEM ");
    testArtFactoryCreation();
    testElectronicsFactoryCreation();
    testVehicleFactoryCreation();
    testAdmin();
    testAuction();
    testBidTransaction();
    testEntity();
    testNetworkRequest();
    testSeller();
    testUser();

    System.out.println(" TESTING COMPLETED ");
  }

  // TC09: Kiểm tra quá trình sản xuất Tác phẩm nghệ thuật (ArtFactory).
  public static void testArtFactoryCreation() {
    ItemFactory artFactory = new ArtFactory();
    Item item =
        artFactory.createItem(
            "Tranh Đêm đầy sao",
            "Bản sao phục dựng",
            BigDecimal.valueOf(500.0),
            BigDecimal.valueOf(500.0));

    if (item instanceof Art && item.getItemName().equals("Tranh Đêm đầy sao")) {
      System.out.println("[PASSED] TC11: ArtFactory tạo đúng thực thể Art và lưu đúng thông tin.");
    } else {
      System.err.println("[FAILED] TC11: ArtFactory hoạt động sai.");
    }
  }

  // TC10: Kiểm tra quá trình sản xuất Đồ điện tử (ElectronicsFactory).
  public static void testElectronicsFactoryCreation() {
    ItemFactory elecFactory = new ElectronicsFactory();
    Item item =
        elecFactory.createItem(
            "iPhone 17 Pro Max",
            "Máy trần 99%",
            BigDecimal.valueOf(1000.0),
            BigDecimal.valueOf(1000.0));

    if (item instanceof Electronics && item.getId() != null && !item.getId().isEmpty()) {
      System.out.println(
          "[PASSED] TC12: ElectronicsFactory tạo thành công. UUID tự động sinh: " + item.getId());
    } else {
      System.err.println("[FAILED] TC12: Thiếu id sản phẩm.");
    }
  }

  // TC11: Kiểm tra quá trình sản xuất Phương tiện (VehicleFactory).
  public static void testVehicleFactoryCreation() {
    ItemFactory vehicleFactory = new VehicleFactory();
    Item item =
        vehicleFactory.createItem(
            "Xe điện Vinfast",
            "Xe chính chủ",
            BigDecimal.valueOf(200.0),
            BigDecimal.valueOf(200.0));

    if (item instanceof Vehicle
        && item.getStartingPrice().compareTo(BigDecimal.valueOf(200.0)) == 0) {
      System.out.println("[PASSED] TC13: VehicleFactory hoạt động chính xác.");
    } else {
      System.err.println("[FAILED] TC13: Lỗi tạo phương tiện ở VehicleFactory.");
    }
  }

  // TC12: Kiểm tra khởi tạo lớp Admin (Người quản trị).
  public static void testAdmin() {
    try {
      Admin admin = new Admin("admin01", "adminpass", 1, "Quản trị viên", "Hệ thống");

      if (admin != null && admin.getUserName().equals("admin01")) {
        System.out.println("[PASSED] TC14: Khởi tạo tài khoản quản trị Admin thành công.");
      } else {
        System.err.println("[FAILED] TC14: Khởi tạo Admin thành công nhưng sai thông tin.");
      }
    } catch (Exception e) {
      System.err.println("[FAILED] TC14: Lỗi khởi tạo Admin.");
    }
  }

  // TC13: Kiểm tra khởi tạo một Phiên đấu giá (Auction)
  public static void testAuction() {
    try {
      ItemFactory artFactory = new ArtFactory();
      Item item =
          artFactory.createItem(
              "Bình cổ", "Đồ gốm", BigDecimal.valueOf(300.0), BigDecimal.valueOf(300.0));

      User seller = new Seller("sellera", "password123");

      Auction auction = new Auction(1, item, BigDecimal.valueOf(300.0), seller, null, null);

      System.out.println("[PASSED] TC15: Khởi tạo phiên đấu giá Auction thành công.");
    } catch (Exception e) {
      System.err.println("[FAILED] TC15: Lỗi tạo phiên đấu giá Auction.");
    }
  }

  // TC14: Kiểm tra khởi tạo một Lịch sử ra giá (BidTransaction)
  public static void testBidTransaction() {
    try {

      ItemFactory artFactory = new ArtFactory();
      Item item =
          artFactory.createItem(
              "Bình cổ", "Đồ gốm", BigDecimal.valueOf(300.0), BigDecimal.valueOf(300.0));
      User seller = new Seller("sellera", "password123");
      Auction auction = new Auction(1, item, BigDecimal.valueOf(300.0), seller, null, null);

      Bidder bidder =
          new Bidder("nguyenvanc", "pass789", "Hải Phòng", BigDecimal.valueOf(500.0), 5);
      BidTransaction transaction =
          new BidTransaction(item.getId(), bidder.getId(), BigDecimal.valueOf(350.0));

      System.out.println("[PASSED] TC16: Khởi tạo giao dịch ra giá BidTransaction thành công.");
    } catch (Exception e) {
      System.err.println("[FAILED] TC16: Lỗi tạo giao dịch BidTransaction.");
    }
  }

  // TC15: Kiểm tra thực thể cơ sở Entity.
  public static void testEntity() {
    try {
      ItemFactory artFactory = new ArtFactory();
      Item item = artFactory.createItem("Bức họa", "Sơn dầu", BigDecimal.ONE, BigDecimal.ONE);

      if (item instanceof Entity) {
        System.out.println("[PASSED] TC17: Lớp cơ sở Entity được tích hợp và kế thừa thành công.");
      } else {
        System.err.println("[FAILED] TC17: Lỗi cấu trúc hệ thống! Item không kế thừa từ Entity.");
      }
    } catch (Exception e) {
      System.err.println("[FAILED] TC17: Lỗi khi thực hiện kiểm tra lớp Entity.");
    }
  }

  // TC16: Kiểm tra cấu trúc đóng gói tin nhắn mạng NetworkRequest.
  public static void testNetworkRequest() {
    try {
      NetworkRequest request =
          new NetworkRequest(NetworkRequest.requestType.values()[0], "Dữ liệu mẫu");

      if (request != null) {
        System.out.println("[PASSED] TC18: Khởi tạo mô hình đóng gói NetworkRequest thành công.");
      }
    } catch (Exception e) {
      System.err.println("[FAILED] TC18: Lỗi khởi tạo NetworkRequest.");
    }
  }

  // TC17: Kiểm tra khởi tạo tài khoản Người bán (Seller)
  public static void testSeller() {
    try {
      Seller seller = new Seller("sellera", "password123");

      if (seller.getUserName().equals("sellera")) {
        System.out.println("[PASSED] TC19: Khởi tạo tài khoản Seller thành công.");
      } else {
        System.err.println("[FAILED] TC19: Thông tin Seller sau khi khởi tạo bị sai.");
      }
    } catch (Exception e) {
      System.err.println("[FAILED] TC19: Lỗi hệ thống khi tạo Seller.");
    }
  }

  // TC18: Kiểm tra tính kế thừa của lớp cha User.
  public static void testUser() {
    User userSample = new Bidder("testuser", "securepwd", "Hà Nội", BigDecimal.valueOf(100.0), 0);

    if (userSample.getUserName().equals("testuser")
        && userSample.getPassword().equals("securepwd")) {
      System.out.println("[PASSED] TC20: Cơ chế kế thừa lớp cha User hoạt động chính xác.");
    } else {
      System.err.println("[FAILED] TC20: Thuộc tính đọc từ lớp cha User bị sai lệch.");
    }
  }
}
