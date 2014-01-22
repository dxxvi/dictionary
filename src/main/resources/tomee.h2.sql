CREATE SCHEMA IF NOT EXISTS tomme;

CREATE TABLE IF NOT EXISTS tomee.my_files (
    id INT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR2(31) NOT NULL,
    file_size INT,
    file_comment VARCHAR2(821904),
    file_content BLOB
);