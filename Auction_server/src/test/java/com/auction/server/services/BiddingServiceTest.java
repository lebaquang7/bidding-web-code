package com.auction.server.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.auction.server.DatabaseConfig;
import com.auction.shared.models.BidStatus;
import com.auction.shared.models.Item;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

@DisplayName("Server Module: Kiểm thử đồng thời & đặt giá - BiddingService")
public class BiddingServiceTest {

  private MockedStatic<DatabaseConfig> mockedDbConfig;

  @BeforeEach
  void initMocks() {
    mockedDbConfig = Mockito.mockStatic(DatabaseConfig.class, Mockito.RETURNS_DEFAULTS);
  }

  @AfterEach
  void clearMocks() {
    mockedDbConfig.close();
  }

  @Test
  @DisplayName("TC-SERVER-01: Đặt giá hợp lệ thành công")
  void testPlaceBid_Success() {
    // Tạo Item giả lập đầy đủ dữ liệu để vượt qua các vòng Check Null của BiddingService
    Item mockItem = Mockito.mock(Item.class);
    Mockito.when(mockItem.getCurrentPrice()).thenReturn(new BigDecimal("100.00"));
    Mockito.when(mockItem.getPriceIncrement()).thenReturn(new BigDecimal("10.00"));
    Mockito.when(mockItem.getHighestBidderId()).thenReturn("user_old");
    Mockito.when(mockItem.getSellerId())
        .thenReturn("seller_001"); // Giả lập tránh lỗi NullPointerException

    // Chặn toàn bộ đường gọi tới DB thật
    mockedDbConfig.when(() -> DatabaseConfig.getItemById("item1")).thenReturn(mockItem);
    mockedDbConfig.when(() -> DatabaseConfig.isAuctionRunningInDB("item1")).thenReturn(true);
    mockedDbConfig
        .when(
            () ->
                DatabaseConfig.executeBidTransaction(
                    Mockito.eq("item1"), Mockito.anyString(), Mockito.any()))
        .thenReturn(true);

    // Thực thi test
    BidStatus.bidStatus result =
        BiddingService.placeBid("item1", "user_new", new BigDecimal("120.00"));

    // Khẳng định kết quả
    assertEquals(BidStatus.bidStatus.SUCCESS, result);
  }

  @Test
  @DisplayName("TC-SERVER-02: Xử lý đấu giá đồng thời (Chống Lost Update)")
  void testConcurrentBidding_Safety() throws InterruptedException {
    int numberOfThreads = 6;
    ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);

    // Dùng 2 chốt chặn để đảm bảo 6 luồng cùng xuất phát một lúc
    CountDownLatch readyLatch = new CountDownLatch(numberOfThreads);
    CountDownLatch startLatch = new CountDownLatch(1);

    AtomicInteger successCount = new AtomicInteger(0);

    // Tạo item mock chung ở luồng chính
    Item mockItem = Mockito.mock(Item.class);
    Mockito.when(mockItem.getCurrentPrice()).thenReturn(new BigDecimal("100.00"));
    Mockito.when(mockItem.getPriceIncrement()).thenReturn(new BigDecimal("5.00"));
    Mockito.when(mockItem.getSellerId()).thenReturn("seller_boss");

    for (int i = 0; i < numberOfThreads; i++) {
      final String bidderId = "competitor_" + i;
      service.submit(
          () -> {
            // Mở MockedStatic riêng biệt cho từng luồng con
            try (MockedStatic<DatabaseConfig> threadMockedDb =
                Mockito.mockStatic(DatabaseConfig.class, Mockito.RETURNS_DEFAULTS)) {

              // Giả lập dữ liệu cho luồng này
              threadMockedDb
                  .when(() -> DatabaseConfig.getItemById("concurrency_id"))
                  .thenReturn(mockItem);
              threadMockedDb
                  .when(() -> DatabaseConfig.isAuctionRunningInDB("concurrency_id"))
                  .thenReturn(true);

              // Luồng nào chạy lệnh này đầu tiên (successCount == 0) thì DB trả về true, các luồng
              // sau bị false
              threadMockedDb
                  .when(
                      () ->
                          DatabaseConfig.executeBidTransaction(
                              Mockito.eq("concurrency_id"), Mockito.anyString(), Mockito.any()))
                  .thenAnswer(invocation -> successCount.get() == 0);

              readyLatch.countDown();
              startLatch.await();

              BidStatus.bidStatus status =
                  BiddingService.placeBid("concurrency_id", bidderId, new BigDecimal("150.00"));

              if (status == BidStatus.bidStatus.SUCCESS) {
                successCount.incrementAndGet();
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
          });
    }

    readyLatch.await();
    startLatch.countDown();

    service.shutdown();
    service.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);

    assertEquals(1, successCount.get(), "Chỉ được phép có duy nhất 1 giao dịch thành công");
  }
}
