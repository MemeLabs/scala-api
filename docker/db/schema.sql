CREATE DATABASE dev_api
  DEFAULT CHARACTER SET utf8
  DEFAULT COLLATE utf8_general_ci;
use  dev_api;

CREATE TABLE `users` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`provider`, `provider_id`)
);

INSERT INTO `users` VALUES ("1","admin","ABZaSyAcb7om56Bq76PATOlZn_LdKSxa73BlMrG", DEFAULT, DEFAULT);