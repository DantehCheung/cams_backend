use cams;
DROP DATABASE IF EXISTS `cams`;
CREATE DATABASE `cams` /*!40100 DEFAULT CHARACTER SET utf8mb4 */ /*!80016 DEFAULT ENCRYPTION='N' */;

use cams;
CREATE TABLE `User` (
  `CNA` varchar(255) PRIMARY KEY NOT NULL,
  `emailDomain` varchar(255) NOT NULL COMMENT 'vtc.edu.hk',
  `salt` char(5) NOT NULL COMMENT 'genarate by system',
  `password` char(65) NOT NULL COMMENT 'add AS CONCAT(status,SHA2(CONCAT(password,salt), 256)) STORED  NOT NULL in table create sql
the first char is the state of the account(such as ! mean bock, 0 mean ok)the next 64 char is sha256
https://stackoverflow.com/questions/53617727/using-mysql-to-generate-sha-256-hashes',
  `accessLevel` int NOT NULL DEFAULT 10000 COMMENT '0=admin,100=老師/技術員,1000=student',
  `accessPage` int DEFAULT 0 COMMENT '用1表示有權access頁面,最多32個pages',
  `firstName` varchar(255) NOT NULL DEFAULT '',
  `lastName` varchar(255) NOT NULL DEFAULT '',
  `contentNo` char(8) NOT NULL,
  `campusID` int NOT NULL,
    `lastLoginTime` datetime DEFAULT (now()),
  `lastLoginIP` varchar(255),
  `loginFail` int NOT NULL DEFAULT 0
);

CREATE TABLE `UserCard` (
  `CardID` varchar(255) PRIMARY KEY NOT NULL,
  `CNA` varchar(255) NOT NULL
);

CREATE TABLE `Campus` (
  `campusID` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `campusShortName` varchar(10) UNIQUE NOT NULL COMMENT 'IVE(CW)',
  `campusName` varchar(255) NOT NULL
);

CREATE TABLE `Room` (
  `roomID` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `campusID` int NOT NULL,
  `roomNumber` varchar(255) NOT NULL COMMENT '349',
  `roomName` varchar(255) NOT NULL DEFAULT ''
);

CREATE TABLE `RoomRFID` (
  `roomID` int NOT NULL,
  `RFID` char(24) UNIQUE NOT NULL COMMENT 'save 24 character hex string
 use HEX() / 0x123456789ABCDEF',
  PRIMARY KEY (`roomID`, `RFID`)
);

CREATE TABLE `Device` (
  `deviceID` int PRIMARY KEY  NOT NULL AUTO_INCREMENT,
  `deviceName` varchar(255) NOT NULL,
  `price` decimal NOT NULL,
  `orderDate` date NOT NULL,
  `arriveDate` date,
  `maintenanceDate` date,
  `roomID` int NOT NULL,
  `state` char(1) NOT NULL COMMENT 'A = Available, B = Borrowed, M = Maintenance, L = Lost/Damaged, O = Overdue, R = Reserved, D = Destroyed',
  `remark` varchar(255) NOT NULL DEFAULT ''
);

CREATE TABLE `DeviceDoc` (
  `deviceID` int PRIMARY KEY NOT NULL,
  `docPath` varchar(255) NOT NULL
);

CREATE TABLE `DevicePartID` (
  `deviceID` int NOT NULL,
  `devicePartID` int NOT NULL,
  `devicePartName` varchar(255) NOT NULL,
  PRIMARY KEY (`deviceID`, `devicePartID`)
);

CREATE TABLE `DeviceRFID` (
  `deviceID` int NOT NULL,
  `devicePartID` int NOT NULL,
  `RFID` char(24) UNIQUE NOT NULL COMMENT 'save 24 character hex string
 use HEX() / 0x123456789ABCDEF',
  PRIMARY KEY (`deviceID`, `devicePartID`)
);

CREATE TABLE `DeviceBorrowRecord` (
  `borrowRecordID` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `borrowDate` date NOT NULL DEFAULT (CURDATE()),
  `deviceID` int NOT NULL,
  `borrowUserCNA` varchar(255) NOT NULL,
  `leasePeriod` date NOT NULL DEFAULT (DATE_ADD(CURDATE(), INTERVAL 14 DAY))
);

CREATE TABLE `DeviceReturnRecord` (
  `borrowRecordID` int PRIMARY KEY NOT NULL,
  `returnDate` date NOT NULL DEFAULT (CURDATE()),
  `checkRecordID` int
);

CREATE TABLE `CheckDeviceReturnRecord` (
  `checkRecordID` int PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `checkDT` datetime NOT NULL,
  `inspector` varchar(255) NOT NULL
);

CREATE TABLE `Log` (
  `DT` datetime NOT NULL DEFAULT (now()),
  `UserCNA` varchar(255) NOT NULL,
  `log` varchar(255) NOT NULL,
  PRIMARY KEY (`DT`, `UserCNA`)
);



ALTER TABLE `UserCard` COMMENT = 'the table for update and re-new the card safety';

ALTER TABLE `User` ADD FOREIGN KEY (`campusID`) REFERENCES `Campus` (`campusID`);

ALTER TABLE `UserCard` ADD FOREIGN KEY (`CNA`) REFERENCES `User` (`CNA`);

ALTER TABLE `Room` ADD FOREIGN KEY (`campusID`) REFERENCES `Campus` (`campusID`);

ALTER TABLE `RoomRFID` ADD FOREIGN KEY (`roomID`) REFERENCES `Room` (`roomID`);

