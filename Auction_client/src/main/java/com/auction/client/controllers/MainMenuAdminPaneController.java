package com.auction.client.controllers;

import com.auction.client.services.ItemsEventHandler;
import com.auction.shared.models.Item;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Pagination;
import javafx.scene.layout.GridPane;

public class MainMenuAdminPaneController {
  // Giống với MainMenuAuctionListPane, nhưng cho admin quản lý sản phẩm
  // Đường dẫn đến view của controller này
  private static final String PATH_TO_VIEW = "/com/auction/client/views/mainMenu_adminPane.fxml";

  public static String getPATH_TO_VIEW() {
    return PATH_TO_VIEW;
  }

  @FXML GridPane gridPane;
  @FXML Pagination pagination;

  private ObservableList<Item> itemList = FXCollections.observableArrayList();

  private static final int ITEMS_PER_PAGE = 6;
  private static final int COLUMN_COUNT = 2;

  /** Usage: Tự chạy khi controller được gọi */
  public void initialize() {
    refreshItems();

    pagination
        .currentPageIndexProperty()
        .addListener(
            (observable, oldIndex, newIndex) -> {
              renderItem(itemList, newIndex.intValue());
            });

    itemList.addListener(
        (ListChangeListener<Item>)
            change -> {
              updatePagination();
              renderItem(itemList, pagination.getCurrentPageIndex());
            });
  }

  /** Usage: tải lại các sản phẩm trên màn hình */
  public void refreshItems() {
    List<Item> fetchedItems = ItemsEventHandler.fetchAllItems();

    if (fetchedItems != null) {
      itemList.setAll(fetchedItems);
    } else {
      itemList.clear();
    }

    updatePagination();
    renderItem(itemList, 0);
  }

  /** Usage: cập nhật số trang trong pagination (danh sách trang sản phẩm) */
  private void updatePagination() {
    int pageCount = (int) Math.ceil((double) itemList.size() / ITEMS_PER_PAGE);
    pagination.setPageCount(pageCount == 0 ? 1 : pageCount);
  }

  /**
   * Usage: Render các sản phẩm dưới dạng thẻ hiện sản phẩm
   *
   * @param list Danh sách sản phẩm
   * @param paginationIndex Số trang được chọn
   */
  public void renderItem(List<Item> list, int paginationIndex) {
    gridPane.getChildren().clear();

    if (list.isEmpty()) return;

    int startIndex = paginationIndex * ITEMS_PER_PAGE;
    int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, list.size());

    if (startIndex >= list.size()) return;

    List<Item> itemListSublist = list.subList(startIndex, endIndex);

    for (int i = 0; i < itemListSublist.size(); i++) {
      try {
        Item item = itemListSublist.get(i);
        FXMLLoader loader =
            new FXMLLoader(getClass().getResource(AdminCardController.getPATH_TO_VIEW()));

        Parent card = loader.load();

        AdminCardController controller = loader.getController();
        controller.setData(item);

        int columnIndex = i / COLUMN_COUNT;
        int rowIndex = i % COLUMN_COUNT;

        gridPane.add(card, columnIndex, rowIndex);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
