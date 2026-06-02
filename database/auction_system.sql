-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th6 02, 2026 lúc 09:13 AM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `auction_system`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `admins`
--

CREATE TABLE `admins` (
  `id` varchar(40) NOT NULL,
  `accessLevel` int(11) NOT NULL,
  `department` varchar(100) NOT NULL,
  `internalEmployeeId` varchar(40) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `admins`
--

INSERT INTO `admins` (`id`, `accessLevel`, `department`, `internalEmployeeId`) VALUES
('001', 10, '', '');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `artworks`
--

CREATE TABLE `artworks` (
  `id` varchar(40) NOT NULL,
  `artistName` varchar(255) NOT NULL,
  `isOriginal` tinyint(1) NOT NULL,
  `creationYear` int(11) NOT NULL,
  `medium` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `artworks`
--

INSERT INTO `artworks` (`id`, `artistName`, `isOriginal`, `creationYear`, `medium`) VALUES
('01799397-9cb6-4358-a54c-e46389880ea0', '', 1, 0, ''),
('320e77a8-0267-46e5-ba03-6f3f03768957', '', 1, 0, ''),
('ed9e068a-9541-49e0-bc4a-dddd5a508816', '', 1, 0, ''),
('8a7a9079-28ff-4429-9753-6939d3ddadd4', '', 1, 0, '');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `bidders`
--

CREATE TABLE `bidders` (
  `id` varchar(40) NOT NULL,
  `shippingAddress` varchar(255) NOT NULL,
  `balance` double NOT NULL DEFAULT 0,
  `reputationScore` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `bidders`
--

INSERT INTO `bidders` (`id`, `shippingAddress`, `balance`, `reputationScore`) VALUES
('23312c96-d768-4e4f-b7ff-de57529c1e97', '', 0, 0),
('969885f3-1093-4903-ad56-aa4e123306eb', '', 0, 0);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `bid_history`
--

CREATE TABLE `bid_history` (
  `bid_id` int(11) NOT NULL,
  `item_Id` varchar(40) NOT NULL,
  `bidder_Id` varchar(40) NOT NULL,
  `bid_amount` decimal(15,2) NOT NULL,
  `bid_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `status` varchar(20) NOT NULL DEFAULT 'SUCCESS'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `bid_history`
--

INSERT INTO `bid_history` (`bid_id`, `item_Id`, `bidder_Id`, `bid_amount`, `bid_time`, `status`) VALUES
(1, '01799397-9cb6-4358-a54c-e46389880ea0', '23312c96-d768-4e4f-b7ff-de57529c1e97', 8000000.00, '2026-05-25 13:22:26', 'SUCCESS'),
(2, '01799397-9cb6-4358-a54c-e46389880ea0', '969885f3-1093-4903-ad56-aa4e123306eb', 90000000.00, '2026-05-25 17:09:18', 'SUCCESS'),
(3, '01799397-9cb6-4358-a54c-e46389880ea0', '23312c96-d768-4e4f-b7ff-de57529c1e97', 150000000.00, '2026-05-26 07:48:07', 'SUCCESS'),
(4, '01799397-9cb6-4358-a54c-e46389880ea0', '969885f3-1093-4903-ad56-aa4e123306eb', 160000000.00, '2026-05-26 09:14:23', 'SUCCESS'),
(5, '01799397-9cb6-4358-a54c-e46389880ea0', '23312c96-d768-4e4f-b7ff-de57529c1e97', 180000000.00, '2026-05-26 09:22:48', 'SUCCESS'),
(6, '01799397-9cb6-4358-a54c-e46389880ea0', '969885f3-1093-4903-ad56-aa4e123306eb', 190000000.00, '2026-05-26 09:27:50', 'SUCCESS'),
(7, '01799397-9cb6-4358-a54c-e46389880ea0', '23312c96-d768-4e4f-b7ff-de57529c1e97', 200000000.00, '2026-05-26 10:00:37', 'SUCCESS'),
(8, '01799397-9cb6-4358-a54c-e46389880ea0', '969885f3-1093-4903-ad56-aa4e123306eb', 210000000.00, '2026-05-26 10:04:37', 'SUCCESS'),
(9, '01799397-9cb6-4358-a54c-e46389880ea0', '23312c96-d768-4e4f-b7ff-de57529c1e97', 220000000.00, '2026-05-26 10:21:08', 'SUCCESS'),
(10, '01799397-9cb6-4358-a54c-e46389880ea0', '969885f3-1093-4903-ad56-aa4e123306eb', 230000000.00, '2026-05-26 10:30:19', 'SUCCESS'),
(11, '01799397-9cb6-4358-a54c-e46389880ea0', '23312c96-d768-4e4f-b7ff-de57529c1e97', 240000000.00, '2026-05-26 10:35:20', 'SUCCESS'),
(12, '01799397-9cb6-4358-a54c-e46389880ea0', '969885f3-1093-4903-ad56-aa4e123306eb', 240000001.00, '2026-05-26 14:48:29', 'SUCCESS'),
(13, '1d27b154-9af4-4257-af67-6cbca151ce1f', '23312c96-d768-4e4f-b7ff-de57529c1e97', 45000000008.00, '2026-05-27 01:15:57', 'SUCCESS'),
(14, '1d27b154-9af4-4257-af67-6cbca151ce1f', '969885f3-1093-4903-ad56-aa4e123306eb', 46000000000.00, '2026-05-27 01:17:57', 'SUCCESS'),
(15, '1d27b154-9af4-4257-af67-6cbca151ce1f', '23312c96-d768-4e4f-b7ff-de57529c1e97', 46000000001.00, '2026-05-27 02:02:12', 'SUCCESS'),
(16, '1d27b154-9af4-4257-af67-6cbca151ce1f', '969885f3-1093-4903-ad56-aa4e123306eb', 46000000005.00, '2026-05-27 02:02:55', 'SUCCESS'),
(17, '1d27b154-9af4-4257-af67-6cbca151ce1f', '23312c96-d768-4e4f-b7ff-de57529c1e97', 46000000007.00, '2026-05-27 02:04:01', 'SUCCESS'),
(18, '1d27b154-9af4-4257-af67-6cbca151ce1f', '969885f3-1093-4903-ad56-aa4e123306eb', 46000000009.00, '2026-05-27 02:05:37', 'SUCCESS'),
(19, '01799397-9cb6-4358-a54c-e46389880ea0', '23312c96-d768-4e4f-b7ff-de57529c1e97', 250000000.00, '2026-05-27 13:37:23', 'SUCCESS'),
(20, '320e77a8-0267-46e5-ba03-6f3f03768957', '23312c96-d768-4e4f-b7ff-de57529c1e97', 210000.00, '2026-05-27 16:39:09', 'SUCCESS'),
(21, '320e77a8-0267-46e5-ba03-6f3f03768957', '969885f3-1093-4903-ad56-aa4e123306eb', 218500.00, '2026-05-27 16:47:43', 'SUCCESS'),
(22, '320e77a8-0267-46e5-ba03-6f3f03768957', '23312c96-d768-4e4f-b7ff-de57529c1e97', 227300.00, '2026-05-27 16:55:30', 'SUCCESS'),
(23, '320e77a8-0267-46e5-ba03-6f3f03768957', '969885f3-1093-4903-ad56-aa4e123306eb', 237000.00, '2026-05-27 17:11:49', 'SUCCESS'),
(24, '320e77a8-0267-46e5-ba03-6f3f03768957', '23312c96-d768-4e4f-b7ff-de57529c1e97', 247000.00, '2026-05-31 06:11:33', 'SUCCESS'),
(25, '972e8aac-5ab6-4619-b586-07fd3afb2650', '23312c96-d768-4e4f-b7ff-de57529c1e97', 40000000.00, '2026-05-31 06:14:44', 'SUCCESS'),
(26, '320e77a8-0267-46e5-ba03-6f3f03768957', '969885f3-1093-4903-ad56-aa4e123306eb', 257000.00, '2026-06-01 04:12:30', 'SUCCESS'),
(27, '320e77a8-0267-46e5-ba03-6f3f03768957', '23312c96-d768-4e4f-b7ff-de57529c1e97', 268000.00, '2026-06-01 04:41:10', 'SUCCESS'),
(28, '320e77a8-0267-46e5-ba03-6f3f03768957', '969885f3-1093-4903-ad56-aa4e123306eb', 300000.00, '2026-06-01 12:18:58', 'SUCCESS'),
(29, 'ed9e068a-9541-49e0-bc4a-dddd5a508816', '969885f3-1093-4903-ad56-aa4e123306eb', 110000.00, '2026-06-02 02:10:22', 'SUCCESS');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `electronic_items`
--

CREATE TABLE `electronic_items` (
  `id` varchar(40) NOT NULL,
  `brand` varchar(100) NOT NULL,
  `model` varchar(100) NOT NULL,
  `warrantyMonths` int(11) NOT NULL,
  `itemCondition` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `electronic_items`
--

INSERT INTO `electronic_items` (`id`, `brand`, `model`, `warrantyMonths`, `itemCondition`) VALUES
('972e8aac-5ab6-4619-b586-07fd3afb2650', '', '', 24, '');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `items`
--

CREATE TABLE `items` (
  `id` varchar(40) NOT NULL,
  `type` varchar(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `starting_price` decimal(15,2) NOT NULL,
  `current_price` decimal(15,2) NOT NULL,
  `seller_Id` varchar(40) NOT NULL,
  `highest_Bidder_Id` varchar(40) DEFAULT NULL,
  `price_Increment` decimal(15,2) NOT NULL DEFAULT 0.00,
  `image_path` varchar(255) DEFAULT NULL,
  `status` varchar(30) NOT NULL DEFAULT 'PENDING_APPROVAL',
  `duration_time` int(255) NOT NULL,
  `start_time` timestamp NULL DEFAULT NULL,
  `end_time` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `items`
--

INSERT INTO `items` (`id`, `type`, `name`, `description`, `starting_price`, `current_price`, `seller_Id`, `highest_Bidder_Id`, `price_Increment`, `image_path`, `status`, `duration_time`, `start_time`, `end_time`) VALUES
('01799397-9cb6-4358-a54c-e46389880ea0', 'Art', 'randompicture', 'drawn by a 2 yo kid', 5000000.00, 250000000.00, 'ac75be0f-a079-4e8d-8b86-bd586ce2f2d5', '23312c96-d768-4e4f-b7ff-de57529c1e97', 0.00, NULL, 'PENDING_APPROVAL', 600, NULL, NULL),
('1d27b154-9af4-4257-af67-6cbca151ce1f', 'Vehicle', 'Mercedes-Benz 300SL', 'Manufactured in 1954', 45000000000.00, 46000000009.00, 'ac75be0f-a079-4e8d-8b86-bd586ce2f2d5', '969885f3-1093-4903-ad56-aa4e123306eb', 4.00, NULL, 'PENDING_APPROVAL', 600, NULL, NULL),
('320e77a8-0267-46e5-ba03-6f3f03768957', 'Art', 'Job application', 'A JOB', 200000.00, 300000.00, 'ac75be0f-a079-4e8d-8b86-bd586ce2f2d5', '969885f3-1093-4903-ad56-aa4e123306eb', 4.00, '1779889715308_Screenshot 2026-05-27 204806.png', 'FINISHED', 600, NULL, NULL),
('8a7a9079-28ff-4429-9753-6939d3ddadd4', 'Art', 'anotherRandomBs', '', 150000.00, 150000.00, 'ac75be0f-a079-4e8d-8b86-bd586ce2f2d5', NULL, 3.00, NULL, 'PENDING_APPROVAL', 10, NULL, NULL),
('972e8aac-5ab6-4619-b586-07fd3afb2650', 'Electronics', 'Iphone 18 ProMax', 'Iphone 17 but better', 36000000.00, 40000000.00, 'ac75be0f-a079-4e8d-8b86-bd586ce2f2d5', '23312c96-d768-4e4f-b7ff-de57529c1e97', 6.00, '1779872725865_tải xuống.jpg', 'PENDING_APPROVAL', 600, NULL, NULL),
('ed9e068a-9541-49e0-bc4a-dddd5a508816', 'Art', 'randombs', '', 100000.00, 110000.00, 'ac75be0f-a079-4e8d-8b86-bd586ce2f2d5', '969885f3-1093-4903-ad56-aa4e123306eb', 4.00, NULL, 'PENDING_APPROVAL', 3, NULL, NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `sellers`
--

CREATE TABLE `sellers` (
  `id` varchar(40) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `sellers`
--

INSERT INTO `sellers` (`id`) VALUES
('ac75be0f-a079-4e8d-8b86-bd586ce2f2d5');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `users`
--

CREATE TABLE `users` (
  `id` varchar(40) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `role`) VALUES
('001', 'Onion', '123456789', 'Admin'),
('23312c96-d768-4e4f-b7ff-de57529c1e97', 'bidder', '123', 'Bidder'),
('969885f3-1093-4903-ad56-aa4e123306eb', 'Bidder2', '1234', 'Bidder'),
('ac75be0f-a079-4e8d-8b86-bd586ce2f2d5', 'seller', '123', 'Seller');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `vehicle`
--

CREATE TABLE `vehicle` (
  `id` varchar(40) NOT NULL,
  `licensePlate` varchar(20) NOT NULL,
  `mileage` int(11) NOT NULL,
  `manufacturingYear` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `vehicle`
--

INSERT INTO `vehicle` (`id`, `licensePlate`, `mileage`, `manufacturingYear`) VALUES
('1d27b154-9af4-4257-af67-6cbca151ce1f', '', 0, 0);

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `admins`
--
ALTER TABLE `admins`
  ADD KEY `fk_admin_users` (`id`);

--
-- Chỉ mục cho bảng `artworks`
--
ALTER TABLE `artworks`
  ADD KEY `fk_artworks_items` (`id`);

--
-- Chỉ mục cho bảng `bidders`
--
ALTER TABLE `bidders`
  ADD KEY `fk_bidders_users` (`id`);

--
-- Chỉ mục cho bảng `bid_history`
--
ALTER TABLE `bid_history`
  ADD PRIMARY KEY (`bid_id`),
  ADD KEY `fk_bid_history_items` (`item_Id`),
  ADD KEY `fk_bid_history_users` (`bidder_Id`);

--
-- Chỉ mục cho bảng `electronic_items`
--
ALTER TABLE `electronic_items`
  ADD KEY `fk_electronic_items_items` (`id`);

--
-- Chỉ mục cho bảng `items`
--
ALTER TABLE `items`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_items_sellers` (`seller_Id`),
  ADD KEY `fk_items_bidders` (`highest_Bidder_Id`);

--
-- Chỉ mục cho bảng `sellers`
--
ALTER TABLE `sellers`
  ADD KEY `fk_sellers_users` (`id`);

--
-- Chỉ mục cho bảng `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `vehicle`
--
ALTER TABLE `vehicle`
  ADD KEY `fk_vehicle_items` (`id`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `bid_history`
--
ALTER TABLE `bid_history`
  MODIFY `bid_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `admins`
--
ALTER TABLE `admins`
  ADD CONSTRAINT `fk_admin_users` FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `artworks`
--
ALTER TABLE `artworks`
  ADD CONSTRAINT `fk_artworks_items` FOREIGN KEY (`id`) REFERENCES `items` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `bidders`
--
ALTER TABLE `bidders`
  ADD CONSTRAINT `fk_bidders_users` FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `bid_history`
--
ALTER TABLE `bid_history`
  ADD CONSTRAINT `fk_bid_history_items` FOREIGN KEY (`item_Id`) REFERENCES `items` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_bid_history_users` FOREIGN KEY (`bidder_Id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `electronic_items`
--
ALTER TABLE `electronic_items`
  ADD CONSTRAINT `fk_electronic_items_items` FOREIGN KEY (`id`) REFERENCES `items` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `items`
--
ALTER TABLE `items`
  ADD CONSTRAINT `fk_items_bidders` FOREIGN KEY (`highest_Bidder_Id`) REFERENCES `bidders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_items_sellers` FOREIGN KEY (`seller_Id`) REFERENCES `sellers` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `sellers`
--
ALTER TABLE `sellers`
  ADD CONSTRAINT `fk_sellers_users` FOREIGN KEY (`id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Các ràng buộc cho bảng `vehicle`
--
ALTER TABLE `vehicle`
  ADD CONSTRAINT `fk_vehicle_items` FOREIGN KEY (`id`) REFERENCES `items` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
