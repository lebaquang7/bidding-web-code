module Auction_server {
  requires javafx.controls;
  requires javafx.fxml;
  requires Auction_shared;
  requires java.sql;
  requires mysql.connector.j;

  exports com.auction.server;
}
