package com.auction.client;

import javafx.stage.Screen;

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



    /**
     * Consolidated Window Size config values
     */

    private static final int DEFAULT_STAGE_SIZE_X = 600;
    private static final int DEFAULT_STAGE_SIZE_Y = 400;

    public static int getDEFAULT_STAGE_SIZE_X(){
        return DEFAULT_STAGE_SIZE_X;
    }

    public static int getDEFAULT_STAGE_SIZE_Y(){
        return DEFAULT_STAGE_SIZE_Y;
    }

    public static int getFULL_STAGE_SIZE_X(){
        return (int) Screen.getPrimary().getBounds().getMaxX();
    }

    public static int getFULL_STAGE_SIZE_Y(){
        return (int) Screen.getPrimary().getBounds().getMaxY();
    }
}
