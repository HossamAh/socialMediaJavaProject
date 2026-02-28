package com.example.demo.dao.impl;

import com.example.demo.dao.UserDAO;
import com.example.demo.model.User;
import com.example.demo.Database.DBConnection;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    @Override
    public User createUser(User user) throws Exception {
        String sql = "INSERT INTO users (username, email, password, profile_picture, bio) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword()); // لازم يكون HASH
            ps.setString(4, user.getProfilePicture());
            ps.setString(5, user.getBio());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getInt(1));
            }
        }

        return user;
    }

    @Override
    public User getUserById(int id) throws Exception {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractUser(rs);
            }
        }

        return null;
    }

    @Override
    public User getUserByUsername(String username) throws Exception {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractUser(rs);
            }
        }

        return null;
    }

    @Override
    public User getUserByEmail(String email) throws Exception {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractUser(rs);
            }
        }

        return null;
    }

    @Override
    public List<User> getAllUsers() throws Exception {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(extractUser(rs));
            }
        }

        return users;
    }

    @Override
    public User updateUser(User user) throws Exception {
        String sql = "UPDATE users SET username = ?, email = ?, password = ?, profile_picture = ?, bio = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword()); // HASH
            ps.setString(4, user.getProfilePicture());
            ps.setString(5, user.getBio());
            ps.setInt(6, user.getId());

            ps.executeUpdate();
        }

        return user;
    }

    @Override
    public boolean deleteUser(int id) throws Exception {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean authenticate(String username, String password) throws Exception {

        User user = getUserByUsername(username);

        if (user == null) {
            return false;
        }

        BCrypt.Result result = BCrypt.verifyer()
                .verify(password.toCharArray(), user.getPassword());

        return result.verified;
    }

    @Override
    public List<User> searchUsers(String keyword) throws Exception {

        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE username LIKE ? OR email LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";

            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                users.add(extractUser(rs));
            }
        }

        return users;
    }

    private User extractUser(ResultSet rs) throws SQLException {

        User user = new User();

        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password")); // HASH
        user.setProfilePicture(rs.getString("profile_picture"));
        user.setBio(rs.getString("bio"));

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            user.setCreatedAt(ts.toLocalDateTime());
        }

        return user;
    }
}