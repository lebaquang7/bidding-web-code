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

public class MainMenuAuctionListPaneController {
  // Usage: Controller cho màn hình danh sách sản phẩm.
  // Đường dẫn đến view của controller này
  private static final String PATH_TO_VIEW =
      "/com/auction/client/views/mainMenu_auctionListPane.fxml";

  public static String getPATH_TO_VIEW() {
    return PATH_TO_VIEW;
  }

  @FXML GridPane gridPane;
  @FXML Pagination pagination;

  private final ObservableList<Item> itemList = FXCollections.observableArrayList();

  // Số lượng vật phẩm hiển thị trên mỗi trang và số cột của lưới
  private static final int ITEMS_PER_PAGE = 6;
  private static final int COLUMN_COUNT = 2;

  /** Usage: Chạy khi controller được gọi */
  public void initialize() {
    // Lấy dữ liệu từ Server
    refreshItems();

    // Lắng nghe thay đổi của trang hiện tại trên Pagination
    pagination
        .currentPageIndexProperty()
        .addListener(
            (observable, oldIndex, newIndex) -> {
              renderItem(itemList, newIndex.intValue());
            });

    // Lắng nghe thay đổi của danh sách phần tử (nếu có cập nhật danh sách)
    itemList.addListener(
        (ListChangeListener<Item>)
            change -> {
              updatePagination();
              renderItem(itemList, pagination.getCurrentPageIndex());
            });
  }

  /** Usage: Gọi Server để lấy danh sách vật phẩm mới nhất và cập nhật UI */
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

  /** Usage: Cập nhật số lượng trang dựa trên kích thước danh sách thực tế */
  private void updatePagination() {
    // Sử dụng (double) để tránh lỗi chia số nguyên (Integer Division)
    int pageCount = (int) Math.ceil((double) itemList.size() / ITEMS_PER_PAGE);
    pagination.setPageCount(pageCount == 0 ? 1 : pageCount);
  }

  /**
   * Usage Hiển thị danh sách vật phẩm lên GridPane dựa trên chỉ mục trang
   *
   * @param list Danh sách sản phẩm
   * @param paginationIndex Chỉ mục trang
   */
  public void renderItem(List<Item> list, int paginationIndex) {
    // Xóa các card cũ trên giao diện
    gridPane.getChildren().clear();

    if (list.isEmpty()) return;

    // Tính toán khoảng vật phẩm cần hiển thị cho trang hiện tại
    int startIndex = paginationIndex * ITEMS_PER_PAGE;
    int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, list.size());

    if (startIndex >= list.size()) return;

    List<Item> itemListSublist = list.subList(startIndex, endIndex);

    for (int i = 0; i < itemListSublist.size(); i++) {
      try {
        Item item = itemListSublist.get(i);
        FXMLLoader loader =
            new FXMLLoader(getClass().getResource(AuctionCardController.getPATH_TO_VIEW()));

        Parent card = loader.load();

        // Lấy controller của card và truyền dữ liệu Item vào
        AuctionCardController controller = loader.getController();
        controller.setData(item);

        // Tính toán vị trí cột và dòng (3 cột, 2 dòng)
        int columnIndex = i / COLUMN_COUNT;
        int rowIndex = i % COLUMN_COUNT;

        gridPane.add(card, columnIndex, rowIndex);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
