package com.auction.client.Models;

import com.auction.shared.models.Item;
import com.auction.shared.models.NetworkRequest;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.auction.shared.models.NetworkRequest.requestType.SellItem;

public class ItemsEventHandler {
    //Sell item
    public static String sellItem (Item newItem) {
        //new Socket("192.168.x.x", port)
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

    // Tìm item
    public static List<Item> fetchAllItems() {
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
                return (List<Item>) response;
            }
            return new ArrayList<>();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
