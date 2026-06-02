package com.auction.server;

import com.auction.server.services.AuctionManager;
import com.auction.server.services.AuctionSession;
import com.auction.server.services.BiddingService;
import com.auction.server.services.NotificationService;
import com.auction.shared.models.Admin;
import com.auction.shared.models.Auction;
import com.auction.shared.models.AuctionStatus;
import com.auction.shared.models.BidStatus;
import com.auction.shared.models.BidTransaction;
import com.auction.shared.models.Item;
import com.auction.shared.models.NetworkRequest;
import com.auction.shared.models.User;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Lớp này giúp Server xử lý nhiều người cùng lúc (Multithreading)
public class ClientHandler extends Thread {
  private Socket socket;
  private ObjectOutputStream out;
  private ObjectInputStream in;

  private User currentUser;

  public ClientHandler(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    try {
      // Tạo output trước rồi flush để đẩy hết dữ liệu đi rồi tạo input
      out = new ObjectOutputStream(socket.getOutputStream());
      out.flush();
      in = new ObjectInputStream(socket.getInputStream());

      // Tạo vòng lặp đợi xử lý yêu cầu
      while (true) {
        Object request = in.readObject();
        if (request == null) break;
        handleRequest(request);
      }

    } catch (IOException | ClassNotFoundException e) {
      System.out.println("Một Client đã ngắt kết nối.");
    } finally {
      // Đảm bảo đóng socket khi kết thúc
      try {
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  // Xử lý các yêu cầu từ client
  private void handleRequest(Object request) {
    if (request instanceof NetworkRequest) {
      NetworkRequest networkRequest = (NetworkRequest) request;

      // Yêu cầu đăng nhập
      if (networkRequest.getType() == NetworkRequest.requestType.Login) {
        User loginData = (User) networkRequest.getData();

        User user = DatabaseConfig.findUserByUsername(loginData.getUserName());

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
          e.printStackTrace();
          System.err.println("Lỗi khi phản hồi đăng nhập: " + e.getMessage());
        }
      }

      // Yêu cầu đăng ký
      if (networkRequest.getType() == NetworkRequest.requestType.Register) {
        User newUser = (User) networkRequest.getData();

        try {
          // Kiểm tra xem username đã tồn tại trong database chưa
          User existingUser = DatabaseConfig.findUserByUsername(newUser.getUserName());

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
          e.printStackTrace();
          System.err.println("Lỗi khi phản hồi đăng ký: " + e.getMessage());
        }
      }

      // Yêu cầu bán vật phẩm
      if (networkRequest.getType() == NetworkRequest.requestType.SellItem) {
        Item newItem = (Item) networkRequest.getData();

        try {
          // Kiểm tra nếu có ảnh
          if (newItem.getImageBytes() != null && newItem.getImagePath() != null) {
            File imageDir = new File("server_storage/item_images");
            if (!imageDir.exists()) imageDir.mkdirs();

            String uniqueFileName = System.currentTimeMillis() + "_" + newItem.getImagePath();
            File fileToSave = new File(imageDir, uniqueFileName);

            Files.write(fileToSave.toPath(), newItem.getImageBytes());

            newItem.setImagePath(uniqueFileName);
          }

          boolean success = DatabaseConfig.saveNewItem(newItem);

          if (success) {
            out.writeObject("success");
          } else {
            out.writeObject("fail");
          }
          out.flush();
        } catch (IOException e) {
          e.printStackTrace();
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
          e.printStackTrace();
          System.err.println("Lỗi gửi danh sách Item: " + e.getMessage());
        }
      }

      // Yêu cầu lấy dữ liệu ảnh (mảng byte) từ server_storage
      if (networkRequest.getType() == NetworkRequest.requestType.GetItemImage) {
        String fileName = (String) networkRequest.getData();
        try {
          File imageFile = new File("server_storage/item_images", fileName);

          if (imageFile.exists()) {
            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
            out.writeObject(imageBytes);
          } else {
            out.writeObject(null);
          }
          out.flush();
        } catch (IOException e) {
          e.printStackTrace();
          System.err.println("Lỗi khi gửi dữ liệu ảnh: " + e.getMessage());
        }
      }

      // Yêu cầu trả giá cho vật phẩm
      if (networkRequest.getType() == NetworkRequest.requestType.Bid) {
        BidTransaction bidData = (BidTransaction) networkRequest.getData();

        try {
          BidStatus.bidStatus status =
              BiddingService.placeBid(
                  bidData.getItemId(), bidData.getBidderId(), bidData.getBidAmount());

          out.writeObject(status);
          out.flush();

          if (status == BidStatus.bidStatus.SUCCESS) {
            NotificationService.broadcast(bidData);
          }

        } catch (IOException e) {
          e.printStackTrace();
          System.err.println("Lỗi khi phản hồi đặt giá: " + e.getMessage());
        }
      }

      // Tạo một Thread luôn mở để nhận thông báo thay đổi về giá vật phẩm,etc
      if (networkRequest.getType() == NetworkRequest.requestType.SubscribeNotification) {
        NotificationService.addClient(this);
        System.out.println("Một kết nối đã đăng ký nhận Real-time.");

        try {
          while (in.read() != -1) {}
        } catch (IOException e) {
          e.printStackTrace();
          System.out.println("Kết nối Real-time đã bị ngắt bởi Client.");
        } finally {
          NotificationService.removeClient(this);
        }
        return;
      }

      // Yêu cầu khởi tạo Auction từ admin
      if (networkRequest.getType() == NetworkRequest.requestType.InitializeAuction) {
        Map<String, Object> data = (Map<String, Object>) networkRequest.getData();
        User currentUser = (User) data.get("requester");
        String itemId = (String) data.get("itemId");
        AuctionSession session = AuctionManager.getInstance().getAuctionSession(itemId);

        try {
          if (currentUser == null || !(currentUser instanceof Admin)) {
            out.writeObject("unauthorized");
            out.flush();
            return; // Không cho khởi tạo Auction nếu không phải Admin
          }

          if (session == null) {
            Item item = DatabaseConfig.getItemById(itemId);
            if (item != null) {
              LocalDateTime startTime = LocalDateTime.now();
              LocalDateTime endTime = startTime.plusMinutes(item.getDurationTime());

              Auction auctionDetails =
                  new com.auction.shared.models.Auction(
                      0, item, item.getStartingPrice(), null, null, null);
              auctionDetails.setStatus(com.auction.shared.models.AuctionStatus.PENDING_APPROVAL);

              long durationInSeconds = (long) item.getDurationTime() * 60;

              // Tạo Session mới và đăng ký vào Manager
              session = new AuctionSession(itemId, item, auctionDetails, durationInSeconds);
              AuctionManager.getInstance().registerSession(itemId, session);
            }
          }

          boolean success = (session != null && session.startAuction());
          out.writeObject(success ? "success" : "fail");
          out.flush();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      if (networkRequest.getType() == NetworkRequest.requestType.FetchBidHistory) {
        try {
          List<BidTransaction> bidHistory = DatabaseConfig.getAllBidHistory();
          out.writeObject(bidHistory);
          out.flush();
        } catch (IOException e) {
          e.printStackTrace();
          System.err.println("Lỗi gửi danh sách Bid History: " + e.getMessage());
        }
      }

      if (networkRequest.getType() == NetworkRequest.requestType.GetAuctionState) {
        try {
          HashMap<String, AuctionStatus> itemStatusHashMap = DatabaseConfig.getAuctionState();
          out.writeObject(itemStatusHashMap);
          out.flush();
        } catch (IOException e) {
          e.printStackTrace();
          System.err.println("Lỗi gửi danh sách Bid History: " + e.getMessage());
        }
      }

      if (networkRequest.getType() == NetworkRequest.requestType.DenyAuction) {
        Map<String, Object> data = (Map<String, Object>) networkRequest.getData();
        String itemId = (String) data.get("itemId");
        User requester = (User) data.get("requester");

        try {
          if (requester instanceof Admin) {
            String result = DatabaseConfig.deleteItemIfPending(itemId);
            out.writeObject(result);
          } else {
            out.writeObject("unauthorized");
          }
          out.flush();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      // Yêu cầu AutoBid
      if (networkRequest.getType() == NetworkRequest.requestType.Bid
          && networkRequest.getData() instanceof java.util.Map) {
        try {
          java.util.Map<String, Object> map =
              (java.util.Map<String, Object>) networkRequest.getData();
          String itemId = (String) map.get("itemId");
          String bidderId = (String) map.get("bidderId");
          java.math.BigDecimal maxBid = (java.math.BigDecimal) map.get("maxBid");
          java.math.BigDecimal increment = (java.math.BigDecimal) map.get("increment");

          com.auction.server.services.AuctionManager.getInstance()
              .registerAutoBid(itemId, bidderId, maxBid, increment);
          out.writeObject(BidStatus.bidStatus.SUCCESS);
          out.flush();
        } catch (IOException e) {
          System.err.println("Lỗi cài đặt Auto-Bid: " + e.getMessage());
        }
      }
    }
  }

  public void sendToClient(Object message) {
    try {
      out.writeObject(message);
      out.flush();
    } catch (IOException e) {
      NotificationService.removeClient(this);
      System.err.println("Không thể gửi thông báo tới một client.");
    }
  }
}
