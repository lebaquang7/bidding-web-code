package com.auction.server.services;

import com.auction.shared.models.Item;
// import com.auction.server.models.Auction; //sẽ cần tạo class này sau

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuctionManager {

    private Map<String, Object> activeAuctions; // Tạm để Object, sau này đổi thành class Auction

    private static volatile AuctionManager instance;
    private AuctionManager() {}

    public static AuctionManager getInstance() {
        AuctionManager result = instance;
        if (result == null) {
            synchronized (AuctionManager.class) {
                result = instance;
                if (result == null) {
                    instance = result = new AuctionManager();
                }
            }
        }
        return result;
    }

    public void startNewAuction(String auctionId, Item item) {
        // Code logic tạo phiên đấu giá và nhét vào activeAuctions
        System.out.println("Bắt đầu phiên đấu giá cho mặt hàng: " + item.getName());
    }
}
