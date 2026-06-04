package com.auction.client.services;

import com.auction.client.controllers.AuctionViewController;
import com.auction.client.controllers.ItemDetailsController;
import com.auction.shared.models.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javafx.application.Platform;

public class ClientNotificationListener extends Thread {
  // Class xử lý notification giữa server và client
  private static Object currentController;
  private Socket socket;

  public ClientNotificationListener() {
    this.setDaemon(true); // Tự tắt khi app đóng
  }

  /**
   * Usage: cài đặt controller hiện tại
   *
   * @param controller
   */
  public static void setCurrentController(Object controller) {
    currentController = controller;
  }

  /** Usage: Xử lý thông tin nhận từ server */
  @Override
  public void run() {
    // Tạo một kết nối luôn mở để nhận cập nhật về giá vật phẩm, etc
    // new Socket("192.168.x.x", port)
    try {
      Socket socket = new Socket(NetworkConfig.SERVER_IP, NetworkConfig.PORT);
      ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
      out.flush();
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

      NetworkRequest subReq =
          new NetworkRequest(NetworkRequest.requestType.SubscribeNotification, null);
      out.writeObject(subReq);
      out.flush();

      while (true) {
        Object incoming = in.readObject();

        if (currentController != null) {
          if (currentController instanceof AuctionViewController avc) {
            avc.handleNotification(incoming);
          } else if (currentController instanceof ItemDetailsController idc) {
            idc.handleNotification(incoming);
          }
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

            synchronized (this) {
              this.notifyAll();
            }
          } else {
            System.out.println("Không tìm thấy Item ID" + tx.getItemId());
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

  /** Usage: Dừng listener */
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
