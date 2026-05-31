package com.auction.client.Models;

import static com.auction.shared.models.NetworkRequest.requestType.SellItem;

import com.auction.shared.models.BidStatus;
import com.auction.shared.models.BidTransaction;
import com.auction.shared.models.Item;
import com.auction.shared.models.NetworkRequest;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ItemsEventHandler {
  // Sell item
  public static String sellItem(Item newItem) {
    // new Socket("192.168.x.x", port)
    try (Socket socket = new Socket("127.0.0.1", 1234);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
      out.flush();
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

      NetworkRequest request = new NetworkRequest(SellItem, newItem);
      out.writeObject(request);
      out.flush();

      Object response = in.readObject();
      return (response instanceof String) ? (String) response : "fail";

    } catch (Exception e) {
      e.printStackTrace();
      return "connection_error";
    }
  }

  // Lấy item từ DB về
  public static List<Item> fetchAllItems() {
    // new Socket("192.168.x.x", port)
    try (Socket socket = new Socket("127.0.0.1", 1234);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

      out.flush();
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

      // Gửi request yêu cầu lấy toàn bộ vật phẩm
      NetworkRequest request = new NetworkRequest(NetworkRequest.requestType.GetAllItems, null);
      out.writeObject(request);
      out.flush();

      // Nhận kết quả từ ClientHandler trả về
      Object response = in.readObject();
      if (response instanceof List<?>) {
        List<Item> items = (List<Item>) response;

        com.auction.shared.models.Inventory.setAllItems(items);

        return items;
      }
      return new ArrayList<>();

    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  public static BidStatus.bidStatus placeBid(String itemId, String userId, BigDecimal amount) {
    // new Socket("192.168.x.x", port)
    try (Socket socket = new Socket("127.0.0.1", 1234);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

      out.flush();
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

      BidTransaction bidData = new BidTransaction(itemId, userId, amount);

      // Gửi request với type là PlaceBid
      NetworkRequest request = new NetworkRequest(NetworkRequest.requestType.Bid, bidData);
      out.writeObject(request);
      out.flush();

      // Đọc phản hồi Enum (SUCCESS, LOW_BID, INVALID...) từ Server
      Object response = in.readObject();

      if (response instanceof BidStatus.bidStatus) {
        return (BidStatus.bidStatus) response;
      } else {
        return BidStatus.bidStatus.INVALID;
      }

    } catch (Exception e) {
      e.printStackTrace();
      // Lỗi kết nối mạng hoặc Server không phản hồi
      return BidStatus.bidStatus.INVALID;
    }
  }

  public static byte[] downloadItemImage(String imagePath) {
    if (imagePath == null || imagePath.isEmpty()) return null;

    // new Socket("192.168.x.x", port)
    try (Socket socket = new Socket("127.0.0.1", 1234);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
      out.flush();
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

      NetworkRequest request =
          new NetworkRequest(NetworkRequest.requestType.GetItemImage, imagePath);
      out.writeObject(request);
      out.flush();

      Object response = in.readObject();
      if (response instanceof byte[]) {
        return (byte[]) response;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static boolean initializeAuction(String itemId) {
    // new Socket("192.168.x.x", port)
    try (Socket socket = new Socket("127.0.0.1", 1234);
         ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
      out.flush();
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

      NetworkRequest request = new NetworkRequest(NetworkRequest.requestType.InitializeAuction, itemId);
      out.writeObject(request);
      out.flush();

      Object response = in.readObject();
      return "success".equals(response);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
