package com.jobhunter.dao;

import com.jobhunter.model.Application;
import com.jobhunter.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the `Applications` table.
 * Uses prepared statements and try-with-resources. No business logic.
 */
public class ApplicationDAO {

    /** Apply for a job. Returns application with generated id, or null on failure. */
    public Application applyJob(Application application) {
        final String sql = "INSERT INTO Applications (user_id, job_id, resume_id, cover_letter, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, application.getUserId());
            ps.setInt(2, application.getJobId());

            if (application.getResumeId() > 0) {
                ps.setInt(3, application.getResumeId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }

            ps.setString(4, application.getCoverLetter());
            ps.setString(5, application.getStatus() != null ? application.getStatus() : "PENDING");

            int affected = ps.executeUpdate();
            if (affected == 0) return null;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    application.setId(keys.getInt(1));
                    return application;
                }
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to apply for job", e);
        }
    }

    /** Retrieve an application by id. */
    public Application getApplicationById(int id) {
        final String sql = "SELECT id, user_id, job_id, resume_id, cover_letter, status FROM Applications WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch application by id", e);
        }
    }

    /** Get all applications for a user. */
    public List<Application> getApplicationsByUser(int userId) {
        final String sql = "SELECT id, user_id, job_id, resume_id, cover_letter, status FROM Applications WHERE user_id = ? ORDER BY applied_at DESC";
        List<Application> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch applications by user", e);
        }
    }

    /** Get all applications for a job. */
    public List<Application> getApplicationsByJob(int jobId) {
        final String sql = "SELECT id, user_id, job_id, resume_id, cover_letter, status FROM Applications WHERE job_id = ? ORDER BY applied_at DESC";
        List<Application> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch applications by job", e);
        }
    }

    /** Get applicants (applications) for all jobs belonging to a company. */
    public List<Application> getApplicantsForCompany(int companyId) {
        final String sql = "SELECT a.id, a.user_id, a.job_id, a.resume_id, a.cover_letter, a.status " +
                "FROM Applications a JOIN Jobs j ON a.job_id = j.id WHERE j.company_id = ? ORDER BY a.applied_at DESC";
        List<Application> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch applicants for company", e);
        }
    }

    /** Update only the application status. Returns true if updated. */
    public boolean updateApplicationStatus(int applicationId, String status) {
        final String sql = "UPDATE Applications SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, applicationId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update application status", e);
        }
    }

    /** Delete an application by id. */
    public boolean deleteApplication(int applicationId) {
        final String sql = "DELETE FROM Applications WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, applicationId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete application", e);
        }
    }

    /** Check whether a user already applied to a job. */
    public boolean hasAlreadyApplied(int userId, int jobId) {
        final String sql = "SELECT 1 FROM Applications WHERE user_id = ? AND job_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check existing application", e);
        }
    }

    private Application mapRow(ResultSet rs) throws SQLException {
        Application a = new Application();
        a.setId(rs.getInt("id"));
        a.setUserId(rs.getInt("user_id"));
        a.setJobId(rs.getInt("job_id"));
        int resumeId = rs.getInt("resume_id");
        if (!rs.wasNull()) a.setResumeId(resumeId);
        a.setCoverLetter(rs.getString("cover_letter"));
        a.setStatus(rs.getString("status"));
        return a;
    }
}
