package com.auction.client.Models;

import com.auction.shared.models.Bidder;
import com.auction.shared.models.NetworkRequest;
import com.auction.shared.models.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static com.auction.shared.models.NetworkRequest.requestType.Register;

public class AccountEventHandler {
    //Loại bỏ accountStorage HashMap, dùng database thay thế

    //Xác thực đăng nhập
    public static String validateAccount(String name, String password) {
        //Sử dụng bidder tạm thời để tìm kiếm User trong database
        //Nếu tìm được người dùng sẽ tự trả về đúng kiểu
        User loginRequestData = new Bidder(name, password, null, null, 0.0, 0);

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
                // Server trả về một đối tượng User(Admin/Bidder/Seller)
                return "loginSuccessful";
            } else if (response instanceof String) {
                // Trả về đúng các từ khóa mà switch-case trong LoginController đang đợi
                String msg = (String) response;
                if (msg.equals("invalidCredentials")) return "invalidPassword";
            }

            return "accountDoesntExist";

        } catch (Exception e) {
            e.printStackTrace();
            return "serverError"; // Trường hợp mất kết nối Server
        }
    }

    //Đăng ký
    public static String registerAccount(User newUser) {
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