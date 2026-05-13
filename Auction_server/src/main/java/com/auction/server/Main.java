package com.auction.server;

import com.auction.shared.models.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final int PORT = 1234; // Cổng kết nối
    public static Map<String, User> users = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println(">>> Auction Server đã sẵn sàng tại port " + PORT);

            // Vòng lặp vô tận để chấp nhận nhiều người dùng kết nối
            while (true) {
                Socket clientSocket = serverSocket.accept();//Chờ có người kết nối
                System.out.println("Có kết nối mới từ: " + clientSocket.getInetAddress());

                // Tạo một luồng (Thread) riêng cho mỗi người dùng để server không bị treo
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("Lỗi khởi động Server: " + e.getMessage());
        }
    }
}
