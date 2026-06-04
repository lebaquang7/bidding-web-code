package com.auction.server;

import com.auction.server.services.AuctionManager;
import com.auction.server.services.AuctionSession;
import com.auction.shared.models.Auction;
import com.auction.shared.models.Item;
import com.auction.shared.models.NetworkConfig;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainServer {
  private static final int PORT = NetworkConfig.PORT; // Cổng kết nối
  // Tạo list các luồng ClientHandler để realtimeUpdate tới tất cả người dùng cùng lúc
  public static List<ClientHandler> clients = new CopyOnWriteArrayList<>();

  public static void resumeActiveAuction() {
    System.out.println("Đang khôi phục các phiên đấu giá đang chạy");

    List<Item> runningItems = DatabaseConfig.getAllItems();

    for (Item item : runningItems) {
      try {
        String itemId = item.getId();

        if (DatabaseConfig.isAuctionRunningInDB(itemId)) {
          AuctionSession session = AuctionManager.getInstance().getAuctionSession(itemId);
          if (session == null) {
            Auction auctionDetails =
                new Auction(0, item, item.getStartingPrice(), null, null, null);
            session =
                new AuctionSession(
                    itemId, item, auctionDetails, (long) item.getDurationTime() * 60);

            AuctionManager.getInstance().registerSession(itemId, session);
          }

          session.startAuction();
          System.out.println("Đã khôi phục phiên cho Item: " + item.getItemName());
        }
      } catch (Exception e) {
        System.err.print("Lỗi khi khôi phục phiên của item" + item.getItemName());
      }
    }
  }

  public static void main(String[] args) {
    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      System.out.println(">>> Auction Server đã sẵn sàng tại port " + PORT);

      resumeActiveAuction();

      // Vòng lặp vô tận để chấp nhận nhiều người dùng kết nối
      while (true) {
        Socket clientSocket = serverSocket.accept(); // Chờ có người kết nối
        System.out.println("Có kết nối mới từ: " + clientSocket.getInetAddress());
        ClientHandler handler = new ClientHandler(clientSocket);
        clients.add(handler);

        // Tạo một luồng (Thread) riêng cho mỗi người dùng để server không bị treo
        handler.start();
      }
    } catch (IOException e) {
      System.err.println("Lỗi khởi động Server: " + e.getMessage());
    }
  }
}
