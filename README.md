# bidding-web-code - Auction System
Bài tập lớn lập trình nâng cao: Phát triển hệ thống đấu giá trực tuyến
## 1 - Mô tả bài toán, phạm vi hệ thống
- Mô tả: Hệ thống Auction System giải quyết bài toán thiết kế hệ thống đấu giá tài sản trực tuyến giữa các người dùng (client) khác nhau, được kết nối với nhau qua máy chủ (server)
- Phạm vi hệ thống:
  - Quản lý người dùng (admin, bidder, seller), sản phẩm đấu giá, phiên đấu giá (trạng thái, lịch sử giao dịch, người thắng, ...).
  - Giao diện người dùng (GUI) với các chức năng hỗ trợ phiên đấu giá, hỗ trợ thay đổi nền và đơn vị tiền tệ được hiển thị.
  - Giao diện hỗ trợ cập nhật dữ liệu phiên đấu giá theo thời gian thực (realtime update), biểu đồ giá theo thời gian (live price chart).
  - Server có khả năng xử lý các yêu cầu đấu giá đồng thời (concurrent bidding)
  - Hỗ trợ tính năng auto bid với thang tăng khi bid và trần giá, hỗ trợ gia hạn phiên đấu khi có bid mới mà thời gian còn ít hơn 1 phút (anti sniping)
## 2 - Công nghệ sử dụng, môi trường chạy, yêu cầu cài đặt
- Công nghệ sử dụng:
  - **Ngôn ngữ:** Java (target JDK 25)
  - **GUI:** JavaFX 21.0.6
  - **Server:** Apache (XAMPP)
  - **Database:** MySQL 10.4.32 - MariaDB (XAMPP)
  - **Testing:** JUnit 5.10.2, Mockito 5.14.2
  - **Build tool:** Maven
  - **Plugin:** Maven Checkstyle, Diffplug spotless
- Môi trương chạy: Window, Linux, Mac
- Yêu cầu cài đặt: Máy cần cài đặt JDK 25+, XAMPP, Maven và đã cài đúng System environment variable.
## 3 - Cấu trúc thư mục / Module chính
- Các module chính: `Auction_client`, `Auction_server`, `Auction_shared`
- Cấu trúc thư mục:  
```
bidding-web-code/
├── .github/workflow/                            # Chứa file CI/CD (GitHub Actions)
├── pom.xml                                      # pom.xml chung, chứa checkstyle, spotless plugin
├── Auction_client/                              # Module chứa các thành phần cấu thành client (GUI, giao tiếp với server)
│   ├── pom.xml                                  
│   └── src/main/
│       ├── java/
│       │   └── com/auction/client/
│       │       ├── controller/                  # Chứa các controller của javaFX view (MVC)
│       │       ├── services/                    # Chứa các logic điều khiển sau controller (MVC), giao tiếp với server 
│       │       └── utils/                       # Chứa những class khác mà controller dùng đến
│       └── resources/ 
│           ├── com/auction/client/views/        # Chứa các javafx .fxml view (MVC)
│           ├── files/                           # Chứa các file không phải class được sử dụng bỏi client 
│           └── images/                          # Chứa hình ảnh client sử dụng
│
├── Auction_server/
│   ├── pom.xml
│   └── src/main/java/
│       └── com/auction/server/                  # Chứa các class của server
│           └── services/
│
├── Auction_shared/
│   ├── pom.xml
│   └── src/main/java/
│       └── com/auction/shared/
│           ├── factory/                         # Chứa các factory khởi tạo Item 
│           └── model/                           # Chứa các lớp chính
│ 
├── database/                                    # Chứa cơ sở dữ liệu (database) được load bởi server
│ 
└── server_storage/                              # Chứa những file được load bởi server
```
## 4. Câu lệnh dòng lệnh để chạy chương trình
- Nếu máy đang chạy Client không phải là máy đang chạy Server, mở và chỉnh sửa trường ID và Port trong `bidding-web-code/Auction_shared/src/main/java/com/auction/shared/model/NetworkConfig.json` trước khi build và chạy client để phù hợp với địa chỉ IP của server. (Port cần chưa bị chiếm dụng bởi app khác. Có thể cần thay đổi cài đặt tường lửa nếu port bị chặn.)
- Build và Package: Sử dụng terminal, chạy trên mọi hệ điều hành (Window/Linux/Mac). Mở terminal đến đường dẫn của thư mục `bidding-web-code` và chạy `mvn clean package`
- Khởi tạo Database: Sử dụng XAMPP để chạy Apache và MySQL, mở trang Admin của MySQL và nhập bảng từ đường dẫn `../bidding-web-code/database/auction_system.sql`
- Tiếp tục với #5
## 5. Hướng dẫn chạy Server/Client theo thứ tự cụ thể
- Chạy server: Sau khi build (#4) và khởi tạo database, mở một terminal mới đến đường dẫn `bidding-web-code`, và chạy:
```
cd Auction_server/target
java -jar Auction_server-1.0-SNAPSHOT-jar-with-dependencies.jar
```
- Chạy client: Sau khi build (#4) và chạy server, mở một terminal mới đến đường dẫn `bidding-web-code`, và chạy:
```
cd Auction_client
mvn javafx:run
```
## 6. Danh sách chức năng đã hoàn thành: 
- Chức năng bắt buộc:
  - Quản lý người dùng, sản phẩm đấu giá, phiên đấu giá ✅
  - Chức năng đấu giá ✅
  - Xử lý lỗi, ngoại lệ ✅
  - Giao diện người dùng ✅
  - CI/CD chạy xanh ✅
- Chức năng nâng cao:
  - Auto Bidding ✅
  - Concurrent Bidding ✅
  - Realtime Update ✅
  - Realtime Price Curve ✅
  - Anti Sniping ✅
- Chức năng tự sáng tạo:
  - Hỗ trợ đăng, lưu trữ và hiện hình ảnh sản phẩm đấu giá ✅
  - Đổi màu nền cho client (theme) ✅
  - Đổi đơn vị tiền tệ, tự động cập nhật đơn vị tiền tệ trong client ✅
## 7. Link báo cáo PDF, video demo 
# TBD
