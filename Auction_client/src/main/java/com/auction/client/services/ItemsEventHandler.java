package com.auction.client.services;

import static com.auction.shared.models.NetworkRequest.requestType.SellItem;

import com.auction.client.Properties;
import com.auction.shared.models.AuctionStatus;
import com.auction.shared.models.BidStatus;
import com.auction.shared.models.BidTransaction;
import com.auction.shared.models.Inventory;
import com.auction.shared.models.Item;
import com.auction.shared.models.NetworkRequest;
import com.auction.shared.models.User;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemsEventHandler {
  // Class xử lý những sự kiện liên quan đến sản phẩm giữa client và server

  /**
   * Usage: Gửi yêu cầu bán sản phẩm
   *
   * @param newItem Sản phẩm
   * @return
   */
  public static String sellItem(Item newItem) {
    // new Socket("192.168.x.x", port)
    try (Socket socket = new Socket(Properties.getSERVER_IP(), Properties.getSERVER_PORT());
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

  /**
   * Usage: Lấy danh sách Item từ Database về
   *
   * @return
   */
  public static List<Item> fetchAllItems() {
    // new Socket("192.168.x.x", port)
    try (Socket socket = new Socket(Properties.getSERVER_IP(), Properties.getSERVER_PORT());
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

        Inventory.setAllItems(items);

        return items;
      }
      return new ArrayList<>();

    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  /**
   * Usage: Lấy trạng thái phiên đấu giá của 1 sản phẩm
   *
   * @param item Sản phẩm
   * @return
   */
  public static AuctionStatus getAuctionStatus(Item item) {
    try (Socket socket = new Socket(Properties.getSERVER_IP(), Properties.getSERVER_PORT());
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

      out.flush();
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

      // Gửi request yêu cầu lấy toàn bộ vật phẩm
      NetworkRequest request = new NetworkRequest(NetworkRequest.requestType.GetAuctionState, null);
      out.writeObject(request);
      out.flush();

      // Nhận kết quả từ ClientHandler trả về
      Object response = in.readObject();
      AuctionStatus itemStatus = ((HashMap<String, AuctionStatus>) response).get(item.getId());
      return itemStatus;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return AuctionStatus.UNKNOWN;
  }

  /**
   * Usage: Lấy danh sách lịch sử bid từ server cho 1 item nhất định
   *
   * @param item Sản phẩm
   * @return
   */
  public static List<BidTransaction> fetchBidTransactionsForItem(Item item) {
    // new Socket("192.168.x.x", port)
    try (Socket socket = new Socket(Properties.getSERVER_IP(), Properties.getSERVER_PORT());
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

      out.flush();
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

      // Gửi request yêu cầu lấy toàn bộ vật phẩm
      NetworkRequest request = new NetworkRequest(NetworkRequest.requestType.FetchBidHistory, null);
      out.writeObject(request);
      out.flush();

      // Nhận kết quả từ ClientHandler trả về
      Object response = in.readObject();
      if (response instanceof List<?>) {
        return ((List<?>) response)
            .stream()
                .filter(BidTransaction.class::isInstance) // filter instances of bidtransaction
                .map(BidTransaction.class::cast) // map to bid transaction class
                .filter(bt -> bt.getItemId().equals(item.getId())) // filter those with correct id
                .toList(); // to list
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new ArrayList<>();
  }

  /**
   * Usage: Đặt bid cho 1 sản phẩm
   *
   * @param itemId ID sản phẩm
   * @param userId ID người dùng
   * @param amount Lượng tiền đặt
   * @return
   */
  public static BidStatus.bidStatus placeBid(String itemId, String userId, BigDecimal amount) {
    // new Socket("192.168.x.x", port)
    try (Socket socket = new Socket(Properties.getSERVER_IP(), Properties.getSERVER_PORT());
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

  /**
   * Usage: Tải hình ảnh.
   *
   * @param imagePath Đường dẫn đến hình ảnh.
   * @return
   */
  public static byte[] downloadItemImage(String imagePath) {
    if (imagePath == null || imagePath.isEmpty()) return null;

    // new Socket("192.168.x.x", port)
    try (Socket socket = new Socket(Properties.getSERVER_IP(), Properties.getSERVER_PORT());
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

  /**
   * Usage: Khởi động phiên đấu giá
   *
   * @param itemId ID sản phẩm
   * @param currentUser Người dùng hiện tại
   * @return
   */
  public static String initializeAuction(String itemId, User currentUser) {
    // new Socket("192.168.x.x", port)
    try (Socket socket = new Socket(Properties.getSERVER_IP(), Properties.getSERVER_PORT());
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
      out.flush();
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

      Map<String, Object> requestData = new HashMap<>();
      requestData.put("itemId", itemId);
      requestData.put("requester", currentUser);

      NetworkRequest request =
          new NetworkRequest(NetworkRequest.requestType.InitializeAuction, requestData);
      out.writeObject(request);
      out.flush();

      Object response = in.readObject();
      return (response instanceof String) ? (String) response : "fail";
    } catch (Exception e) {
      e.printStackTrace();
      return "error";
    }
  }

  /**
   * Usage: Từ chối khởi động phiên đấu giá
   *
   * @param itemId ID sản phẩm
   * @param currentUser Người dùng hiện tại
   * @return
   */
  public static String denyAuction(String itemId, User currentUser) {
    try (Socket socket = new Socket(Properties.getSERVER_IP(), Properties.getSERVER_PORT());
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
      out.flush();
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

      Map<String, Object> requestData = new HashMap<>();
      requestData.put("itemId", itemId);
      requestData.put("requester", currentUser);

      NetworkRequest request =
          new NetworkRequest(NetworkRequest.requestType.DenyAuction, requestData);
      out.writeObject(request);
      out.flush();

      Object response = in.readObject();
      return (response instanceof String) ? (String) response : "fail";
    } catch (Exception e) {
      e.printStackTrace();
      return "fail";
    }
  }

  /**
   * Usage: Đăng kí auto bid
   *
   * @param itemId ID sản phẩm
   * @param userId ID người dùng
   * @param maxBid Auto bid - bid tối đa
   * @param increment Auto bid - thang tăng mỗi lần auto
   * @return
   */
  public static boolean registerAutoBid(
      String itemId, String userId, BigDecimal maxBid, BigDecimal increment) {
    try (Socket socket = new Socket(Properties.getSERVER_IP(), Properties.getSERVER_PORT());
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
      out.flush();
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

      // Gói 4 thông số vào một Map để gửi đi cho gọn
      Map<String, Object> requestData = new HashMap<>();
      requestData.put("itemId", itemId);
      requestData.put("userId", userId);
      requestData.put("maxBid", maxBid);
      requestData.put("increment", increment);

      NetworkRequest request =
          new NetworkRequest(NetworkRequest.requestType.valueOf("RegisterAutoBid"), requestData);
      out.writeObject(request);
      out.flush();

      Object response = in.readObject();
      return (response instanceof Boolean) ? (Boolean) response : false;

    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Usage: Dừng auto bid
   *
   * @param itemId ID sản phẩm
   * @param userId ID người dùng
   * @return
   */
  public static boolean cancelAutoBid(String itemId, String userId) {
    try (Socket socket = new Socket(Properties.getSERVER_IP(), Properties.getSERVER_PORT());
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
      out.flush();
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

      Map<String, Object> requestData = new HashMap<>();
      requestData.put("itemId", itemId);
      requestData.put("userId", userId);

      NetworkRequest request =
          new NetworkRequest(NetworkRequest.requestType.valueOf("CancelAutoBid"), requestData);
      out.writeObject(request);
      out.flush();

      Object response = in.readObject();
      return (response instanceof Boolean) ? (Boolean) response : false;

    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
