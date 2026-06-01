module Auction_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires Auction_shared;
    requires Auction_server;
    requires java.desktop;

    opens com.auction.client to
            javafx.fxml;
    opens com.auction.client.controllers to
            javafx.fxml;

    exports com.auction.client;
}
