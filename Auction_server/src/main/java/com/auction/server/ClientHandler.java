package com.auction.server;

import com.auction.server.models.*;

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
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            System.out.println("Đang lắng nghe dữ liệu từ client...");

            while (true) {
                Object request = in.readObject();
                if (request == null) break;
                //Xử lý yêu cầu
                handleRequest(request);
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Một Client đã ngắt kết nối.");
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
                User loginInfo = (User) networkRequest.getData();
                User authenticatedUser = null;

                //Admin đăng nhập
                Admin admin = Admin.getInstance();
                if (loginInfo.getUserName().equals(admin.getUserName()) && loginInfo.getPassWord().equals(admin.getPassWord())) {
                    authenticatedUser = admin;
                } else {
                    //Seller/Bidder đăng nhập sử dụng Hashmap
                    User user = Main.users.get(loginInfo.getUserName());
                    if (user != null && loginInfo.getPassWord().equals(user.getPassWord())) {
                        authenticatedUser = user;
                    }
                }

                try {
                    //Đăng nhập được thì gửi dữ liệu của user đi
                    if (authenticatedUser != null) {
                        out.writeObject(authenticatedUser);
                    } else {
                        out.writeObject("Không thể đăng nhập: Tên đăng nhập hoặc mật khẩu không đúng");
                    }
                    out.flush();
                } catch (IOException e) {
                    System.out.println("Lỗi khi gửi dữ liệu về client");
                }
            }

            //Yêu cầu đăng ký
            if (networkRequest.getType() == NetworkRequest.requestType.Register) {

            }

            //Yêu cầu đăng xuất
            if (networkRequest.getType() == NetworkRequest.requestType.Logout) {

            }
        }
    }
}
