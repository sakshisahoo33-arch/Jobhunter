package com.jobhunter.dao;

import com.jobhunter.model.Job;
import com.jobhunter.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the `Jobs` table. Uses prepared statements and try-with-resources.
 */
public class JobDAO {

    /** Create a new job and return it with generated id, or null on failure. */
    public Job createJob(Job job) {
        final String sql = "INSERT INTO Jobs (company_id, title, description, location, salary, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, job.getCompanyId());
            ps.setString(2, job.getTitle());
            ps.setString(3, job.getDescription());
            ps.setString(4, job.getLocation());
            ps.setString(5, job.getSalary());
            ps.setString(6, job.getStatus());

            int affected = ps.executeUpdate();
            if (affected == 0) return null;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    job.setId(keys.getInt(1));
                    return job;
                }
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create job", e);
        }
    }

    /** Get a job by id. */
    public Job getJobById(int id) {
        final String sql = "SELECT id, company_id, title, description, location, salary, status FROM Jobs WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch job by id", e);
        }
    }

    /** Get all jobs. */
    public List<Job> getAllJobs() {
        final String sql = "SELECT id, company_id, title, description, location, salary, status FROM Jobs ORDER BY id DESC";
        List<Job> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch jobs", e);
        }
    }

    /** Get jobs for a specific company. */
    public List<Job> getJobsByCompanyId(int companyId) {
        final String sql = "SELECT id, company_id, title, description, location, salary, status FROM Jobs WHERE company_id = ? ORDER BY id DESC";
        List<Job> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch jobs by company", e);
        }
    }

    /** Search jobs by title, description or location using wildcard. */
    public List<Job> searchJobs(String q) {
        final String sql = "SELECT id, company_id, title, description, location, salary, status FROM Jobs WHERE title LIKE ? OR description LIKE ? OR location LIKE ? ORDER BY id DESC";
        String w = "%" + q + "%";
        List<Job> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, w);
            ps.setString(2, w);
            ps.setString(3, w);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search jobs", e);
        }
    }

    /** Update job. Returns true if updated. */
    public boolean updateJob(Job job) {
        final String sql = "UPDATE Jobs SET title = ?, description = ?, location = ?, salary = ?, status = ? WHERE id = ? AND company_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, job.getTitle());
            ps.setString(2, job.getDescription());
            ps.setString(3, job.getLocation());
            ps.setString(4, job.getSalary());
            ps.setString(5, job.getStatus());
            ps.setInt(6, job.getId());
            ps.setInt(7, job.getCompanyId());

            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update job", e);
        }
    }

    /** Delete a job by id (and companyId to ensure ownership). */
    public boolean deleteJob(int id, int companyId) {
        final String sql = "DELETE FROM Jobs WHERE id = ? AND company_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setInt(2, companyId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete job", e);
        }
    }

    private Job mapRow(ResultSet rs) throws SQLException {
        Job j = new Job();
        j.setId(rs.getInt("id"));
        j.setCompanyId(rs.getInt("company_id"));
        j.setTitle(rs.getString("title"));
        j.setDescription(rs.getString("description"));
        j.setLocation(rs.getString("location"));
        j.setSalary(rs.getString("salary"));
        j.setStatus(rs.getString("status"));
        return j;
    }
}
