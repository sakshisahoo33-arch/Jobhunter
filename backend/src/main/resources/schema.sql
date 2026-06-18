-- JobHunter SQL schema and sample data
-- JobHunter SQL schema and sample data

-- Create database if it does not exist and use it
CREATE DATABASE IF NOT EXISTS `jobhunter` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `jobhunter`;

-- Drop tables only if you want a clean slate (commented out by default)
-- DROP TABLE IF EXISTS JobSkills, UserSkills, SavedJobs, Applications, Resumes, Jobs, Skills, Companies, Users;

-- Users table
CREATE TABLE IF NOT EXISTS `Users` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `first_name` VARCHAR(100) NOT NULL,
    `last_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(150) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `role` VARCHAR(50) NOT NULL,
    `phone` VARCHAR(50),
    `resume_id` INT DEFAULT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_users_email` (`email`),
    INDEX `idx_users_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Companies table
CREATE TABLE IF NOT EXISTS `Companies` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(150) NOT NULL,
    `email` VARCHAR(150) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `phone` VARCHAR(50),
    `description` TEXT,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_companies_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Jobs table with ENUMs for work_mode and employment_type
CREATE TABLE IF NOT EXISTS `Jobs` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `company_id` INT NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `description` TEXT NOT NULL,
    `location` VARCHAR(150),
    `salary` VARCHAR(100),
    `work_mode` ENUM('ON_SITE','REMOTE','HYBRID') DEFAULT 'REMOTE',
    `employment_type` ENUM('FULL_TIME','PART_TIME','CONTRACT','INTERNSHIP','TEMPORARY') DEFAULT 'FULL_TIME',
    `status` ENUM('OPEN','CLOSED','PAUSED') DEFAULT 'OPEN',
    `posted_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`company_id`) REFERENCES `Companies`(`id`) ON DELETE CASCADE,
    INDEX `idx_jobs_company` (`company_id`),
    INDEX `idx_jobs_title` (`title`),
    INDEX `idx_jobs_location` (`location`),
    INDEX `idx_jobs_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Resumes table
CREATE TABLE IF NOT EXISTS `Resumes` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `file_name` VARCHAR(255) NOT NULL,
    `file_path` VARCHAR(500) NOT NULL,
    `uploaded_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `Users`(`id`) ON DELETE CASCADE,
    INDEX `idx_resumes_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Skills table
CREATE TABLE IF NOT EXISTS `Skills` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL UNIQUE,
    `description` VARCHAR(255),
    INDEX `idx_skills_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- UserSkills join table
CREATE TABLE IF NOT EXISTS `UserSkills` (
    `user_id` INT NOT NULL,
    `skill_id` INT NOT NULL,
    PRIMARY KEY (`user_id`, `skill_id`),
    FOREIGN KEY (`user_id`) REFERENCES `Users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`skill_id`) REFERENCES `Skills`(`id`) ON DELETE CASCADE,
    INDEX `idx_user_skills_user` (`user_id`),
    INDEX `idx_user_skills_skill` (`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- JobSkills join table
CREATE TABLE IF NOT EXISTS `JobSkills` (
    `job_id` INT NOT NULL,
    `skill_id` INT NOT NULL,
    PRIMARY KEY (`job_id`, `skill_id`),
    FOREIGN KEY (`job_id`) REFERENCES `Jobs`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`skill_id`) REFERENCES `Skills`(`id`) ON DELETE CASCADE,
    INDEX `idx_job_skills_job` (`job_id`),
    INDEX `idx_job_skills_skill` (`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Applications table with ENUM status
CREATE TABLE IF NOT EXISTS `Applications` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `job_id` INT NOT NULL,
    `resume_id` INT DEFAULT NULL,
    `cover_letter` TEXT,
    `status` ENUM('PENDING','REVIEWED','INTERVIEW','OFFERED','REJECTED','HIRED') DEFAULT 'PENDING',
    `applied_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `Users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`job_id`) REFERENCES `Jobs`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`resume_id`) REFERENCES `Resumes`(`id`) ON DELETE SET NULL,
    INDEX `idx_applications_user` (`user_id`),
    INDEX `idx_applications_job` (`job_id`),
    INDEX `idx_applications_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- SavedJobs table
CREATE TABLE IF NOT EXISTS `SavedJobs` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `job_id` INT NOT NULL,
    `saved_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `Users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`job_id`) REFERENCES `Jobs`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `ux_saved_user_job` (`user_id`, `job_id`),
    INDEX `idx_saved_jobs_user` (`user_id`),
    INDEX `idx_saved_jobs_job` (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Ensure Users.resume_id is indexed (no FK to avoid circular dependency)
ALTER TABLE `Users` ADD INDEX IF NOT EXISTS `idx_users_resume` (`resume_id`);

-- Sample data (use INSERT IGNORE to avoid duplicates)
INSERT IGNORE INTO `Skills` (`name`, `description`) VALUES
    ('Java', 'Java programming and enterprise experience'),
    ('SQL', 'Structured query language knowledge'),
    ('Spring', 'Spring framework familiarity'),
    ('MySQL', 'MySQL database administration and query optimization');

INSERT IGNORE INTO `Companies` (`name`, `email`, `password`, `phone`, `description`) VALUES
    ('JobHunter Inc', 'hr@jobhunter.com', 'SecurePass123', '+1234567890', 'A technology company hiring top talent.');

INSERT IGNORE INTO `Users` (`first_name`, `last_name`, `email`, `password`, `role`, `phone`) VALUES
    ('Alice', 'Johnson', 'alice@example.com', 'Password123', 'USER', '+15551234567'),
    ('Bob', 'Smith', 'bob@example.com', 'Password123', 'USER', '+15557654321'),
    ('Eve', 'Recruiter', 'eve@jobhunter.com', 'Password123', 'RECRUITER', '+15550001111');

INSERT IGNORE INTO `Jobs` (`company_id`, `title`, `description`, `location`, `salary`, `work_mode`, `employment_type`) VALUES
    (1, 'Java Backend Developer', 'Build MVC web applications using Servlets and JDBC.', 'Remote', '80000-100000', 'REMOTE', 'FULL_TIME'),
    (1, 'MySQL Database Administrator', 'Manage MySQL workloads and support JDBC applications.', 'New York, NY', '90000-110000', 'ON_SITE', 'FULL_TIME');

INSERT IGNORE INTO `Resumes` (`user_id`, `file_name`, `file_path`) VALUES
    (1, 'alice_resume.pdf', '/resumes/alice_resume.pdf');

INSERT IGNORE INTO `UserSkills` (`user_id`, `skill_id`) VALUES
    (1, 1),
    (1, 3),
    (2, 1),
    (2, 2);

INSERT IGNORE INTO `JobSkills` (`job_id`, `skill_id`) VALUES
    (1, 1),
    (1, 3),
    (2, 4);

INSERT IGNORE INTO `SavedJobs` (`user_id`, `job_id`) VALUES
    (1, 1);

INSERT IGNORE INTO `Applications` (`user_id`, `job_id`, `resume_id`, `cover_letter`) VALUES
    (1, 1, 1, 'I am excited to apply for the Java Backend Developer role.');

-- Additional useful indexes
CREATE INDEX IF NOT EXISTS `idx_users_created` ON `Users`(`created_at`);
CREATE INDEX IF NOT EXISTS `idx_companies_created` ON `Companies`(`created_at`);
CREATE INDEX IF NOT EXISTS `idx_jobs_posted` ON `Jobs`(`posted_at`);

-- Notes:
-- - Replace placeholder passwords with secure hashed values before production use.
-- - Consider moving `db.password` out of version control and using environment-specific configuration.

