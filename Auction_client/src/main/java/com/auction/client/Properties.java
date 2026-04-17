package com.auction.client;

public class Properties {
    // Consolidated constant values used across the app
    // based on https://github.com/ZacharyDavidSaunders/InventoryManagementSystem/blob/master/src/InventoryManagementSystem/Properties.java
    private static final String APPLICATION_NAME = "Auction Client";
    private static final String APPLICATION_VERSION = "INDEV v0.1";
    private static final String APPLICATION_NAME_AND_VERSION = APPLICATION_NAME+" ("+APPLICATION_VERSION+")";

    private static final String APPLICATION_IMAGE_DIRECTORY = "/images/icon.png";

    public static String getAPPLICATION_NAME() {
        return APPLICATION_NAME;
    }

    public static String getAPPLICATION_VERSION() {
        return APPLICATION_VERSION;
    }

    public static String getAPPLICATION_NAME_AND_VERSION() {
        return APPLICATION_NAME_AND_VERSION;
    }

    public static String getAPPLICATION_IMAGE_DIRECTORY(){
        return APPLICATION_IMAGE_DIRECTORY;
    }
}
