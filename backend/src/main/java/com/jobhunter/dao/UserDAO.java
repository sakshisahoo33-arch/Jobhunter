package com.jobhunter.dao;

import com.jobhunter.model.User;
import com.jobhunter.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for `Users` table.
 *
 * - Uses {@link DBConnection#getConnection()} to obtain JDBC connections.
 * - Uses PreparedStatement with parameterized queries only (no string concatenation).
 * - Uses try-with-resources to ensure resources are closed.
 * - Returns model objects and lists; contains no business logic.
 */
public class UserDAO {

    /** Register a new user. Returns the created User with id populated, or null on failure. */
    public User registerUser(User user) {
        final String sql = "INSERT INTO Users (first_name, last_name, email, password, role, phone, resume_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole());
            ps.setString(6, user.getPhone());
            if (user.getResumeId() > 0) {
                ps.setInt(7, user.getResumeId());
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }

            int affected = ps.executeUpdate();
            if (affected == 0) {
                return null;
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                    return user;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error registering user", e);
        }
    }

    /** Login user by email and password. Returns User on success, otherwise null. */
    public User loginUser(String email, String password) {
        final String sql = "SELECT * FROM Users WHERE email = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error logging in user", e);
        }
    }

    /** Get user by id. Returns null if not found. */
    public User getUserById(int id) {
        final String sql = "SELECT * FROM Users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user by id", e);
        }
    }

    /** Get user by email. Returns null if not found. */
    public User getUserByEmail(String email) {
        final String sql = "SELECT * FROM Users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user by email", e);
        }
    }

    /** Get all users ordered by creation time descending. */
    public List<User> getAllUsers() {
        final String sql = "SELECT * FROM Users ORDER BY created_at DESC";
        List<User> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all users", e);
        }
    }

    /** Update user information. Returns true if update succeeded. */
    public boolean updateUser(User user) {
        final String sql = "UPDATE Users SET first_name = ?, last_name = ?, email = ?, password = ?, role = ?, phone = ?, resume_id = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole());
            ps.setString(6, user.getPhone());
            if (user.getResumeId() > 0) {
                ps.setInt(7, user.getResumeId());
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }
            ps.setInt(8, user.getId());

            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user", e);
        }
    }

    /** Delete user by id. Returns true if deleted. */
    public boolean deleteUser(int id) {
        final String sql = "DELETE FROM Users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }

    /** Check if an email already exists in the database. */
    public boolean emailExists(String email) {
        final String sql = "SELECT 1 FROM Users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking email existence", e);
        }
    }

    /**
     * Map a ResultSet row to a User model instance.
     *
     * @param rs positioned ResultSet
     * @return populated User object
     * @throws SQLException on SQL errors
     */
    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setFirstName(rs.getString("first_name"));
        u.setLastName(rs.getString("last_name"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        u.setPhone(rs.getString("phone"));
        int resumeId = rs.getInt("resume_id");
        if (!rs.wasNull()) {
            u.setResumeId(resumeId);
        }
        return u;
    }
}
