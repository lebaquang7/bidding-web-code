package com.auction.server;

import com.auction.shared.models.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseConfig {
  // Kết nối với database
  // Sửa localhost
  // private static final String URL = "jdbc:mysql://192.168.x.x:3306/auction_system";

  private static final String URL = "jdbc:mysql://localhost:3306/auction_system";
  private static final String USER = "root";
  private static final String PASS = "";

  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASS);
  }

  // Tìm User khi đăng nhập
  public static User findUserByUsername(String username) {
    // 1. Chỉ tìm trong bảng users trước để kiểm tra tài khoản có tồn tại không
    String sql = "SELECT * FROM users WHERE username = ?";

    try (Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, username);
      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
        String role = rs.getString("role");
        String pass = rs.getString("password");
        String id = rs.getString("id");

        User user = null;
        if ("Bidder".equals(role)) {
          String sqlBidder = "SELECT * FROM bidders WHERE id = ?";
          try (PreparedStatement psBidder = conn.prepareStatement(sqlBidder)) {
            psBidder.setString(1, id);
            ResultSet rsBidder = psBidder.executeQuery();
            if (rsBidder.next()) {
              user =
                  new Bidder(
                      username,
                      pass,
                      rsBidder.getString("shippingAddress"),
                      rsBidder.getBigDecimal("balance"),
                      rsBidder.getInt("reputationScore"));
            }
          }
        }
        if ("Admin".equals(role)) {
          String sqlAdmin = "SELECT * FROM admins WHERE id = ?";
          try (PreparedStatement psAdmin = conn.prepareStatement(sqlAdmin)) {
            psAdmin.setString(1, id);
            ResultSet rsAdmin = psAdmin.executeQuery();
            if (rsAdmin.next()) {
              user =
                  new Admin(
                      username,
                      pass,
                      rsAdmin.getInt("accessLevel"),
                      rsAdmin.getString("department"),
                      rsAdmin.getString("internalEmployeeId"));
            }
          }
        }
        if ("Seller".equals(role)) {
          String sqlSeller = "SELECT * FROM sellers WHERE id = ?";
          try (PreparedStatement psSeller = conn.prepareStatement(sqlSeller)) {
            psSeller.setString(1, id);
            ResultSet rsSeller = psSeller.executeQuery();
            if (rsSeller.next()) {
              user = new Seller(username, pass);
            }
          }
        }

        if (user != null) {
          user.setId(id);
        }
        return user;
      }

    } catch (SQLException e) {
      System.err.print("Lỗi khi tìm User: " + e.getMessage());
    }

    return null;
  }

  // Lưu User vào database khi đăng ký
  public static boolean saveNewUser(User user) {
    String sqlUser = "INSERT INTO users (id, username, password, role) VALUES (?, ?, ?, ?)";
    String sqlSub = "";

    if (user instanceof Admin) {
      sqlSub =
          "INSERT INTO admins (id, accessLevel, department, internalEmployeeId) VALUES (?, ?, ?, ?)";
    } else if (user instanceof Bidder) {
      sqlSub =
          "INSERT INTO bidders (id, shippingAddress, balance, reputationScore) VALUES (?, ?, ?, ?)";
    } else if (user instanceof Seller) {
      sqlSub = "INSERT INTO sellers (id) VALUES (?)";
    }

    Connection connection = null;
    try {
      connection = getConnection();
      connection.setAutoCommit(false);

      try (PreparedStatement psUser = connection.prepareStatement(sqlUser)) {
        psUser.setString(1, user.getId());
        psUser.setString(2, user.getUserName());
        psUser.setString(3, user.getPassword());
        psUser.setString(4, user.getClass().getSimpleName());
        psUser.executeUpdate();
      }

      try (PreparedStatement psSub = connection.prepareStatement(sqlSub)) {
        psSub.setString(1, user.getId()); // Khóa ngoại đồng bộ id

        if (user instanceof Admin admin) {
          psSub.setInt(2, admin.getAccessLevel());
          psSub.setString(3, admin.getDepartment());
          psSub.setString(4, admin.getInternalEmployeeId());
        } else if (user instanceof Bidder bidder) {
          psSub.setString(2, bidder.getShippingAddress());
          psSub.setBigDecimal(3, bidder.getBalance());
          psSub.setInt(4, bidder.getReputationScore());
        } else if (user instanceof Seller seller) {
          // Để nếu thêm thuộc tính cho seller thì sửa
        }
        psSub.executeUpdate();
      }
      connection.commit();
      return true;

    } catch (SQLException e) {
      if (connection != null) {
        try {
          connection.rollback();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }
      System.err.println("Lỗi SQL khi đăng ký: " + e.getMessage());
      return false;
    } finally {
      if (connection != null)
        try {
          connection.setAutoCommit(true);
          connection.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
    }
  }

  // Thêm vật phẩm vào database khi bán vật phẩm
  public static boolean saveNewItem(Item item) {
    String sqlItem =
        "INSERT INTO items (id, type, name, description, starting_price, current_price, seller_Id, price_Increment, image_path, duration_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    String sqlSub = "";

    if (item instanceof Art) {
      sqlSub =
          "INSERT INTO artworks (id, artistName, isOriginal, creationYear, medium) VALUES (?, ?, ?, ?, ?)";
    } else if (item instanceof Electronics) {
      sqlSub =
          "INSERT INTO electronic_items (id, brand, model, warrantyMonths, itemCondition) VALUES (?, ?, ?, ?, ?)";
    } else if (item instanceof Vehicle) {
      sqlSub =
          "INSERT INTO vehicle (id, licensePlate, mileage, manufacturingYear) VALUES (?, ?, ?, ?)";
    }

    Connection connection = null;
    try {
      connection = getConnection();
      connection.setAutoCommit(false);

      // Lưu vào bảng chung items
      try (PreparedStatement psItem = connection.prepareStatement(sqlItem)) {
        psItem.setString(1, item.getId()); // ID sinh ra từ Entity

        String type =
            item instanceof Art ? "Art" : (item instanceof Electronics ? "Electronics" : "Vehicle");
        psItem.setString(2, type);
        psItem.setString(3, item.getItemName());
        psItem.setString(4, item.getDescription());
        psItem.setBigDecimal(5, item.getStartingPrice());
        psItem.setBigDecimal(6, item.getStartingPrice());
        psItem.setString(7, item.getSellerId());
        psItem.setBigDecimal(8, item.getPriceIncrement());
        psItem.setString(9, item.getImagePath());
        psItem.setInt(10, item.getDurationTime());

        psItem.executeUpdate();
      }

      // Lưu vào bảng ứng với lớp con
      try (PreparedStatement psSub = connection.prepareStatement(sqlSub)) {
        psSub.setString(1, item.getId()); // Khóa ngoại trỏ về items(id)

        if (item instanceof Art art) {
          psSub.setString(2, art.getArtistName());
          psSub.setBoolean(3, art.getIsOriginal());
          psSub.setInt(4, art.getCreationYear());
          psSub.setString(5, art.getMedium());
        } else if (item instanceof Electronics electronics) {
          psSub.setString(2, electronics.getBrand());
          psSub.setString(3, electronics.getModel());
          psSub.setInt(4, electronics.getWarrantyMonths());
          psSub.setString(5, electronics.getCondition());
        } else if (item instanceof Vehicle vehicle) {
          psSub.setString(2, vehicle.getLicensePlate());
          psSub.setInt(3, vehicle.getMileage());
          psSub.setInt(4, vehicle.getManufacturingYear());
        }
        psSub.executeUpdate();
      }
      connection.commit(); // Xác nhận lưu cả 2 bảng thành công
      return true;
    } catch (SQLException e) {
      // rollback để hủy lưu nếu có lỗi chỉ lưu được bảng 1 nhưng bảng 2 không được
      if (connection != null) {
        try {
          connection.rollback();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }
      System.err.println("Lỗi SQL khi bán sản phẩm: " + e.getMessage());
      return false;
    } finally {
      if (connection != null) {
        try {
          connection.setAutoCommit(true);
          connection.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }

  // Hàm lấy tất cả vật phẩm trong DB để đưa lên giao diện
  public static List<Item> getAllItems() {
    List<Item> items = new ArrayList<>();

    String sql = "SELECT * FROM items";

    try (Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {

      while (rs.next()) {
        String id = rs.getString("id");
        String type = rs.getString("type");
        String name = rs.getString("name");
        String description = rs.getString("description");
        BigDecimal startingPrice = rs.getBigDecimal("starting_price");
        BigDecimal currentPrice = rs.getBigDecimal("current_price");
        String sellerId = rs.getString("seller_Id");
        BigDecimal priceIncrement = rs.getBigDecimal("price_Increment");
        String imagePath = rs.getString("image_path");
        String highestBidderId = rs.getString("highest_Bidder_Id");
        int durationTime = rs.getInt("duration_time");
        Timestamp startTime = rs.getTimestamp("start_time");
        Timestamp endTime = rs.getTimestamp("end_time");


        Item item = null;
        if ("Art".equals(type)) {
          String sqlArtwork = "SELECT * FROM artworks WHERE id = ?";
          try (PreparedStatement psSub = connection.prepareStatement(sqlArtwork)) {
            psSub.setString(1, id);
            try (ResultSet rsSub = psSub.executeQuery()) {
              if (rsSub.next()) {
                String artistName = rsSub.getString("artistName");
                boolean isOriginal = rsSub.getBoolean("isOriginal");
                int creationYear = rsSub.getInt("creationYear");
                String medium = rsSub.getString("medium");

                item =
                    new Art(
                        name,
                        description,
                        startingPrice,
                        currentPrice,
                        artistName,
                        isOriginal,
                        creationYear,
                        medium);
              }
            }
          }
        } else if ("Electronics".equals(type)) {
          String sqlElectronics = "SELECT * FROM electronic_items WHERE id = ?";
          try (PreparedStatement psSub = connection.prepareStatement(sqlElectronics)) {
            psSub.setString(1, id);
            try (ResultSet rsSub = psSub.executeQuery()) {
              if (rsSub.next()) {
                String brand = rsSub.getString("brand");
                String model = rsSub.getString("model");
                int warrantyMonths = rsSub.getInt("warrantyMonths");
                String itemCondition = rsSub.getString("itemCondition");

                item =
                    new Electronics(
                        name,
                        description,
                        startingPrice,
                        currentPrice,
                        warrantyMonths,
                        itemCondition,
                        brand,
                        model);
              }
            }
          }
        } else if ("Vehicle".equals(type)) {
          String sqlVehicle = "SELECT * FROM vehicle WHERE id = ?";
          try (PreparedStatement psSub = connection.prepareStatement(sqlVehicle)) {
            psSub.setString(1, id);
            try (ResultSet rsSub = psSub.executeQuery()) {
              if (rsSub.next()) {
                String licensePlate = rsSub.getString("licensePlate");
                int mileage = rsSub.getInt("mileage");
                int manufacturingYear = rsSub.getInt("manufacturingYear");

                item = new Vehicle(
                        name,
                        description,
                        startingPrice,
                        currentPrice,
                        licensePlate,
                        mileage,
                        manufacturingYear);
              }
            }
          }
        }

        if (item != null) {
          item.setId(id);
          item.setSellerId(sellerId);
          item.setHighestBidderId(highestBidderId);
          item.setPriceIncrement(priceIncrement);
          item.setImagePath(imagePath);
          item.setDurationTime(durationTime);
          if (startTime != null) {item.setStartTime(startTime.toLocalDateTime());}
          if (endTime != null) {item.setEndTime(endTime.toLocalDateTime());}
          items.add(item);
        }
      }
    } catch (SQLException e) {
      System.err.println("Lỗi lấy danh sách sản phẩm: " + e.getMessage());
    }
    return items;
  }

  // Hàm lấy 1 vật phẩm duy nhất (Dùng cho BiddingService)
  public static Item getItemById(String itemId) {
    String sql = "SELECT * FROM items WHERE id = ?";
    try (Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {

      ps.setString(1, itemId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          String id = rs.getString("id");
          String type = rs.getString("type");
          String name = rs.getString("name");
          String description = rs.getString("description");
          BigDecimal startingPrice = rs.getBigDecimal("starting_price");
          BigDecimal currentPrice = rs.getBigDecimal("current_price");
          String sellerId = rs.getString("seller_Id");
          BigDecimal priceIncrement = rs.getBigDecimal("price_Increment");
          String highestBidderId = rs.getString("highest_Bidder_Id");
          int durationTime = rs.getInt("duration_time");
          Timestamp startTime = rs.getTimestamp("start_time");
          Timestamp endTime = rs.getTimestamp("end_time");

          Item item = null;
          if ("Art".equals(type)) {
            String sqlArt = "SELECT * FROM artworks WHERE id = ?";
            try (PreparedStatement psSub = connection.prepareStatement(sqlArt)) {
              psSub.setString(1, id);
              try (ResultSet rsSub = psSub.executeQuery()) {
                if (rsSub.next()) {
                  String artistName = rsSub.getString("artistName");
                  int creationYear = rsSub.getInt("creationYear");
                  String medium = rsSub.getString("medium");
                  Boolean isOriginal = rsSub.getBoolean("isOriginal");
                  item =
                      new Art(
                          name,
                          description,
                          startingPrice,
                          currentPrice,
                          artistName,
                          isOriginal,
                          creationYear,
                          medium);
                }
              }
            }
          } else if ("Electronics".equals(type)) {
            String sqlElec = "SELECT * FROM electronic_items WHERE id = ?";
            try (PreparedStatement psSub = connection.prepareStatement(sqlElec)) {
              psSub.setString(1, id);
              try (ResultSet rsSub = psSub.executeQuery()) {
                if (rsSub.next()) {
                  int warrantyMonths = rsSub.getInt("warrantyMonths");
                  String itemCondition = rsSub.getString("itemCondition");
                  String brand = rsSub.getString("brand");
                  String model = rsSub.getString("model");
                  item =
                      new Electronics(
                          name,
                          description,
                          startingPrice,
                          currentPrice,
                          warrantyMonths,
                          itemCondition,
                          brand,
                          model);
                }
              }
            }
          } else if ("Vehicle".equals(type)) {
            String sqlVehicle = "SELECT * FROM vehicle WHERE id = ?";
            try (PreparedStatement psSub = connection.prepareStatement(sqlVehicle)) {
              psSub.setString(1, id);
              try (ResultSet rsSub = psSub.executeQuery()) {
                if (rsSub.next()) {
                  String licensePlate = rsSub.getString("licensePlate");
                  int mileage = rsSub.getInt("mileage");
                  int manufacturingYear = rsSub.getInt("manufacturingYear");
                  item =
                      new Vehicle(
                          name,
                          description,
                          startingPrice,
                          currentPrice,
                          licensePlate,
                          mileage,
                          manufacturingYear);
                }
              }
            }
          }

          if (item != null) {
            item.setId(id);
            item.setSellerId(sellerId);
            item.setPriceIncrement(priceIncrement);
            item.setDurationTime(durationTime);
            if (startTime != null) {item.setStartTime(startTime.toLocalDateTime());}
            if (endTime != null) {item.setEndTime(endTime.toLocalDateTime());}
            item.setHighestBidderId(highestBidderId);
          }
          return item;
        }
      }
    } catch (SQLException e) {
      System.err.println("Lỗi khi tìm vật phẩm theo ID: " + e.getMessage());
    }
    return null;
  }

  // Cập nhật startTime / endTime
  public static void updateAuctionTimer(String itemId, LocalDateTime startTime, LocalDateTime endTime) {
    String sql = "UPDATE items SET start_time = ?, end_time = ? WHERE id = ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setTimestamp(1, startTime != null ? Timestamp.valueOf(startTime) : null);
      pstmt.setTimestamp(2, endTime != null ? Timestamp.valueOf(endTime) : null);
      pstmt.setString(3, itemId);

      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // Hàm thực hiện yêu cầu lưu lịch sử đấu giá
  public static boolean executeBidTransaction(String itemId, String bidderId, BigDecimal amount) {
    String insertHistorySql =
        "INSERT INTO bid_history (item_id, bidder_id, bid_amount, bid_time) VALUES (?, ?, ?, NOW())";
    String updateItemSql = "UPDATE items SET current_price = ?, highest_bidder_id = ? WHERE id = ?";

    Connection conn = null;
    try {
      conn = getConnection();
      conn.setAutoCommit(false);

      // 1. Ghi vào lịch sử đấu giá
      try (PreparedStatement psHistory = conn.prepareStatement(insertHistorySql)) {
        psHistory.setString(1, itemId);
        psHistory.setString(2, bidderId);
        psHistory.setBigDecimal(3, amount);
        psHistory.executeUpdate();
      }

      // 2. Cập nhật thông tin mới nhất cho vật phẩm
      try (PreparedStatement psUpdate = conn.prepareStatement(updateItemSql)) {
        psUpdate.setBigDecimal(1, amount);
        psUpdate.setString(2, bidderId);
        psUpdate.setString(3, itemId);
        psUpdate.executeUpdate();
      }

      Item currentItem = getItemById(itemId);
      if (amount.compareTo(currentItem.getCurrentPrice()) <= 0) {
        return false;
      }

      conn.commit(); // Thành công hết thì mới lưu
      return true;
    } catch (SQLException e) {
      if (conn != null) {
        try {
          conn.rollback();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }
      e.printStackTrace();
      return false;
    } finally {
      if (conn != null) {
        try {
          conn.setAutoCommit(true);
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static boolean updateItemEndTime(String itemId, LocalDateTime newEndTime) {
    String sql = "UPDATE items SET end_time = ? WHERE id = ?";

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setTimestamp(1, java.sql.Timestamp.valueOf(newEndTime));
      pstmt.setString(2, itemId);

      return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
      System.err.println("Lỗi cập nhật thời gian: " + e.getMessage());
      return false;
    }
  }

  // Cập nhật trạng thái phiên đấu giá
  public static boolean updateAuctionStatus(String itemId, AuctionStatus status) {
    String sql = "UPDATE items SET status = ? WHERE id = ?";
    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, status.toString());
      pstmt.setString(2, itemId);

      int affectedRows = pstmt.executeUpdate();
      return affectedRows > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // Kiểm tra trạng thái phiên đấu giá
  public static boolean isAuctionRunningInDB(String itemId) {
    String sql = "SELECT status FROM items WHERE id = ?";
    try (Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, itemId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return "RUNNING".equals(rs.getString("status"));
        }
      }
    } catch (SQLException e) {
      System.err.println("Lỗi khi check status DB: " + e.getMessage());
    }
    return false;
  }

  // Lấy thông tin User thắng cuộc khi hết giờ
  public static Bidder getWinnerFromHistory(String itemId) {
    String sql = "SELECT bidder_id FROM bid_history WHERE item_id = ? ORDER BY bid_amount DESC LIMIT 1";
    try (Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, itemId);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          String bidderId = rs.getString("bidder_id");
          User user = findUserById(bidderId); // Giả định bạn đã có hàm tìm user theo ID
          if (user instanceof Bidder) {
            return (Bidder) user;
          }
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static List<BidTransaction> getAllBidHistory() {
    String sql = "SELECT * FROM bid_history";
    List<BidTransaction> bidHistory = new ArrayList<>();
    try (Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        String itemId = rs.getString("item_Id");
        String bidderId = rs.getString("bidder_Id");
        BigDecimal bidAmount = rs.getBigDecimal("bid_amount");
        LocalDateTime bidTime = rs.getTimestamp("bid_time").toLocalDateTime();
        BidTransaction bidTransaction = new BidTransaction(itemId, bidderId, bidAmount, bidTime);
        bidHistory.add(bidTransaction);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
    return bidHistory;
  }

  public static HashMap<String, AuctionStatus> getAuctionState() {
    String sql = "SELECT * FROM items";
    HashMap<String, AuctionStatus> itemStatusHashMap = new HashMap<>();
    try (Connection conn = getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        AuctionStatus auctionStatus = AuctionStatus.UNKNOWN;
        switch (rs.getString("status")) {
          case "PENDING_APPROVAL" -> auctionStatus = AuctionStatus.PENDING_APPROVAL;
          case "RUNNING" -> auctionStatus = AuctionStatus.RUNNING;
          case "FINISHED" -> auctionStatus = AuctionStatus.FINISHED;
          case "CANCELLED" -> auctionStatus = AuctionStatus.CANCELLED;
          case "PAID" -> auctionStatus = AuctionStatus.PAID;
        }
        String itemId = rs.getString("id");
        itemStatusHashMap.put(itemId, auctionStatus);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return new HashMap<>();
    }
    return itemStatusHashMap;
  }

    public static User findUserById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                String username = rs.getString("username");
                String pass = rs.getString("password");

                User user = null;
                if ("Admin".equals(role)) {
                    String sqlAdmin = "SELECT * FROM admins WHERE id = ?";
                    try (PreparedStatement psAdmin = conn.prepareStatement(sqlAdmin)) {
                        psAdmin.setString(1, id);
                        ResultSet rsAdmin = psAdmin.executeQuery();
                        if (rsAdmin.next()) {
                            user = new Admin(
                                            username,
                                            pass,
                                            rsAdmin.getInt("accessLevel"),
                                            rsAdmin.getString("department"),
                                            rsAdmin.getString("internalEmployeeId"));
                        }
                    }
                }
                if ("Bidder".equals(role)) {
                    String sqlBidder = "SELECT * FROM bidders WHERE id = ?";
                    try (PreparedStatement psBidder = conn.prepareStatement(sqlBidder)) {
                        psBidder.setString(1, id);
                        ResultSet rsBidder = psBidder.executeQuery();
                        if (rsBidder.next()) {
                            user =
                                    new Bidder(
                                            username,
                                            pass,
                                            rsBidder.getString("shippingAddress"),
                                            rsBidder.getBigDecimal("balance"),
                                            rsBidder.getInt("reputationScore"));
                        }
                    }
                }
                if ("Seller".equals(role)) {
                    String sqlSeller = "SELECT * FROM sellers WHERE id = ?";
                    try (PreparedStatement psSeller = conn.prepareStatement(sqlSeller)) {
                        psSeller.setString(1, id);
                        ResultSet rsSeller = psSeller.executeQuery();
                        if (rsSeller.next()) {
                            user = new Seller(username, pass);
                        }
                    }
                }

              if (user != null) {
                user.setId(id);
              }
              return user;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}
