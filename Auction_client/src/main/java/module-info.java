module Auction_client {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.auction.client to javafx.fxml;
    opens com.auction.client.Controllers to javafx.fxml;
    exports com.auction.client;
}