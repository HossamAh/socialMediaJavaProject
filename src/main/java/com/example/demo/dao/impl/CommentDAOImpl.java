package com.example.demo.dao.impl;

import com.example.demo.dao.CommentDAO;
import com.example.demo.model.Comment;
import com.example.demo.Database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAOImpl implements CommentDAO {
    @Override
    public Comment createComment(Comment comment) throws Exception {
        String sql = "INSERT INTO comments (user_id, post_id, content) VALUES (?, ?, ?)";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, comment.getUserId());
            ps.setInt(2, comment.getPostId());
            ps.setString(3, comment.getContent());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                comment.setId(rs.getInt(1));
            }
        }
        return comment;
    }

    @Override
    public Comment getCommentById(int id) throws Exception {
        String sql = "SELECT c.*, u.username FROM comments c JOIN users u ON c.user_id = u.id WHERE c.id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractComment(rs);
            }
        }
        return null;
    }

    @Override
    public List<Comment> getCommentsByPostId(int postId) throws Exception {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.username FROM comments c JOIN users u ON c.user_id = u.id WHERE c.post_id = ? ORDER BY c.created_at ASC";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                comments.add(extractComment(rs));
            }
        }
        return comments;
    }

    @Override
    public List<Comment> getCommentsByUserId(int userId) throws Exception {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.username FROM comments c JOIN users u ON c.user_id = u.id WHERE c.user_id = ? ORDER BY c.created_at DESC";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                comments.add(extractComment(rs));
            }
        }
        return comments;
    }

    @Override
    public Comment updateComment(Comment comment) throws Exception {
        String sql = "UPDATE comments SET content = ? WHERE id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, comment.getContent());
            ps.setInt(2, comment.getId());
            ps.executeUpdate();
        }
        return comment;
    }

    @Override
    public boolean deleteComment(int id) throws Exception {
        String sql = "DELETE FROM comments WHERE id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Comment extractComment(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getInt("id"));
        comment.setUserId(rs.getInt("user_id"));
        comment.setPostId(rs.getInt("post_id"));
        comment.setContent(rs.getString("content"));
        comment.setAuthorUsername(rs.getString("username"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            comment.setCreatedAt(ts.toLocalDateTime());
        }
        return comment;
    }
}
