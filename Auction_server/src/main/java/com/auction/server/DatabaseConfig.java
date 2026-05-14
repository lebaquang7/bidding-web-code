package com.auction.server;

import com.auction.shared.models.Admin;
import com.auction.shared.models.Bidder;
import com.auction.shared.models.Seller;
import com.auction.shared.models.User;

import java.sql.*;

public class DatabaseConfig {
    // Kết nối vói database
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
                            username, pass, id,
                            rs.getInt("accessLevel"),
                            rs.getString("department"),
                            rs.getString("internalEmployeeId")
                    );

                    case "Seller" -> new Seller(username, pass, id);
                    default -> new Bidder(
                            username, pass, id,
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
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(6, user.getId());

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
            } else {
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
}