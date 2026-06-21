CREATE DATABASE IF NOT EXISTS `jobhunter` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `jobhunter`;

-- Disable foreign key checks to ensure clean drops
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `job_skills`;
DROP TABLE IF EXISTS `user_skills`;
DROP TABLE IF EXISTS `saved_jobs`;
DROP TABLE IF EXISTS `applications`;
DROP TABLE IF EXISTS `resumes`;
DROP TABLE IF EXISTS `jobs`;
DROP TABLE IF EXISTS `skills`;
DROP TABLE IF EXISTS `companies`;
DROP TABLE IF EXISTS `users`;

SET FOREIGN_KEY_CHECKS = 1;

-- 1. Users table
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `users` WRITE;
INSERT INTO `users` VALUES 
(1,'Snehil Singh','snehil@gmail.com','123456','9876543210','2026-06-19 12:23:48'),
(2,'Rahul Kumar','rahul@gmail.com','123456','9876543211','2026-06-19 12:23:48'),
(3,'Aman Gupta','aman@gmail.com','123456','9876543212','2026-06-19 12:23:48'),
(4,'Priya Sharma','priya@gmail.com','123456','9876543213','2026-06-19 12:23:48'),
(5,'Neha Verma','neha@gmail.com','123456','9876543214','2026-06-19 12:23:48');
UNLOCK TABLES;

