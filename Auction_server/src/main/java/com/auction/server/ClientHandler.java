package com.auction.server;

import com.auction.shared.models.*;

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

            //Tạo vòng lặp đợi xử lý yêu cầu
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
                if (bid.getMaxBid() > 0) {
                    com.auction.server.services.AuctionManager.getInstance().registerAutoBid("AUC_123", bid);
                    return;
                }

                System.out.println("Nhận mức giá: " + bid.getBidAmount());

                Auction currentAuction = bid.getAuction();

                if (bid.getBidAmount() > currentAuction.getCurrentPrice()) {
                    System.out.println(">>> Trả giá THÀNH CÔNG!");
                    // Sau này sẽ thêm code cập nhật giá vào danh sách chung ở đây
                    com.auction.server.services.AuctionManager.getInstance().runAutoBiddingEngine("AUC_123", currentAuction);

                } else {
                    System.out.println(">>> Trả giá THẤP HƠN giá hiện tại. Thất bại!");
                }
            }

            //Yêu cầu đăng nhập
            if (networkRequest.getType() == NetworkRequest.requestType.Login) {
                try {
                    User loginUser = (User) networkRequest.getData();
                    User existingUser = DatabaseConfig.findUserByUsername(loginUser.getUsername());

                    if (existingUser == null) {
                        out.writeObject("accountDoesntExist");
                    }
                    else if (existingUser.getPassword().equals(loginUser.getPassword())) {
                        out.writeObject("loginSuccessful");
                        out.writeObject(existingUser);
                    }
                    else {
                        out.writeObject("invalidPassword");
                    }
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //Yêu cầu đăng ký
            if (networkRequest.getType() == NetworkRequest.requestType.Register) {
                User newUser = (User) networkRequest.getData(); //

                try {
                    // Kiểm tra xem username đã tồn tại trong database chưa
                    User existingUser = DatabaseConfig.findUserByUsername(newUser.getUsername());

                    if (existingUser != null) {
                        // Nếu đã tồn tại, gửi thông báo lỗi trùng lặp về Client
                        out.writeObject("duplicate");
                    } else {
                        // Nếu chưa có, lưu vào database
                        boolean isSaved = DatabaseConfig.saveNewUser(newUser);
                        if (isSaved) {
                            out.writeObject("success");
                        } else {
                            out.writeObject("error");
                        }
                    }
                    out.flush(); // Đẩy kết quả về lại Client
                } catch (IOException e) {
                    System.err.println("Lỗi khi phản hồi đăng ký: " + e.getMessage());
                }
            }

            // Yêu cầu bán vật phẩm
            if (networkRequest.getType() == NetworkRequest.requestType.SellItem) {
                Item newItem = (Item) networkRequest.getData();

                try {
                    boolean success = DatabaseConfig.saveNewItem(newItem);
                    out.writeObject(success ? "success" : "fail");
                    out.flush();
                } catch (IOException e) {
                    System.err.println("Lỗi khi bán vật phẩm: " + e.getMessage());
                }
            }
            // Xử lý yêu cầu lấy toàn bộ sản phẩm trên sàn
            if (networkRequest.getType() == NetworkRequest.requestType.GetAllItems) {
                try {
                    java.util.ArrayList<Item> allItems = DatabaseConfig.getAllItems();
                    out.writeObject(allItems);
                    out.flush();
                } catch (IOException e) {
                    System.err.println("Lỗi phản hồi GetAllItems: " + e.getMessage());
                }
            }

            // Xử lý yêu cầu lấy danh sách đồ của riêng một Seller
            if (networkRequest.getType() == NetworkRequest.requestType.GetSellerItems) {
                try {
                    String sellerId = (String) networkRequest.getData();
                    java.util.ArrayList<Item> sellerItems = DatabaseConfig.getSellerItems(sellerId);
                    out.writeObject(sellerItems);
                    out.flush();
                } catch (IOException e) {
                    System.err.println("Lỗi phản hồi GetSellerItems: " + e.getMessage());
                }
            }

            // Xử lý yêu cầu xóa đồ
            if (networkRequest.getType() == NetworkRequest.requestType.DeleteItem) {
                try {
                    String itemId = (String) networkRequest.getData();
                    boolean isDeleted = DatabaseConfig.deleteItem(itemId);
                    out.writeObject(isDeleted ? "success" : "fail");
                    out.flush();
                } catch (IOException e) {
                    System.err.println("Lỗi phản hồi DeleteItem: " + e.getMessage());
                }
            }

            // Xử lý yêu cầu cập nhật đồ
            if (networkRequest.getType() == NetworkRequest.requestType.UpdateItem) {
                try {
                    Item updatedItem = (Item) networkRequest.getData();
                    boolean isUpdated = DatabaseConfig.updateItem(updatedItem);
                    out.writeObject(isUpdated ? "success" : "fail");
                    out.flush();
                } catch (IOException e) {
                    System.err.println("Lỗi phản hồi UpdateItem: " + e.getMessage());
                }
            }
        }
    }
}
