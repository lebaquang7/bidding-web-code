package com.auction.client.Models;

import static com.auction.shared.models.NetworkRequest.requestType.Register;

import com.auction.shared.models.Bidder;
import com.auction.shared.models.NetworkRequest;
import com.auction.shared.models.User;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AccountEventHandler {
  // Loại bỏ accountStorage HashMap, dùng database thay thế

  private static User currentUser;

  public static void setCurrentUser(User user) {
    currentUser = user;
  }

  public static User getCurrentUser() {
    return currentUser;
  }

  // Xác thực đăng nhập
  public static String validateAccount(String name, String password) {
    // Sử dụng bidder tạm thời để tìm kiếm User trong database
    // Nếu tìm được người dùng sẽ tự trả về đúng kiểu
    User loginRequestData = new Bidder(name, password, null, null, 0);

    // new Socket("192.168.x.x", port)
    try (Socket socket = new Socket("localhost", 1234); // Kết nối tới Server
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
      out.flush();
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

      // 1. Gửi yêu cầu Login
      NetworkRequest request =
          new NetworkRequest(NetworkRequest.requestType.Login, loginRequestData);
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

  // Đăng ký
  public static String registerAccount(User newUser) {
    // new Socket("192.168.x.x", port)
    try (Socket socket = new Socket("127.0.0.1", 1234);
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

  // Migrate mấy method này vào 1 class riêng
  // Sell item
  // tạm thời để comment đã. Khi sửa xong thì tắt comment đi.

  // public static String sellItem (Item newItem) {
  //     //new Socket("192.168.x.x", port)
  //     try (Socket socket = new Socket("127.0.0.1", 1234);
  //          ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
  //         out.flush();
  //         ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

  //         NetworkRequest request = new NetworkRequest(NetworkRequest.requestType.SellItem,
  // newItem);
  //         out.writeObject(request);
  //         out.flush();

  //         Object response = in.readObject();
  //         return (response instanceof String) ? (String) response : "fail";

  //     } catch (Exception e) {
  //         e.printStackTrace();
  //         return "connection_error";
  //     }
  // }

  // public static java.util.ArrayList<com.auction.shared.models.Item> getSellerItems(String
  // sellerId) {
  //     try (java.net.Socket socket = new java.net.Socket("127.0.0.1", 1234);
  //          java.io.ObjectOutputStream out = new
  // java.io.ObjectOutputStream(socket.getOutputStream())) {
  //         out.flush();
  //         java.io.ObjectInputStream in = new java.io.ObjectInputStream(socket.getInputStream());

  //         com.auction.shared.models.NetworkRequest request = new
  // com.auction.shared.models.NetworkRequest(com.auction.shared.models.NetworkRequest.requestType.GetSellerItems, sellerId);
  //         out.writeObject(request);
  //         out.flush();

  //         Object response = in.readObject();
  //         if (response instanceof java.util.ArrayList) {
  //             return (java.util.ArrayList<com.auction.shared.models.Item>) response;
  //         }
  //     } catch (Exception e) {
  //         e.printStackTrace();
  //     }
  //     return new java.util.ArrayList<>();
  // }

  // public static String deleteItem(String itemId) {
  //     try (java.net.Socket socket = new java.net.Socket("127.0.0.1", 1234);
  //          java.io.ObjectOutputStream out = new
  // java.io.ObjectOutputStream(socket.getOutputStream())) {
  //         out.flush();
  //         java.io.ObjectInputStream in = new java.io.ObjectInputStream(socket.getInputStream());

  //         com.auction.shared.models.NetworkRequest request = new
  // com.auction.shared.models.NetworkRequest(com.auction.shared.models.NetworkRequest.requestType.DeleteItem, itemId);
  //         out.writeObject(request);
  //         out.flush();

  //         Object response = in.readObject();
  //         return (response instanceof String) ? (String) response : "fail";
  //     } catch (Exception e) {
  //         e.printStackTrace();
  //         return "connection_error";
  //     }
  // }

  // public static String updateItem(com.auction.shared.models.Item item) {
  //     try (java.net.Socket socket = new java.net.Socket("127.0.0.1", 1234);
  //          java.io.ObjectOutputStream out = new
  // java.io.ObjectOutputStream(socket.getOutputStream())) {
  //         out.flush();
  //         java.io.ObjectInputStream in = new java.io.ObjectInputStream(socket.getInputStream());

  //         com.auction.shared.models.NetworkRequest request = new
  // com.auction.shared.models.NetworkRequest(com.auction.shared.models.NetworkRequest.requestType.UpdateItem, item);
  //         out.writeObject(request);
  //         out.flush();

  //         Object response = in.readObject();
  //         return (response instanceof String) ? (String) response : "fail";
  //     } catch (Exception e) {
  //         e.printStackTrace();
  //         return "connection_error";
  //     }
  // }

  // public static java.util.ArrayList<com.auction.shared.models.Item> getAllItems() {
  //     try (java.net.Socket socket = new java.net.Socket("127.0.0.1", 1234);
  //          java.io.ObjectOutputStream out = new
  // java.io.ObjectOutputStream(socket.getOutputStream())) {
  //         out.flush();
  //         java.io.ObjectInputStream in = new java.io.ObjectInputStream(socket.getInputStream());

  //         com.auction.shared.models.NetworkRequest request = new
  // com.auction.shared.models.NetworkRequest(com.auction.shared.models.NetworkRequest.requestType.GetAllItems, null);
  //         out.writeObject(request);
  //         out.flush();

  //         Object response = in.readObject();
  //         if (response instanceof java.util.ArrayList) {
  //             return (java.util.ArrayList<com.auction.shared.models.Item>) response;
  //         }
  //     } catch (Exception e) {
  //         e.printStackTrace();
  //     }
  //     return new java.util.ArrayList<>();
  // }

}
