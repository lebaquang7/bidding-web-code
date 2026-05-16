package com.auction.server;

import com.auction.shared.models.*;

import java.sql.*;

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
    public static User findUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                String pass = rs.getString("password");
                String id = rs.getString("id");

                // Trả về đúng loại Object dựa trên role trong database
                return switch (role) {
                    case "Admin" -> new Admin(
                            username, pass,
                            rs.getInt("accessLevel"),
                            rs.getString("department"),
                            rs.getString("internalEmployeeId")
                    );

                    case "Seller" -> new Seller(username, pass);
                    default -> new Bidder(
                            username, pass,
                            rs.getString("shippingAddress"),
                            rs.getDouble("balance"),
                            rs.getInt("reputationScore")
                    );
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Lưu User vào database khi đăng ký
    public static boolean saveNewUser(User user) {
        String sql = "INSERT INTO users (id, username, password, role, balance, email, shippingAddress, reputationScore, accessLevel, department, internalEmployeeId) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, user.getId());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(6, user.getEmail());

            if (user instanceof Admin admin) {
                preparedStatement.setString(4, "Admin");
                preparedStatement.setDouble(5, 0.0);
                preparedStatement.setNull(7, java.sql.Types.VARCHAR);
                preparedStatement.setNull(8, java.sql.Types.INTEGER);
                preparedStatement.setInt(9, admin.getAccessLevel());
                preparedStatement.setString(10, admin.getDepartment());
                preparedStatement.setString(11, admin.getInternalEmployeeId());
            } else if (user instanceof Bidder bidder) {
                preparedStatement.setString(4, "Bidder");
                preparedStatement.setDouble(5, bidder.getBalance());
                preparedStatement.setString(7, bidder.getShippingAddress());
                preparedStatement.setInt(8, bidder.getReputationScore());
                preparedStatement.setNull(9, java.sql.Types.INTEGER);
                preparedStatement.setNull(10, java.sql.Types.VARCHAR);
                preparedStatement.setNull(11, java.sql.Types.VARCHAR);
            } else if (user instanceof Seller seller) {
                preparedStatement.setString(4, "Seller");
                preparedStatement.setDouble(5, java.sql.Types.DOUBLE);
                preparedStatement.setNull(7, java.sql.Types.VARCHAR);
                preparedStatement.setNull(8, java.sql.Types.INTEGER);
                preparedStatement.setNull(9, java.sql.Types.INTEGER);
                preparedStatement.setNull(10, java.sql.Types.VARCHAR);
                preparedStatement.setNull(11, java.sql.Types.VARCHAR);
            }
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi đăng ký: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    //Thêm vật phẩm vào database khi bán vật phẩm
    public static boolean saveNewItem(Item item) {
        String sqlItem = "INSERT INTO items (id, name, description, startingPrice, currentPrice, itemType) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlSub = "";

        if (item instanceof Art) {
            sqlSub = "INSERT INTO artworks (itemId, artistName, isOriginal, creationYear, medium) VALUES (?, ?, ?, ?, ?)";
        } else if (item instanceof Electronics) {
            sqlSub = "INSERT INTO electronic_items (itemId, brand, model, warrantyMonths, itemCondition) VALUES (?, ?, ?, ?, ?)";
        } else if (item instanceof Vehicle) {
            sqlSub = "INSERT INTO vehicle (itemId, licensePlate, mileage, manufacturingYear) VALUES (?, ?, ?, ?)";
        }

        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            //Lưu vào bảng chung items
            try (PreparedStatement psItem = connection.prepareStatement(sqlItem)) {
                psItem.setString(1, item.getId()); // ID sinh ra từ Entity
                psItem.setString(2, item.getItemName());
                psItem.setString(3, item.getDescription());
                psItem.setDouble(4, item.getStartingPrice());
                psItem.setDouble(5, item.getCurrentPrice());

                String type = item instanceof Art ? "Art" : (item instanceof Electronics ? "Electronics" : "Vehicle");
                psItem.setString(6, type);
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
                } else if (item instanceof Electronics ele) {
                    psSub.setString(2, ele.getBrand());
                    psSub.setString(3, ele.getModel());
                    psSub.setInt(4, ele.getWarrantyMonths());
                    psSub.setString(5, ele.getCondition());
                } else if (item instanceof Vehicle veh) {
                    psSub.setString(2, veh.getLicensePlate());
                    psSub.setInt(3, veh.getMileage());
                    psSub.setInt(4, veh.getManufacturingYear());
                }
                psSub.executeUpdate();
            }
            connection.commit(); // Xác nhận lưu cả 2 bảng thành công
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi bán sản phẩm: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null) {
                try {connection.setAutoCommit(true); connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
        }

    }
}