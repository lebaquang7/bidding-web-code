package com.auction.client.utils;

import javafx.scene.Node;

public class UIElementHandler {
  // Class quản lý thành phần UI
  /**
   * Usage: hiện node1, ẩn node2. Dùng bidirectional binding nếu cần trữ dữ liệu giữa 2 node.
   *
   * @param node1
   * @param node2
   */
  public static void switchElement(Node node1, Node node2) {
    node1.setManaged(true);
    node1.setVisible(true);
    node2.setManaged(false);
    node2.setVisible(false);
  }

  /**
   * Usage: Tắt phần tử UI
   *
   * @param node
   */
  public static void disableElement(Node node) {
    node.setVisible(false);
    node.setManaged(false);
  }
}
