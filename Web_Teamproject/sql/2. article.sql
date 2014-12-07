CREATE TABLE `project`.`article` (
  `postid` INT(4) UNSIGNED NOT NULL AUTO_INCREMENT,
  `userid` CHAR(20) NOT NULL,
  `albumid` CHAR(20) NOT NULL,
  `photo` VARCHAR(50) NOT NULL,
  `content` TEXT NOT NULL,
  `postdate` DATETIME NOT NULL,
  `category` VARCHAR(20) NOT NULL,
  `hits` int(4) unsigned NOT NULL DEFAULT '0',
  `likehits` int(4) unsigned NOT NULL DEFAULT '0',
  `postip` int(4) unsigned NOT NULL,
  PRIMARY KEY(postid)
) ENGINE=MyISAM;