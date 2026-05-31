package com.auction.server.services;

import com.auction.server.DatabaseConfig;
import com.auction.shared.models.Auction;
import com.auction.shared.models.AuctionStatus;
import com.auction.shared.models.BidStatus;
import com.auction.shared.models.Item;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class BiddingService {
  // Sử dụng ConcurrentHashMap để lưu trữ Lock cho từng vật phẩm (itemId)
  // Giúp nhiều người có thể đấu giá các món đồ khác nhau cùng lúc mà không bị nghẽn
  private static final ConcurrentHashMap<String, ReentrantLock> itemLocks =
      new ConcurrentHashMap<>();

  public static BidStatus.bidStatus placeBid(String itemId, String bidderId, BigDecimal bidAmount) {
    ReentrantLock lock = itemLocks.computeIfAbsent(itemId, k -> new ReentrantLock());

    lock.lock();
    try {
      Item item = DatabaseConfig.getItemById(itemId);
      AuctionSession session = AuctionManager.getInstance().getAuctionSession(itemId);

      // session == null || session.getCurrentState() != AuctionStatus.RUNNING
      if (session == null && item != null && DatabaseConfig.isAuctionRunningInDB(itemId)) {
        Auction auctionDetails = new Auction(0, item, item.getStartingPrice(), null, null, null);

        session = new AuctionSession(itemId, item, auctionDetails, 3600); // Set lại bộ đếm giờ nếu cần
        session.setCurrentState(AuctionStatus.RUNNING);

        AuctionManager.getInstance().registerSession(itemId, session);
        System.out.println("[Recovery] Đã khôi phục AuctionSession cho item: " + itemId);
      }

      if (session == null || session.getCurrentState() != AuctionStatus.RUNNING) {
        System.out.println("Lỗi: Phiên đấu giá chưa bắt đầu hoặc chưa được duyệt.");
        return BidStatus.bidStatus.NOT_STARTED;
      }

      if (item == null) {
        return BidStatus.bidStatus.INVALID;
      }

      // Kiểm tra nếu người bán tự trả giá cho đồ của mình
      if (item.getSellerId().equals(bidderId)) {
        System.out.println("Lỗi: Người bán không được phép trả giá.");
        return BidStatus.bidStatus.INVALID;
      }

      // Giá mới >= Giá hiện tại + Bước giá
      BigDecimal incrementPercent = item.getPriceIncrement();
      BigDecimal incrementAmount =
          item.getCurrentPrice()
              .multiply(incrementPercent)
              .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
      BigDecimal minRequiredBid = item.getCurrentPrice().add(incrementAmount);
      if (bidAmount.compareTo(minRequiredBid) <= 0) {
        System.out.println("Lỗi: Giá trả thấp hơn mức tối thiểu yêu cầu.");
        return BidStatus.bidStatus.INVALID;
      }

      // Kiểm tra nếu người trả giá chính là người đang giữ giá cao nhất
      if (item.getHighestBidderId() != null && item.getHighestBidderId().equals(bidderId)) {
        System.out.println("Lỗi: Bạn đang là người giữ giá cao nhất, không thể tự trả giá thêm.");
        return BidStatus.bidStatus.ALREADY_HIGHEST;
      }

      boolean isSuccess = DatabaseConfig.executeBidTransaction(itemId, bidderId, bidAmount);

      if (isSuccess) {
        System.out.println("Trả giá thành công cho Item: " + itemId);
        AuctionManager.getInstance().applyAntiSniping(itemId);
        return BidStatus.bidStatus.SUCCESS;
      } else {
        System.out.println("Từ chối giao dịch vì: Lỗi update DB");
        return BidStatus.bidStatus.INVALID;
      }

    } finally {
      lock.unlock();
    }
  }

  /** Dọn dẹp map khi phiên đấu giá kết thúc để tránh tràn bộ nhớ */
  public static void removeLock(String itemId) {
    itemLocks.remove(itemId);
  }
}
