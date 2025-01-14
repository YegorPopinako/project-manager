drop table if exists `project` cascade;
drop table if exists `task` cascade;
drop table if exists `user` cascade;

CREATE TABLE `project` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `description` varchar(255) NOT NULL,
                           `title` varchar(255) NOT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `task` (
                        `estimate` int DEFAULT NULL,
                        `end_point` datetime(6) DEFAULT NULL,
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `project_id` bigint DEFAULT NULL,
                        `start_point` datetime(6) DEFAULT NULL,
                        `updated_at` datetime(6) DEFAULT NULL,
                        `description` varchar(255) DEFAULT NULL,
                        `status` varchar(255) DEFAULT NULL,
                        `title` varchar(255) DEFAULT NULL,
                        `created_at` datetime(6) DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        KEY `FKk8qrwowg31kx7hp93sru1pdqa` (`project_id`),
                        CONSTRAINT `FKk8qrwowg31kx7hp93sru1pdqa` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `password` varchar(255) DEFAULT NULL,
                        `role` varchar(255) DEFAULT NULL,
                        `username` varchar(255) DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;