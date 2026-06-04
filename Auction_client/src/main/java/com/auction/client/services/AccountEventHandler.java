package com.auction.client.services;

import static com.auction.shared.models.NetworkRequest.requestType.Register;

import com.auction.shared.models.Bidder;
import com.auction.shared.models.NetworkConfig;
import com.auction.shared.models.NetworkRequest;
import com.auction.shared.models.User;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AccountEventHandler {
  // Class xử lý dịch vụ liên quan đến tài khoản
  private static User currentUser;

  // Getter - setters
  public static void setCurrentUser(User user) {
    currentUser = user;
  }

  public static User getCurrentUser() {
    return currentUser;
  }

  /**
   * Usage: Xác thực thông tin đăng nhập
   *
   * @param name Tên người dùng
   * @param password Mật khẩu người dùng
   * @return
   */
  public static String validateAccount(String name, String password) {
    // Sử dụng bidder tạm thời để tìm kiếm User trong database
    // Nếu tìm được người dùng sẽ tự trả về đúng kiểu
    User loginRequestData = new Bidder(name, password, null, null, 0);

    // new Socket("192.168.x.x", port)
    try (Socket socket =
            new Socket(NetworkConfig.SERVER_IP, NetworkConfig.PORT); // Kết nối tới Server
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
      out.flush();
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

      // Gửi yêu cầu Login
      NetworkRequest request =
          new NetworkRequest(NetworkRequest.requestType.Login, loginRequestData);
      out.writeObject(request);
      out.flush();

      // Đợi phản hồi từ Server
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

  /**
   * Usage: gửi yêu cầu đăng kí
   *
   * @param newUser Người dùng
   * @return
   */
  public static String registerAccount(User newUser) {
    // new Socket("192.168.x.x", port)
    try (Socket socket = new Socket(NetworkConfig.SERVER_IP, NetworkConfig.PORT);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
      out.flush();
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

      // Gửi dữ liệu đến server
      NetworkRequest request = new NetworkRequest(Register, newUser);
      out.writeObject(request);
      out.flush();

      // Nhận và xử lý phản hồi từ server
      Object response = in.readObject();
      return (response instanceof String) ? (String) response : "fail";
    } catch (Exception e) {
      e.printStackTrace();
      return "connection_error";
    }
  }
}
