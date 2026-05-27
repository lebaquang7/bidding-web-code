package com.auction.client.Models;

import com.auction.shared.models.Bidder;
import com.auction.shared.models.NetworkRequest;
import com.auction.shared.models.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.auction.shared.models.Bidder;
import com.auction.shared.models.NetworkRequest;
import static com.auction.shared.models.NetworkRequest.requestType.Register;
import com.auction.shared.models.User;

public class AccountEventHandler {
    //Loại bỏ accountStorage HashMap, dùng database thay thế

    private static User currentUser;

    public static void setCurrentUser(User user) {currentUser = user;}
    public static User getCurrentUser() {return currentUser;}

    //Xác thực đăng nhập
    public static String validateAccount(String name, String password) {
        //Sử dụng bidder tạm thời để tìm kiếm User trong database
        //Nếu tìm được người dùng sẽ tự trả về đúng kiểu
        User loginRequestData = new Bidder(name, password, null, null, 0);

        // new Socket("192.168.x.x", port)
        try (Socket socket = new Socket("localhost", 1234); // Kết nối tới Server
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // 1. Gửi yêu cầu Login
            NetworkRequest request = new NetworkRequest(NetworkRequest.requestType.Login, loginRequestData);
            out.writeObject(request);
            out.flush();

            // 2. Đợi phản hồi từ Server
            Object response = in.readObject();

            if (response instanceof User) {
                User loggedInUser = (User) response;

                setCurrentUser(loggedInUser);

                return "loginSuccessful";
            } else if (response instanceof String) {
                return (String) response;
            }
            return "fail";
        } catch (Exception e) {
            e.printStackTrace();
            return "connection_error";
        }
    }

    //Đăng ký
    public static String registerAccount(User newUser) {
        //new Socket("192.168.x.x", port)
        try (Socket socket = new Socket("127.0.0.1", 1234);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            //Gửi dữ liệu đến server
            NetworkRequest request = new NetworkRequest(Register, newUser);
            out.writeObject(request);
            out.flush();

            //Nhận và xử lý phản hồi từ server
            Object response = in.readObject();
            return (response instanceof String) ? (String) response : "fail";
        } catch (Exception e) {
            e.printStackTrace();
            return "connection_error";
        }
    }
}