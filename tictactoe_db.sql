-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 08, 2025 at 01:18 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `tictactoe_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `game_results`
--

CREATE TABLE `game_results` (
  `id` int(11) NOT NULL,
  `mode` varchar(20) DEFAULT NULL,
  `board_size` int(11) DEFAULT NULL,
  `winner` varchar(50) DEFAULT NULL,
  `timestamp` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `game_results`
--

INSERT INTO `game_results` (`id`, `mode`, `board_size`, `winner`, `timestamp`) VALUES
(1, 'singleplayer', 3, 'Human', '2025-06-07 03:15:49'),
(2, 'singleplayer', 3, 'Human', '2025-06-07 05:54:54'),
(3, 'singleplayer', 3, 'Bot', '2025-06-07 05:55:47'),
(4, 'singleplayer', 3, 'Human', '2025-06-07 05:55:51'),
(5, 'singleplayer', 3, 'Human', '2025-06-07 05:57:37'),
(6, 'singleplayer', 3, 'Draw', '2025-06-07 06:00:48'),
(7, 'singleplayer', 3, 'Human', '2025-06-07 06:00:51'),
(8, 'singleplayer', 3, 'Human', '2025-06-07 06:11:52'),
(9, 'singleplayer', 3, 'Human', '2025-06-07 06:13:28'),
(10, 'singleplayer', 3, 'Human', '2025-06-07 06:19:02'),
(11, 'singleplayer', 3, 'Human', '2025-06-07 06:21:05');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `game_results`
--
ALTER TABLE `game_results`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `game_results`
--
ALTER TABLE `game_results`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
