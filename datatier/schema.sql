CREATE DATABASE IF NOT EXISTS rental_ps;
USE rental_ps;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(255),
    role VARCHAR(20)
);

CREATE TABLE playstation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    status VARCHAR(20),
    price_per_hour DOUBLE
);

CREATE TABLE rental (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    ps_id INT,
    start_time DATETIME,
    end_time DATETIME,
    total_price DOUBLE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (ps_id) REFERENCES playstation(id)
);
