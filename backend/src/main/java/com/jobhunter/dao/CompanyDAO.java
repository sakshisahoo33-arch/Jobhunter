package com.jobhunter.dao;

import com.jobhunter.model.Company;
import com.jobhunter.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the `Companies` table.
 * <p>
 * Provides CRUD, authentication (login), registration and search operations.
 * All DB access uses prepared statements and try-with-resources to prevent leaks and SQL injection.
 */
public class CompanyDAO {

    /** Register a new company. Returns the created Company with id set, or null on failure. */
    public Company registerCompany(Company company) {
        final String sql = "INSERT INTO Companies (name, email, password, phone, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, company.getName());
            ps.setString(2, company.getEmail());
            ps.setString(3, company.getPassword());
            ps.setString(4, company.getPhone());
            ps.setString(5, company.getDescription());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                return null;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    company.setId(keys.getInt(1));
                    return company;
                }
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to register company", e);
        }
    }

    /** Authenticate company by email and password. Returns Company on success, otherwise null. */
    public Company loginCompany(String email, String password) {
        final String sql = "SELECT * FROM Companies WHERE email = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to login company", e);
        }
    }

    /** Find company by id. */
    public Company getCompanyById(int id) {
        final String sql = "SELECT * FROM Companies WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch company by id", e);
        }
    }

    /** Find company by email. */
    public Company getCompanyByEmail(String email) {
        final String sql = "SELECT * FROM Companies WHERE email = ?";
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
            throw new RuntimeException("Failed to fetch company by email", e);
        }
    }

    /** Return all companies ordered by creation time descending. */
    public List<Company> getAllCompanies() {
        final String sql = "SELECT * FROM Companies ORDER BY created_at DESC";
        List<Company> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch companies", e);
        }
    }

    /** Update company record. Returns true if update affected a row. */
    public boolean updateCompany(Company company) {
        final String sql = "UPDATE Companies SET name = ?, email = ?, password = ?, phone = ?, description = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, company.getName());
            ps.setString(2, company.getEmail());
            ps.setString(3, company.getPassword());
            ps.setString(4, company.getPhone());
            ps.setString(5, company.getDescription());
            ps.setInt(6, company.getId());

            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update company", e);
        }
    }

    /** Delete a company by id. Returns true if deletion succeeded. */
    public boolean deleteCompany(int id) {
        final String sql = "DELETE FROM Companies WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete company", e);
        }
    }

    /** Check whether an email is already registered. */
    public boolean emailExists(String email) {
        final String sql = "SELECT 1 FROM Companies WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check company email existence", e);
        }
    }

    /** Search companies by name (wildcard). */
    public List<Company> searchByName(String query) {
        final String sql = "SELECT * FROM Companies WHERE name LIKE ? ORDER BY name";
        String wildcard = "%" + query + "%";
        List<Company> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, wildcard);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search companies by name", e);
        }
    }

    /** Map current row of ResultSet to Company model. */
    private Company mapRow(ResultSet rs) throws SQLException {
        Company c = new Company();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setEmail(rs.getString("email"));
        c.setPassword(rs.getString("password"));
        c.setPhone(rs.getString("phone"));
        c.setDescription(rs.getString("description"));
        return c;
    }
}