ALTER TABLE `Device` ADD FOREIGN KEY (`roomID`) REFERENCES `Room` (`roomID`);

ALTER TABLE `DeviceDoc` ADD FOREIGN KEY (`deviceID`) REFERENCES `Device` (`deviceID`);

ALTER TABLE `DevicePartID` ADD FOREIGN KEY (`deviceID`) REFERENCES `Device` (`deviceID`);

ALTER TABLE `DeviceRFID`
ADD FOREIGN KEY (`deviceID`, `devicePartID`) REFERENCES `DevicePartID` (`deviceID`, `devicePartID`);

ALTER TABLE `DeviceBorrowRecord` ADD FOREIGN KEY (`deviceID`) REFERENCES `Device` (`deviceID`);

ALTER TABLE `DeviceBorrowRecord` ADD FOREIGN KEY (`borrowUserCNA`) REFERENCES `User` (`CNA`);

ALTER TABLE `DeviceReturnRecord` ADD FOREIGN KEY (`borrowRecordID`) REFERENCES `DeviceBorrowRecord` (`borrowRecordID`);

ALTER TABLE `DeviceReturnRecord` ADD FOREIGN KEY (`checkRecordID`) REFERENCES `CheckDeviceReturnRecord` (`checkRecordID`);

ALTER TABLE `CheckDeviceReturnRecord` ADD FOREIGN KEY (`inspector`) REFERENCES `User` (`CNA`);

ALTER TABLE `Log` ADD FOREIGN KEY (`UserCNA`) REFERENCES `User` (`CNA`);

DELIMITER $$
CREATE TRIGGER trg_DevicePartID_AutoIncrement
BEFORE INSERT ON DevicePartID
FOR EACH ROW
BEGIN
  -- Get the maximum devicePartID for the current deviceID
  SET NEW.devicePartID = (
    SELECT IFNULL(MAX(devicePartID), 0) + 1
    FROM DevicePartID
    WHERE deviceID = NEW.deviceID
  );
END$$
DELIMITER ;


-- SET FOREIGN_KEY_CHECKS = 0;

-- insert data here

INSERT INTO `Campus` (`campusID`, `campusShortName`, `campusName`)
VALUES
(1, 'IVE(CW)', 'Hong Kong Institute of Vocational Education (Chai Wan)'),
(2, 'IVE(ST)', 'Hong Kong Institute of Vocational Education (Sha Tin)'),
(3, 'Thei(CW)', 'Technological and Higher Education Institute of Hong Kong (Chai Wan)');

INSERT INTO `Room` (`roomID`, `campusID`, `roomNumber`, `roomName`)
VALUES
(1, 1, '348', 'Artificial Intelligence Lab'),
(2, 1, '349', 'InnoLab'),
(3, 1, '350', 'Game Development Lab');

INSERT INTO `User` (`CNA`, `emailDomain`, `salt`, `password`, `accessLevel`, `accessPage`, `firstName`, `lastName`, `contentNo`, `campusID`)
VALUES
('230026964', 'stu.vtc.edu.hk', 'A1B2C', '00117bcee0fef4a07a693800b9546bb8540bc80b9e76a2853a1017ddafcb7506c', 0, 65535, 'Andy', 'Tse', '96330895', 1),
('230243196', 'stu.vtc.edu.hk', 'A1B2C', '00117bcee0fef4a07a693800b9546bb8540bc80b9e76a2853a1017ddafcb7506c', 100, 63487, 'Ken', 'Lau', '54946051', 1),
('230104577', 'stu.vtc.edu.hk', 'A1B2C', '00117bcee0fef4a07a693800b9546bb8540bc80b9e76a2853a1017ddafcb7506c', 100, 63487, 'Danteh', 'Cheung', '97984901', 1),
('230326045', 'stu.vtc.edu.hk', 'A1B2C', '00117bcee0fef4a07a693800b9546bb8540bc80b9e76a2853a1017ddafcb7506c', 1000, 1540, 'Eric', 'Ho', '51115476', 1);

INSERT INTO `UserCard` (`CardID`, `CNA`)
VALUES
('1312098746', '230026964'),
('1312765546', '230243196'),
('1312624138', '230104577'),
('1311814074', '230326045');

INSERT INTO `RoomRFID` (`roomID`, `RFID`)
VALUES
(1, 'E2801170200013837343092B'),
(2, 'E2801170200003937343092B'),
(2, 'E2801170200011C37340092B'),
(3, 'E2801170200013237343092B');

INSERT INTO `Device` (`deviceID`, `deviceName`, `price`, `orderDate`, `arriveDate`, `maintenanceDate`, `roomID`, `state`, `remark`)
VALUES
(1, 'ItemA', 123, '2025-02-28', '2025-03-01', '2027-03-01', 2, 'A', 'remark for item A'),
(2, 'ItemB', 123, '2025-02-28', '2025-03-01', '2027-03-01', 2, 'A', 'remark for item B');

INSERT INTO `DevicePartID` (`deviceID`, `devicePartID`, `devicePartName`)
VALUES
(1, 1, 'Part1'),
(1, 2, 'Part2'),
(2, 1, 'Part1');

INSERT INTO `DeviceRFID` (`deviceID`, `devicePartID`, `RFID`)
VALUES
(1, 1, 'E2801170200001D37340092B'),
(1, 2, 'E2801170200003337343092B'),
(2, 1, 'E2801170200013437343092B');

-- SET FOREIGN_KEY_CHECKS = 1;