package com.auction.client.services;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.auction.client.controllers.AuctionViewController;
import com.auction.shared.models.BidTransaction;
import com.auction.shared.models.Inventory;
import com.auction.shared.models.Item;
import com.auction.shared.models.NetworkRequest;

import javafx.application.Platform;

public class ClientNotificationListener extends Thread {
  private static AuctionViewController currentController;
  private Socket socket;

  public ClientNotificationListener() {
    this.setDaemon(true); // Tự tắt khi app đóng
  }

  public static void setCurrentController(AuctionViewController controller) {
    currentController = controller;
  }

  @Override
  public void run() {
    // Tạo một kết nối luôn mở để nhận cập nhật về giá vật phẩm, etc
    // new Socket("192.168.x.x", port)
    try {
      Socket socket = new Socket("127.0.0.1", 1234);
      ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
      out.flush();
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

      // Gửi tín hiệu "đăng ký đường dây nóng"
      NetworkRequest subReq = new NetworkRequest(NetworkRequest.requestType.SubscribeNotification, null);
      out.writeObject(subReq);
      out.flush();

      while (true) {
        Object incoming = in.readObject();

        if (currentController != null) {
          currentController.handleNotification(incoming);
        }

        if (incoming instanceof BidTransaction) {
          BidTransaction tx = (BidTransaction) incoming;
          // Tìm item trong Inventory để cập nhật giá
          Item target = Inventory.getItemById(tx.getItemId());

          if (target != null) {
            Platform.runLater(
                () -> {
                  target.setCurrentPrice(tx.getBidAmount());
                });
          } else {
            System.out.println("Cảnh báo: Không tìm thấy Item ID" + tx.getItemId());
          }
        }
      }
    } catch (Exception e) {
      System.err.println("Luồng Real-time bị ngắt kết nối.");
      e.printStackTrace();
    } finally {
      stopListener();
    }
  }

  public void stopListener() {
    try {
      currentController = null;
      if (socket != null && !socket.isClosed()) {
        socket.close();
      }
      System.out.println("Đã ngắt kết nối Real-time.");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
