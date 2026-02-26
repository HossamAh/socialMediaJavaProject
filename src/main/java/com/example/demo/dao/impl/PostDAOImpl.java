package com.example.demo.dao.impl;

import com.example.demo.dao.PostDAO;
import com.example.demo.model.Post;
import com.example.demo.Database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAOImpl implements PostDAO {
    @Override
    public Post createPost(Post post) throws Exception {
        String sql = "INSERT INTO posts (user_id, content, image, privacy) VALUES (?, ?, ?, ?)";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, post.getUserId());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getImagePath());
            ps.setString(4, post.getPrivacy() != null ? post.getPrivacy() : "public");
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                post.setId(rs.getInt(1));
            }
        }
        return post;
    }

    @Override
    public Post getPostById(int id) throws Exception {
        String sql = "SELECT p.*, u.username FROM posts p JOIN users u ON p.user_id = u.id WHERE p.id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractPost(rs);
            }
        }
        return null;
    }

    @Override
    public List<Post> getPostsByUserId(int userId) throws Exception {
        List<Post> posts = new ArrayList<>();
        String sql = "CALL GetPostsOfUser(?)";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                posts.add(extractPost(rs));
            }
        }
        return posts;
    }
    @Override
    public List<Post> getPostsByUserIdPaginated(int userId, int page, int pageSize) throws Exception {
        List<Post> posts = new ArrayList<>();
        String sql = "CALL GetPostsOfUserPaginated(?, ?, ?)";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, page);
            ps.setInt(3, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                posts.add(extractPost(rs));
            }
        }
        return posts;
    }

    @Override
    public List<Post> getAllPosts() throws Exception {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.username FROM posts p JOIN users u ON p.user_id = u.id ORDER BY p.created_at DESC";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                posts.add(extractPost(rs));
            }
        }
        return posts;
    }

    @Override
    public List<Post> getPublicPosts() throws Exception {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, u.username FROM posts p JOIN users u ON p.user_id = u.id WHERE p.privacy = 'public' ORDER BY p.created_at DESC";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                posts.add(extractPost(rs));
            }
        }
        return posts;
    }

    @Override
    public Post updatePost(Post post) throws Exception {
        String sql = "UPDATE posts SET content = ?, image = ?, privacy = ? WHERE id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, post.getContent());
            ps.setString(2, post.getImagePath());
            ps.setString(3, post.getPrivacy());
            ps.setInt(4, post.getId());
            ps.executeUpdate();
        }
        return post;
    }

    @Override
    public boolean deletePost(int id) throws Exception {
        String sql = "DELETE FROM posts WHERE id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Post extractPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setId(rs.getInt("id"));
        post.setUserId(rs.getInt("user_id"));
        post.setContent(rs.getString("content"));
        post.setImagePath(rs.getString("image"));
        post.setPrivacy(rs.getString("privacy"));
        post.setAuthorUsername(rs.getString("username"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            post.setCreatedAt(ts.toLocalDateTime());
        }
        return post;
    }
}
