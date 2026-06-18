package com.jobhunter.dao;

import com.jobhunter.model.Resume;
import com.jobhunter.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the `Resumes` table.
 * Uses JDBC, PreparedStatement and try-with-resources.
 */
public class ResumeDAO {

    /** Upload a resume. Returns the Resume with generated id, or null on failure. */
    public Resume uploadResume(Resume resume) {
        final String sql = "INSERT INTO Resumes (user_id, file_name, file_path) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, resume.getUserId());
            ps.setString(2, resume.getFileName());
            ps.setString(3, resume.getFilePath());

            int affected = ps.executeUpdate();
            if (affected == 0) return null;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    resume.setId(keys.getInt(1));
                    return resume;
                }
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to upload resume", e);
        }
    }

    /** Update resume metadata (file name/path). Returns true if updated. */
    public boolean updateResume(Resume resume) {
        final String sql = "UPDATE Resumes SET file_name = ?, file_path = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, resume.getFileName());
            ps.setString(2, resume.getFilePath());
            ps.setInt(3, resume.getId());
            ps.setInt(4, resume.getUserId());

            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update resume", e);
        }
    }

    /** Get all resumes for a user ordered by upload time descending. */
    public List<Resume> getResumeByUser(int userId) {
        final String sql = "SELECT id, user_id, file_name, file_path FROM Resumes WHERE user_id = ? ORDER BY uploaded_at DESC";
        List<Resume> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Resume r = mapRow(rs);
                    list.add(r);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch resumes for user", e);
        }
    }

    /** Delete a resume by id. Returns true if deleted. */
    public boolean deleteResume(int resumeId) {
        final String sql = "DELETE FROM Resumes WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, resumeId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete resume", e);
        }
    }

    private Resume mapRow(ResultSet rs) throws SQLException {
        Resume r = new Resume();
        r.setId(rs.getInt("id"));
        r.setUserId(rs.getInt("user_id"));
        r.setFileName(rs.getString("file_name"));
        r.setFilePath(rs.getString("file_path"));
        return r;
    }
}
