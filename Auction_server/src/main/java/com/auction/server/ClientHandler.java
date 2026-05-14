package com.auction.server;

import com.auction.shared.models.NetworkRequest;
import com.auction.shared.models.Auction;
import com.auction.shared.models.BidTransaction;
import com.auction.shared.models.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

// Lớp này giúp Server xử lý nhiều người cùng lúc (Multithreading)
public class ClientHandler extends Thread {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //Tạo output trước rồi flush để đẩy hết dữ liệu đi rồi tạo input
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            //Tạo vong lặp đợi xử lý yêu cầu
            while (true) {
                Object request = in.readObject();
                if (request == null) break;
                handleRequest(request);
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Một Client đã ngắt kết nối.");
        } finally {
            // Đảm bảo đóng socket khi kết thúc
            try { socket.close(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    //Xử lý các yêu cầu từ client
    private void handleRequest(Object request) {
        if (request instanceof NetworkRequest) {
            NetworkRequest networkRequest = (NetworkRequest) request;

            //Yêu cầu trả giá
            if (networkRequest.getType() == NetworkRequest.requestType.Bid) {
                BidTransaction bid = (BidTransaction) networkRequest.getData();
                System.out.println("Nhận mức giá: " + bid.getBidAmount());

                Auction currentAuction = bid.getAuction();

                if (bid.getBidAmount() > currentAuction.getCurrentPrice()) {
                    System.out.println(">>> Trả giá THÀNH CÔNG!");
                    // Sau này sẽ thêm code cập nhật giá vào danh sách chung ở đây
                } else {
                    System.out.println(">>> Trả giá THẤP HƠN giá hiện tại. Thất bại!");
                }
            }

            //Yêu cầu đăng nhập
            if (networkRequest.getType() == NetworkRequest.requestType.Login) {
                User loginInfo = (User) networkRequest.getData(); //

                // Gọi DatabaseConfig để tìm user
                User authenticatedUser = DatabaseConfig.findUserByUsername(loginInfo.getUsername());

                try {
                    // Kiểm tra: nếu tìm thấy user và mật khẩu khớp
                    if (authenticatedUser != null && authenticatedUser.getPassword().equals(loginInfo.getPassword())) {
                        // Gửi lại đúng đối tượng Admin/Bidder/Seller về cho Client
                        out.writeObject(authenticatedUser);
                    } else {
                        // Trả về chuỗi thông báo lỗi nếu sai thông tin
                        out.writeObject("invalidCredentials");
                    }
                    out.flush();
                } catch (IOException e) {
                    System.err.println("Lỗi khi phản hồi đăng nhập: " + e.getMessage());
                }
            }

            //Yêu cầu đăng ký
            if (networkRequest.getType() == NetworkRequest.requestType.Register) {
                User newUser = (User) networkRequest.getData(); //

                try {
                    // 1. Kiểm tra xem username đã tồn tại trong database chưa
                    User existingUser = DatabaseConfig.findUserByUsername(newUser.getUsername());

                    if (existingUser != null) {
                        // Nếu đã tồn tại, gửi thông báo lỗi trùng lặp về Client
                        out.writeObject("duplicate");
                    } else {
                        // 2. Nếu chưa có, lưu vào database
                        boolean isSaved = DatabaseConfig.saveNewUser(newUser);
                        if (isSaved) {
                            out.writeObject("success");
                        } else {
                            out.writeObject("error");
                        }
                    }
                    out.flush(); //Đẩy kết quả về lại Client
                } catch (IOException e) {
                    System.err.println("Lỗi khi phản hồi đăng ký: " + e.getMessage());
                }
            }
        }
    }
}
