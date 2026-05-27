package com.auction.server.services;

import com.auction.server.ClientHandler;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationService {
  // Danh sách các kết nối đang hoạt động
  private static final List<ClientHandler> activeClients = new CopyOnWriteArrayList<>();

  public static void addClient(ClientHandler client) {
    activeClients.add(client);
  }

  public static void removeClient(ClientHandler client) {
    activeClients.remove(client);
  }

  public static void broadcast(Object message) {
    for (ClientHandler client : activeClients) {
      client.sendToClient(message);
    }
  }
}
