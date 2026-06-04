-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th6 04, 2026 lúc 05:14 AM
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
('001', 'admin', '123', 'Admin'),
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
  MODIFY `bid_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=59;

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
