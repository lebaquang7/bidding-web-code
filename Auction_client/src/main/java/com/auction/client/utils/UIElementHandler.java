package com.auction.client.utils;

import javafx.scene.Node;

public class UIElementHandler {
  /**
   * Makes node 1 visible, while hiding node 2 via managed and visible Do use bidirectional binding
   * if value of node 1 and 2 need to be tied Usage: pwd hide/show checkbox, etc
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
   * usage: make node invisible and non managed.
   *
   * @param node
   */
  public static void disableElement(Node node) {
    node.setVisible(false);
    node.setManaged(false);
  }
}
