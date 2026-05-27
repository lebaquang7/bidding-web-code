package com.auction.server.services;

import com.auction.shared.models.Bidder;
import java.math.BigDecimal;

public class PaymentService {
  private static PaymentService instance;

  private PaymentService() {
    // Khởi tạo các DAO ở đây nếu cần (ví dụ: transactionDAO)
  }

  /** Tránh trường hợp 2 người dùng 1 tk chung âf cả 2 thực hiện rút tiền và nap tiền cùng lúc */
  public static synchronized PaymentService getInstance() {
    if (instance == null) {
      instance = new PaymentService();
    }
    return instance;
  }

  // Trả tiền cho Item được đấu giá thành công

  public synchronized boolean processPayment(Bidder buyer, BigDecimal amountToPay) {
    System.out.println(" Đang xử lý giao dịch thanh toán cho User: [" + buyer.getUserName() + "]");

    // Lấy số dư hiện tại
    BigDecimal currentBalance = buyer.getBalance();

    // Mệnh đề bảo vệ (Guard Clause) kiểm tra số dư
    if (currentBalance.compareTo(amountToPay) < 0) {
      System.out.println(
          " Giao dịch thất bại: Số dư khả dụng ("
              + currentBalance
              + ") không đủ để thanh toán hóa đơn ("
              + amountToPay
              + ").");
      return false;
    }

    // Thực hiện trừ tiền
    BigDecimal newBalance = currentBalance.subtract(amountToPay);
    buyer.setBalance(newBalance);

    // TODO: Gọi Database (DAO) ở đây để lưu lịch sử trừ tiền và cập nhật số dư xuống MySQL
    // userDAO.updateBalance(buyer.getId(), newBalance);
    // transactionDAO.saveReceipt(buyer.getId(), amountToPay, "Thanh toán phiên đấu giá");

    System.out.println(
        " Giao dịch thành công! Đã thu: " + amountToPay + " | Số dư còn lại: " + newBalance);
    return true;
  }

  /** Nạp tiền vào ví. */
  public synchronized boolean depositMoney(Bidder buyer, BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      System.out.println(" Nạp tiền thất bại: Số tiền nạp phải lớn hơn 0.");
      return false;
    }

    BigDecimal newBalance = buyer.getBalance().add(amount);
    buyer.setBalance(newBalance);

    // TODO: Gọi Database (DAO) để cập nhật số dư
    // userDAO.updateBalance(buyer.getId(), newBalance);

    System.out.println(
        " Đã nạp thành công "
            + amount
            + " vào tài khoản ["
            + buyer.getUserName()
            + "]. Số dư mới: "
            + newBalance);
    return true;
  }

  /** Nghiệp vụ rút tiền từ ví. */
  public synchronized boolean withdrawMoney(Bidder buyer, BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) < 0) {
      System.out.println(" Rút tiền thất bại: Số tiền rút phải lớn hơn 0.");
      return false;
    }

    if (amount.compareTo(buyer.getBalance()) > 0) {
      System.out.println(" Rút tiền thất bại: Số dư không đủ.");
      return false;
    }

    BigDecimal newBalance = buyer.getBalance().subtract(amount);
    buyer.setBalance(newBalance);

    // TODO: Gọi Database (DAO) để cập nhật số dư xuống DB

    System.out.println(
        " Đã rút thành công "
            + amount
            + " từ tài khoản ["
            + buyer.getUserName()
            + "]. Số dư mới: "
            + newBalance);
    return true;
  }
}
