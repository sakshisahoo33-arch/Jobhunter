package com.jobhunter.dao;

import com.jobhunter.model.Skill;
import com.jobhunter.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the `skills`, `user_skills` and `job_skills` tables.
 */
public class SkillDAO {

    /** Add a new skill. Returns the Skill with generated id, or null on failure. */
    public Skill addSkill(Skill skill) {
        final String sql = "INSERT INTO skills (skill_name) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, skill.getSkillName());

            int affected = ps.executeUpdate();
            if (affected == 0) return null;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    skill.setSkillId(keys.getInt(1));
                    return skill;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add skill", e);
        }
    }

    /** Return all skills ordered by name. */
    public List<Skill> getAllSkills() {
        final String sql = "SELECT skill_id, skill_name FROM skills ORDER BY skill_name";
        List<Skill> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Skill s = mapRow(rs);
                list.add(s);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch skills", e);
        }
    }

    /** Assign a skill to a user. Returns true if assignment was created or already exists. */
    public boolean assignSkillToUser(int userId, int skillId) {
        final String check = "SELECT 1 FROM user_skills WHERE user_id = ? AND skill_id = ?";
        final String insert = "INSERT INTO user_skills (user_id, skill_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(check)) {
                ps.setInt(1, userId);
                ps.setInt(2, skillId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return true; // already assigned
                }
            }

            try (PreparedStatement ps2 = conn.prepareStatement(insert)) {
                ps2.setInt(1, userId);
                ps2.setInt(2, skillId);
                int affected = ps2.executeUpdate();
                return affected > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to assign skill to user", e);
        }
    }

    /** Assign a skill to a job. Returns true if assignment was created or already exists. */
    public boolean assignSkillToJob(int jobId, int skillId) {
        final String check = "SELECT 1 FROM job_skills WHERE job_id = ? AND skill_id = ?";
        final String insert = "INSERT INTO job_skills (job_id, skill_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(check)) {
                ps.setInt(1, jobId);
                ps.setInt(2, skillId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return true; // already assigned
                }
            }

            try (PreparedStatement ps2 = conn.prepareStatement(insert)) {
                ps2.setInt(1, jobId);
                ps2.setInt(2, skillId);
                int affected = ps2.executeUpdate();
                return affected > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to assign skill to job", e);
        }
    }

    private Skill mapRow(ResultSet rs) throws SQLException {
        Skill s = new Skill();
        s.setSkillId(rs.getInt("skill_id"));
        s.setSkillName(rs.getString("skill_name"));
        return s;
    }
}