-- 2. Companies table
CREATE TABLE `companies` (
  `company_id` int NOT NULL AUTO_INCREMENT,
  `company_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `industry` varchar(100) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`company_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `companies` WRITE;
INSERT INTO `companies` VALUES 
(1,'Google','google@gmail.com','google123','Technology','2026-06-19 12:23:29'),
(2,'Microsoft','microsoft@gmail.com','microsoft123','Technology','2026-06-19 12:23:29'),
(3,'Amazon','amazon@gmail.com','amazon123','E-Commerce','2026-06-19 12:23:29'),
(4,'Infosys','infosys@gmail.com','infosys123','IT Services','2026-06-19 12:23:29'),
(5,'TCS','tcs@gmail.com','tcs123','IT Services','2026-06-19 12:23:29');
UNLOCK TABLES;

-- 3. Skills table
CREATE TABLE `skills` (
  `skill_id` int NOT NULL AUTO_INCREMENT,
  `skill_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`skill_id`),
  UNIQUE KEY `skill_name` (`skill_name`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `skills` WRITE;
INSERT INTO `skills` VALUES 
(8,'Excel'),
(1,'Java'),
(7,'Machine Learning'),
(4,'Power BI'),
(2,'Python'),
(5,'React'),
(6,'Spring Boot'),
(3,'SQL');
UNLOCK TABLES;

-- 4. Jobs table
CREATE TABLE `jobs` (
  `job_id` int NOT NULL AUTO_INCREMENT,
  `company_id` int NOT NULL,
  `title` varchar(150) NOT NULL,
  `description` text,
  `location` varchar(100) DEFAULT NULL,
  `salary_min` decimal(10,2) DEFAULT NULL,
  `salary_max` decimal(10,2) DEFAULT NULL,
  `posted_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`job_id`),
  KEY `company_id` (`company_id`),
  CONSTRAINT `jobs_ibfk_1` FOREIGN KEY (`company_id`) REFERENCES `companies` (`company_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `jobs` WRITE;
INSERT INTO `jobs` VALUES 
(1,1,'Data Analyst','Analyze business data and generate insights','Bangalore',600000.00,1000000.00,'2026-06-19 12:24:08'),
(2,2,'Backend Developer','Develop REST APIs using Spring Boot','Hyderabad',700000.00,1200000.00,'2026-06-19 12:24:08'),
(3,3,'Data Scientist','Build ML models and pipelines','Pune',900000.00,1500000.00,'2026-06-19 12:24:08'),
(4,4,'Java Developer','Develop enterprise applications','Chennai',500000.00,900000.00,'2026-06-19 12:24:08'),
(5,5,'Business Analyst','Gather requirements and prepare reports','Mumbai',550000.00,950000.00,'2026-06-19 12:24:08');
UNLOCK TABLES;

-- 5. Resumes table
CREATE TABLE `resumes` (
  `resume_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `file_path` varchar(500) DEFAULT NULL,
  `upload_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`resume_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `resumes_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `resumes` WRITE;
INSERT INTO `resumes` VALUES 
(1,1,'Snehil_Resume.pdf','/resumes/snehil.pdf','2026-06-19 12:24:33'),
(2,2,'Rahul_Resume.pdf','/resumes/rahul.pdf','2026-06-19 12:24:33'),
(3,3,'Aman_Resume.pdf','/resumes/aman.pdf','2026-06-19 12:24:33'),
(4,4,'Priya_Resume.pdf','/resumes/priya.pdf','2026-06-19 12:24:33'),
(5,5,'Neha_Resume.pdf','/resumes/neha.pdf','2026-06-19 12:24:33');
UNLOCK TABLES;

-- 6. Applications table
CREATE TABLE `applications` (
  `application_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `job_id` int NOT NULL,
  `application_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('Pending','Shortlisted','Rejected','Selected') DEFAULT 'Pending',
  PRIMARY KEY (`application_id`),
  UNIQUE KEY `user_id` (`user_id`,`job_id`),
  KEY `job_id` (`job_id`),
  CONSTRAINT `applications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `applications_ibfk_2` FOREIGN KEY (`job_id`) REFERENCES `jobs` (`job_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `applications` WRITE;
INSERT INTO `applications` VALUES 
(1,1,1,'2026-06-19 12:25:45','Pending'),
(2,1,2,'2026-06-19 12:25:45','Shortlisted'),
(3,2,3,'2026-06-19 12:25:45','Pending'),
(4,3,1,'2026-06-19 12:25:45','Rejected'),
(5,4,5,'2026-06-19 12:25:45','Selected'),
(6,5,2,'2026-06-19 12:25:45','Pending');
UNLOCK TABLES;

-- 7. Saved Jobs table
CREATE TABLE `saved_jobs` (
  `saved_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `job_id` int NOT NULL,
  `saved_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`saved_id`),
  UNIQUE KEY `user_id` (`user_id`,`job_id`),
  KEY `job_id` (`job_id`),
  CONSTRAINT `saved_jobs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `saved_jobs_ibfk_2` FOREIGN KEY (`job_id`) REFERENCES `jobs` (`job_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `saved_jobs` WRITE;
INSERT INTO `saved_jobs` VALUES 
(1,1,3,'2026-06-19 12:26:05'),
(2,1,5,'2026-06-19 12:26:05'),
(3,2,1,'2026-06-19 12:26:05'),
(4,3,2,'2026-06-19 12:26:05'),
(5,4,4,'2026-06-19 12:26:05'),
(6,5,1,'2026-06-19 12:26:05');
UNLOCK TABLES;

-- 8. User Skills join table
CREATE TABLE `user_skills` (
  `user_id` int NOT NULL,
  `skill_id` int NOT NULL,
  PRIMARY KEY (`user_id`,`skill_id`),
  KEY `skill_id` (`skill_id`),
  CONSTRAINT `user_skills_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `user_skills_ibfk_2` FOREIGN KEY (`skill_id`) REFERENCES `skills` (`skill_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `user_skills` WRITE;
INSERT INTO `user_skills` VALUES 
(2,1),(1,2),(3,2),(1,3),(4,3),(1,4),(5,5),(2,6),(3,7),(4,8);
UNLOCK TABLES;

-- 9. Job Skills join table
CREATE TABLE `job_skills` (
  `job_id` int NOT NULL,
  `skill_id` int NOT NULL,
  PRIMARY KEY (`job_id`,`skill_id`),
  KEY `skill_id` (`skill_id`),
  CONSTRAINT `job_skills_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `jobs` (`job_id`) ON DELETE CASCADE,
  CONSTRAINT `job_skills_ibfk_2` FOREIGN KEY (`skill_id`) REFERENCES `skills` (`skill_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `job_skills` WRITE;
INSERT INTO `job_skills` VALUES 
(2,1),(4,1),(3,2),(1,3),(1,4),(2,6),(3,7),(5,8);
UNLOCK TABLES;
