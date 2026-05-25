package com.auction.server;

import com.auction.shared.models.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

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

            //Yêu cầu đăng nhập
            if (networkRequest.getType() == NetworkRequest.requestType.Login) {
                User loginData = (User) networkRequest.getData();

                User user = DatabaseConfig.findUserByUsername(loginData.getUsername());

                try {
                    if (user == null) {
                        out.writeObject("accountDoesntExist");
                    } else if (!user.getPassword().equals(loginData.getPassword())) {
                        out.writeObject("invalidPassword");
                    } else {
                        out.writeObject(user);
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

            // Yêu cầu lấy thông tin các vật phẩm trên DB về
            if (networkRequest.getType() == NetworkRequest.requestType.GetAllItems) {
                try {
                    List<Item> allItems = DatabaseConfig.getAllItems();
                    out.writeObject(allItems); // Gửi nguyên List đối tượng về cho Client
                    out.flush();
                } catch (IOException e) {
                    System.err.println("Lỗi gửi danh sách Item: " + e.getMessage());
                }
            }
        }
    }
}
