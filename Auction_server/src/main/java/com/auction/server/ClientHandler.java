package com.auction.server;

import com.auction.server.models.Auction;
import com.auction.server.models.BidTransaction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

// Lớp này giúp Server xử lý nhiều người cùng lúc (Multithreading)
public class ClientHandler extends Thread {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("Đang lắng nghe dữ liệu từ client...");

            while (true) {
                // Tạm thời để trống để chờ bạn code logic xử lý Request
                Object request = in.readObject();
                if (request == null) break;
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Một Client đã ngắt kết nối.");
        }
    }

    private void handleRequest(Object request) {
        // 1. Kiểm tra nếu Client gửi yêu cầu trả giá (BidTransaction)
        if (request instanceof BidTransaction) {
            BidTransaction bid = (BidTransaction) request;
            System.out.println("Nhận mức giá: " + bid.getBidAmount());

            // 2. Lấy thông tin phiên đấu giá liên quan
            Auction currentAuction = bid.getAuction();

            // 3. Logic so sánh giá (Dựa trên ảnh image_9cf93a.jpg)
            if (bid.getBidAmount() > currentAuction.getCurrentPrice()) {
                System.out.println(">>> Trả giá THÀNH CÔNG!");
                // Sau này sẽ thêm code cập nhật giá vào danh sách chung ở đây
            } else {
                System.out.println(">>> Trả giá THẤB HƠN giá hiện tại. Thất bại!");
            }
        }

        // Bạn có thể thêm các if (request instanceof ...) khác cho Login/Logout ở đây
    }
}
