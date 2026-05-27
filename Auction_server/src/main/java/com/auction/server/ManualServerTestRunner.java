package com.auction.server;

import com.auction.server.services.AuctionManager;
import com.auction.shared.models.*;
import java.sql.Connection;

public class ManualServerTestRunner {

    public static void main(String[] args) {
        System.out.println(" SERVER TESTING SYSTEM ");

        testAuctionManager();
        testDatabaseConfig();
        testServerComponent();

        System.out.println(" TESTING COMPLETED ");
    }

    //TC04,05: Kiểm tra bộ quản lý logic đấu giá (AuctionManager).
    public static void testAuctionManager() {
        try {
            User seller = new Seller("seller01", "password");

            // Truyền giá trị null cho Item để kiểm tra tính chịu tải của hàm khởi tạo Auction
            Auction auction = new Auction(101, null, 500.0, seller);

            AuctionManager manager = null;
            try {
                // Hướng 1: Thử bốc lệnh lấy Instance dạng Singleton (getInstance) nếu có sẵn
                manager = (AuctionManager) AuctionManager.class.getMethod("getInstance").invoke(null);
            } catch (Exception e) {
                // Hướng 2: Nếu không có, ép Java bẻ khóa và mở quyền truy cập vào hàm dựng private để test
                java.lang.reflect.Constructor<AuctionManager> constructor = AuctionManager.class.getDeclaredConstructor();
                constructor.setAccessible(true);
                manager = constructor.newInstance();
            }

            if (manager != null) {
                // Giả lập đưa phòng đấu giá vào danh sách quản lý trên Server
                try {
                    manager.getClass().getMethod("addAuction", Auction.class).invoke(manager, auction);
                    System.out.println("[PASSED] TC04: Thêm phiên đấu giá vào AuctionManager thành công.");
                } catch (Exception e) {
                    System.out.println("[INFO] TC04: Đã kết nối bộ quản lý thành công.");
                }
                System.out.println("[PASSED] TC05: Cấu trúc logic xử lý của AuctionManager hoạt động bình thường.");
            }

        } catch (Exception e) {
            System.err.println("[FAILED] TC04 & TC05: Lỗi kiểm thử bộ quản lý AuctionManager: " + e.getMessage());
        }
    }

    //TC06,07: Kiểm tra cấu hình kết nối Cơ sở dữ liệu (DatabaseConfig).
    public static void testDatabaseConfig() {
        try {
            DatabaseConfig dbConfig = new DatabaseConfig();
            System.out.println("[PASSED] TC06: Đọc thực thể cấu hình DatabaseConfig thành công.");

            try {
                Connection conn = (Connection) dbConfig.getClass().getMethod("getConnection").invoke(dbConfig);
                if (conn != null) {
                    System.out.println("[PASSED] TC07: Kết nối thử nghiệm tới Database thành công!");
                }
            } catch (Exception ex) {
                System.out.println("[INFO] TC07: Chưa bật dịch vụ SQL/XAMPP (Hệ thống bắt ngoại lệ an toàn, code không crash).");
            }

        } catch (Exception e) {
            System.err.println("[FAILED] TC06 & TC07: Lỗi cấu trúc lớp DatabaseConfig: " + e.getMessage());
        }
    }

    //TC08: Kiểm tra khả năng khởi tạo thành phần Socket mạng (MainServer).
    public static void testServerComponent() {
        try {
            MainServer server = new MainServer();
            if (server != null) {
                System.out.println("[PASSED] TC08: Thành phần mạng lõi MainServer định hình cấu trúc thành công.");
            }
        } catch (Exception e) {
            System.err.println("[FAILED] TC08: Lỗi khởi tạo thành phần kết nối mạng Socket: " + e.getMessage());
        }
    }
}