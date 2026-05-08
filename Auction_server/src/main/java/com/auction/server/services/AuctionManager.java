package com.auction.server.services;

public class AuctionManager {

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
}

