-- Active: 1764784415907@@127.0.0.1@3306@socialmedia
DROP DATABASE IF EXISTS socialmedia;

CREATE DATABASE IF NOT EXISTS socialmedia;

USE socialmedia;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    profile_picture VARCHAR(255),
    bio TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS posts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    image VARCHAR(255),
    privacy ENUM(
        'public',
        'friends',
        'private'
    ) DEFAULT 'public',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS likes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    post_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE,
    UNIQUE (user_id, post_id)
);

CREATE TABLE IF NOT EXISTS friendships (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    friend_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friend_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    type VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- INSERT INTO posts (user_id, content, image, privacy) VALUES
-- (1, 'Hello world! This is my first post.', NULL, 'public'),
-- (2, 'Had a great day at the park!', 'park.jpg', 'friends'),
-- (3, 'Just finished reading a fantastic book.', NULL, 'private');

CREATE PROCEDURE GetFriendsOfUser(IN userId INT)
BEGIN
    SELECT u.id
    FROM users u
    JOIN friendships f ON u.id = f.friend_id
    WHERE f.user_id = userId
    UNION
    SELECT u.id
    FROM users u
    JOIN friendships f ON u.id = f.user_id
    WHERE f.friend_id = userId;
END

CREATE PROCEDURE GetPostsOfFriendsOfUser(IN userId INT)
BEGIN
    SELECT p.id, p.user_id, p.content, p.image, p.privacy, p.created_at
    FROM users u
    JOIN friendships f ON u.id = f.friend_id
    JOIN posts p ON u.id = p.user_id
    WHERE f.user_id = userId
    UNION
    SELECT p.id, p.user_id, p.content, p.image, p.privacy, p.created_at
    FROM users u
    JOIN friendships f ON u.id = f.user_id
    JOIN posts p ON u.id = p.user_id
    WHERE f.friend_id = userId;
END

CREATE PROCEDURE GetPostsOfUser(IN userId INT)
BEGIN
    SELECT p.id, p.user_id, p.content, p.image, p.privacy, p.created_at,u.username
    FROM users u
    JOIN friendships f ON u.id = f.friend_id
    JOIN posts p ON u.id = p.user_id
    WHERE f.user_id = userId AND p.privacy IN ('friends', 'public')
    UNION
    SELECT p.id, p.user_id, p.content, p.image, p.privacy, p.created_at,u.username
    FROM users u
    JOIN friendships f ON u.id = f.user_id
    JOIN posts p ON u.id = p.user_id
    WHERE f.friend_id = userId AND p.privacy IN ('friends', 'public')
    UNION
    SELECT p.id, p.user_id, p.content, p.image, p.privacy, p.created_at,u.username
    FROM posts p
    JOIN users u ON p.user_id = u.id
    WHERE p.user_id = userId
    UNION
    SELECT p.id, p.user_id, p.content, p.image, p.privacy, p.created_at,u.username
    FROM posts p
    JOIN users u ON p.user_id = u.id
    WHERE p.privacy = 'public';
END

CREATE PROCEDURE GetPostsOfUserPaginated(IN userId INT, IN page INT, IN pageSize INT)
BEGIN
    DECLARE offset INT DEFAULT (page - 1) * pageSize;
    SELECT p.id, p.user_id, p.content, p.image, p.privacy, p.created_at, u.username
    FROM users u
    JOIN friendships f ON u.id = f.friend_id
    JOIN posts p ON u.id = p.user_id
    WHERE f.user_id = userId AND p.privacy IN ('friends', 'public')
    UNION
    SELECT p.id, p.user_id, p.content, p.image, p.privacy, p.created_at, u.username
    FROM users u
    JOIN friendships f ON u.id = f.user_id
    JOIN posts p ON u.id = p.user_id
    WHERE f.friend_id = userId AND p.privacy IN ('friends', 'public')
    UNION
    SELECT p.id, p.user_id, p.content, p.image, p.privacy, p.created_at, u.username
    FROM posts p
    JOIN users u ON p.user_id = u.id
    WHERE p.user_id = userId
    UNION
    SELECT p.id, p.user_id, p.content, p.image, p.privacy, p.created_at, u.username
    FROM posts p
    JOIN users u ON p.user_id = u.id
    WHERE p.privacy = 'public'
    ORDER BY created_at DESC
    LIMIT pageSize OFFSET offset;
END

CALL GetPostsOfUser (1);

INSERT INTO posts (user_id, content, image, privacy) VALUES
(1, 'Hello world! This is my first post.', NULL, 'public'),
(1, 'Had a great day at the park!', 'park.jpg', 'friends'),
(1, 'Just finished reading a fantastic book.', NULL, 'private');