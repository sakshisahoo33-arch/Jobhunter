package com.jobhunter.dao;

import com.jobhunter.model.User;
import com.jobhunter.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.mindrot.jbcrypt.BCrypt;
import java.util.List;

/**
 * Data Access Object for users.
 *
 * Implements basic CRUD, authentication and utility methods.
 * All methods use parameterized queries and try-with-resources.
 */
public class UserDAO {

    /** Register a new user. Returns the created user with ID populated, or null on failure. */
    public User registerUser(User user) {
        final String sql = "INSERT INTO users (full_name, email, password, phone) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            // Hash password before storing
            String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            ps.setString(3, hashed);
            ps.setString(4, user.getPhone());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                return null;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setUserId(keys.getInt(1));
                    return user;
                }
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to register user", e);
        }
    }

    /** Authenticate a user using email and password. Returns the User on success or null. */
    public User loginUser(String email, String password) {
        final String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String stored = rs.getString("password");
                    boolean match = false;
                    try {
                        match = BCrypt.checkpw(password, stored);
                    } catch (Exception e) {
                        // ignore and fallback to plain text check
                    }
                    if (match || password.equals(stored)) {
                        return mapRow(rs);
                    }
                }
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to login user", e);
        }
    }

    /** Find a user by id. */
    public User getUserById(int userId) {
        final String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user by id", e);
        }
    }

    /** Find a user by email. */
    public User getUserByEmail(String email) {
        final String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user by email", e);
        }
    }

    /** Return all users ordered by creation time (newest first). */
    public List<User> getAllUsers() {
        final String sql = "SELECT * FROM users ORDER BY created_at DESC";
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch all users", e);
        }
    }

    /** Update an existing user. Returns true when the update affected a row. */
    public boolean updateUser(User user) {
        final String sql = "UPDATE users SET full_name = ?, email = ?, password = ?, phone = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhone());
            ps.setInt(5, user.getUserId());

            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user", e);
        }
    }

    /** Delete a user by id. Returns true if deletion succeeded. */
    public boolean deleteUser(int userId) {
        final String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    /** Check whether an email is already registered. */
    public boolean emailExists(String email) {
        final String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check email existence", e);
        }
    }

    /** Map the current ResultSet row to a User instance. */
    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setPhone(rs.getString("phone"));
        return u;
    }
}
