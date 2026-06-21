package com.jobhunter.dao;

import com.jobhunter.model.SavedJob;
import com.jobhunter.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the `saved_jobs` table.
 * Provides methods to save/remove and list saved jobs.
 */
public class SavedJobDAO {

    /** Save a job for a user. Returns the SavedJob with generated id, or null on failure. */
    public SavedJob saveJob(SavedJob savedJob) {
        final String sql = "INSERT INTO saved_jobs (user_id, job_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, savedJob.getUserId());
            ps.setInt(2, savedJob.getJobId());

            int affected = ps.executeUpdate();
            if (affected == 0) return null;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    savedJob.setSavedId(keys.getInt(1));
                    return savedJob;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save job", e);
        }
    }

    /** Remove a saved job by user and job id. Returns true if removed. */
    public boolean removeSavedJob(int userId, int jobId) {
        final String sql = "DELETE FROM saved_jobs WHERE user_id = ? AND job_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, jobId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove saved job", e);
        }
    }

    /** Get saved jobs for a user. */
    public List<SavedJob> getSavedJobs(int userId) {
        final String sql = "SELECT saved_id, user_id, job_id FROM saved_jobs WHERE user_id = ? ORDER BY saved_at DESC";
        List<SavedJob> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SavedJob s = new SavedJob();
                    s.setSavedId(rs.getInt("saved_id"));
                    s.setUserId(rs.getInt("user_id"));
                    s.setJobId(rs.getInt("job_id"));
                    list.add(s);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch saved jobs", e);
        }
    }

    /** Check if a job is saved by a user. */
    public boolean isSaved(int userId, int jobId) {
        final String sql = "SELECT 1 FROM saved_jobs WHERE user_id = ? AND job_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check saved job", e);
        }
    }
}
