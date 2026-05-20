package com.auction.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.auction.shared.models.Admin;
import com.auction.shared.models.Art;
import com.auction.shared.models.Bidder;
import com.auction.shared.models.Electronics;
import com.auction.shared.models.Item;
import com.auction.shared.models.Seller;
import com.auction.shared.models.User;
import com.auction.shared.models.Vehicle;

public class DatabaseConfig {
    // Kết nối vói database
    // Sửa localhost
    // private static final String URL = "jdbc:mysql://192.168.x.x:3306/auction_system";

    private static final String URL = "jdbc:mysql://localhost:3306/auction_system";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    //Tìm User khi đăng nhập
    // Tìm User khi đăng nhập
    public static User findUserByUsername(String username) {
        // 1. Chỉ tìm trong bảng users trước để kiểm tra tài khoản có tồn tại không
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("id");
                    String role = rs.getString("role");
                    String pass = rs.getString("password");
                    String email = rs.getString("email");

                    // 2. Tùy theo vai trò (role) để truy vấn bảng chi tiết tương ứng và khởi tạo đúng đối tượng
                    switch (role) {
                        case "Bidder" -> {
                            String sqlBidder = "SELECT * FROM bidders WHERE id = ?";
                            try (PreparedStatement psBidder = conn.prepareStatement(sqlBidder)) {
                                psBidder.setString(1, id);
                                try (ResultSet rsBidder = psBidder.executeQuery()) {
                                    if (rsBidder.next()) {
                                        return new Bidder(
                                                username, pass,
                                                rsBidder.getString("shippingAddress"),
                                                rsBidder.getDouble("balance"),
                                                rsBidder.getInt("reputationScore")
                                        );
                                    }
                                }
                            }
                        }
                        case "Admin" -> {
                            String sqlAdmin = "SELECT * FROM admins WHERE id = ?";
                            try (PreparedStatement psAdmin = conn.prepareStatement(sqlAdmin)) {
                                psAdmin.setString(1, id);
                                try (ResultSet rsAdmin = psAdmin.executeQuery()) {
                                    if (rsAdmin.next()) {
                                        return new Admin(
                                                username, pass,
                                                rsAdmin.getInt("accessLevel"),
                                                rsAdmin.getString("department"),
                                                rsAdmin.getString("adminCode")
                                        );
                                    }
                                }
                            }
                        }
                        case "Seller" -> {
                            String sqlSeller = "SELECT * FROM sellers WHERE id = ?";
                            try (PreparedStatement psSeller = conn.prepareStatement(sqlSeller)) {
                                psSeller.setString(1, id);
                                try (ResultSet rsSeller = psSeller.executeQuery()) {
                                    if (rsSeller.next()) {
                                        return new Seller(
                                                username, pass
                                        );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi xác thực đăng nhập: " + e.getMessage());
        }
        return null;
    }

    //Cần sửa lại sau khi cập nhật database, tách riêng từng loại User
    //Lưu User vào database khi đăng ký
    public static boolean saveNewUser(User user) {
        String sqlUser = "INSERT INTO users (id, username, password, role, email) VALUES (?, ?, ?, ?, ?)";
        String sqlSub = "";

        if (user instanceof Admin) {
            sqlSub = "INSERT INTO admins (id, accessLevel, department, internalEmployeeId) VALUES (?, ?, ?, ?)";
        } else if (user instanceof Bidder) {
            sqlSub = "INSERT INTO bidders (id, shippingAddress, balance, reputationScore) VALUES (?, ?, ?, ?)";
        } else if (user instanceof Seller) {
            sqlSub = "INSERT INTO sellers (id) VALUES (?)";
        }

        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement psUser = connection.prepareStatement(sqlUser)) {
                psUser.setString(1, user.getId());
                psUser.setString(2, user.getUsername());
                psUser.setString(3, user.getPassword());
                psUser.setString(4, user.getClass().getSimpleName());
                psUser.setString(5, user.getEmail());
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
                    psSub.setDouble(3, bidder.getBalance());
                    psSub.setInt(4, bidder.getReputationScore());
                } else if (user instanceof Seller seller) {
                    //Để nếu thêm thuộc tính cho seller thì sửa
                }
                psSub.executeUpdate();
            }
            connection.commit();
            return true;

        } catch (SQLException e) {
            if (connection != null) {
                try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            System.err.println("Lỗi SQL khi đăng ký: " + e.getMessage());
            return false;
        } finally {
            if (connection != null) try { connection.setAutoCommit(true); connection.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    //Thêm vật phẩm vào database khi bán vật phẩm
    public static boolean saveNewItem(Item item) {
        String sqlItem = "INSERT INTO items (id, type, name, description, starting_price, current_price, sellerId) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlSub = "";

        if (item instanceof Art) {
            sqlSub = "INSERT INTO artworks (id, artistName, isOriginal, creationYear, medium) VALUES (?, ?, ?, ?, ?)";
        } else if (item instanceof Electronics) {
            sqlSub = "INSERT INTO electronic_items (id, brand, model, warrantyMonths, itemCondition) VALUES (?, ?, ?, ?, ?)";
        } else if (item instanceof Vehicle) {
            sqlSub = "INSERT INTO vehicle (id, licensePlate, mileage, manufacturingYear) VALUES (?, ?, ?, ?)";
        }

        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            //Lưu vào bảng chung items
            try (PreparedStatement psItem = connection.prepareStatement(sqlItem)) {
                psItem.setString(1, item.getId()); // ID sinh ra từ Entity

                String type = item instanceof Art ? "Art" : (item instanceof Electronics ? "Electronics" : "Vehicle");
                psItem.setString(2, type);
                psItem.setString(3, item.getItemName());
                psItem.setString(4, item.getDescription());
                psItem.setBigDecimal(5, item.getStartingPrice());
                psItem.setBigDecimal(6, item.getCurrentPrice());
                psItem.setString(7, item.getSellerId());

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
            //rollback để hủy lưu nếu có lỗi chỉ lưu được bảng 1 nhưng bảng 2 không được
            if (connection != null) {
                try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            System.err.println("Lỗi SQL khi bán sản phẩm: " + e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                try {connection.setAutoCommit(true); connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
        }

    }
}