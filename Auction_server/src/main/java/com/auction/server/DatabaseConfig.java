package com.auction.server;

import com.auction.server.models.*;
import java.sql.*;

public class DatabaseConfig {
    // Thay đổi thông số kết nối phù hợp với máy của bạn
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
                int id = rs.getInt("id");

                // Trả về đúng loại Object dựa trên role trong database
                return switch (role) {
                    case "Admin" -> new Admin(username, pass, id);
                    case "Seller" -> new Seller(username, pass, id);
                    default -> new Bidder(rs.getDouble("balance"), username, pass, id);
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Lưu User vào database khi đăng ký
    public static boolean saveNewUser(User user) {
        String sql = "INSERT INTO users (username, password, role, balance, email) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());

            // Xác định Role để lưu vào cột ENUM của DB
            if (user instanceof Admin) {
                preparedStatement.setString(3, "Admin");
                preparedStatement.setDouble(4, 0.0);
            } else if (user instanceof Seller) {
                preparedStatement.setString(3, "Seller"); //
                preparedStatement.setDouble(4, 0.0);
            } else {
                preparedStatement.setString(3, "Bidder");
                // Lấy balance thực tế của Bidder nếu có
                double bal = (user instanceof Bidder) ? ((Bidder) user).getBalance() : 0.0;
                preparedStatement.setDouble(4, bal);
            }
            preparedStatement.setString(5, "user@example.com");

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi đăng ký: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}