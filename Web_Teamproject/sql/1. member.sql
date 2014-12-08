CREATE DATABASE project DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

GRANT ALL ON project.* TO 'webadmin'@'localhost' IDENTIFIED BY 'admin';

use project 

CREATE TABLE member (
  `userid` VARCHAR(20),
  `userpassword` VARCHAR(40) NOT NULL,
  `registerdate` DATETIME NOT NULL,
  `lastname` VARCHAR(20) NOT NULL,
  `firstname` VARCHAR(20) NOT NULL,
  `nickname` VARCHAR(20) NOT NULL,
  `profilephoto` VARCHAR(60),
  `gender` ENUM('M','F') NOT NULL,
  `email` VARCHAR(50) NOT NULL,
  `introduce` TEXT,
  `website` VARCHAR(50),
  PRIMARY KEY(userid)
) ENGINE=MyISAM;