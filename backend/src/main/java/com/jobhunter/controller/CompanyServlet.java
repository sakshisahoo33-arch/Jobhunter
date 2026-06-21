package com.jobhunter.controller;

import com.google.gson.Gson;
import com.jobhunter.dao.CompanyDAO;
import com.jobhunter.dao.JobDAO;
import com.jobhunter.model.Company;
import com.jobhunter.model.Job;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;
import java.util.List;

@WebServlet(name = "CompanyServlet", urlPatterns = {"/company"})
public class CompanyServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final CompanyDAO companyDAO = new CompanyDAO();
    private final JobDAO jobDAO = new JobDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // action: register | create | edit | delete
        resp.setContentType("application/json");
        String action = req.getParameter("action");
        if (action == null) action = "register";

        try {
            switch (action) {
                case "register":
                    handleRegister(req, resp);
                    break;
                case "create":
                    handleCreateJob(req, resp);
                    break;
                case "edit":
                    handleEditJob(req, resp);
                    break;
                case "delete":
                    handleDeleteJob(req, resp);
                    break;
                default:
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Unknown action")));
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Internal server error: " + e.getMessage())));
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("companyName");
        if (name == null) name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String industry = req.getParameter("industry");

        if (name == null || email == null || password == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Missing required fields")));
            return;
        }

        if (companyDAO.emailExists(email)) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Email already registered")));
            return;
        }

        Company c = new Company();
        c.setCompanyName(name);
        c.setEmail(email);
        c.setPassword(password);
        c.setIndustry(industry);

        Company created = companyDAO.registerCompany(c);
        if (created == null) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Failed to register company")));
            return;
        }

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write(gson.toJson(Map.of("success", true, "company", created)));
    }

    private void handleCreateJob(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("company") == null) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Company authentication required")));
            return;
        }
        Company company = (Company) session.getAttribute("company");
        int companyId = company.getCompanyId();

        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String location = req.getParameter("location");
        String salaryMinStr = req.getParameter("salaryMin");
        if (salaryMinStr == null) salaryMinStr = req.getParameter("salary_min");
        String salaryMaxStr = req.getParameter("salaryMax");
        if (salaryMaxStr == null) salaryMaxStr = req.getParameter("salary_max");

        if (title == null || title.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Missing title")));
            return;
        }

        double salaryMin = (salaryMinStr != null && !salaryMinStr.isBlank()) ? Double.parseDouble(salaryMinStr) : 0.0;
        double salaryMax = (salaryMaxStr != null && !salaryMaxStr.isBlank()) ? Double.parseDouble(salaryMaxStr) : 0.0;

        Job job = new Job();
        job.setCompanyId(companyId);
        job.setTitle(title);
        job.setDescription(description);
        job.setLocation(location);
        job.setSalaryMin(salaryMin);
        job.setSalaryMax(salaryMax);

        Job created = jobDAO.createJob(job);
        if (created == null) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Failed to create job")));
            return;
        }

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write(gson.toJson(Map.of("success", true, "job", created)));
    }

    private void handleEditJob(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("company") == null) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Company authentication required")));
            return;
        }
        Company company = (Company) session.getAttribute("company");
        int companyId = company.getCompanyId();

        String idStr = req.getParameter("id");
        if (idStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Missing job id")));
            return;
        }
        int id = Integer.parseInt(idStr);

        Job job = jobDAO.getJobById(id);
        if (job == null || job.getCompanyId() != companyId) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Not authorized")));
            return;
        }

        job.setTitle(req.getParameter("title"));
        job.setDescription(req.getParameter("description"));
        job.setLocation(req.getParameter("location"));

        String salaryMinStr = req.getParameter("salaryMin");
        if (salaryMinStr == null) salaryMinStr = req.getParameter("salary_min");
        String salaryMaxStr = req.getParameter("salaryMax");
        if (salaryMaxStr == null) salaryMaxStr = req.getParameter("salary_max");

        if (salaryMinStr != null && !salaryMinStr.isBlank()) {
            job.setSalaryMin(Double.parseDouble(salaryMinStr));
        }
        if (salaryMaxStr != null && !salaryMaxStr.isBlank()) {
            job.setSalaryMax(Double.parseDouble(salaryMaxStr));
        }

        boolean ok = jobDAO.updateJob(job);
        resp.setStatus(ok ? HttpServletResponse.SC_OK : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        resp.getWriter().write(gson.toJson(Map.of("success", ok)));
    }

    private void handleDeleteJob(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("company") == null) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Company authentication required")));
            return;
        }
        Company company = (Company) session.getAttribute("company");
        int companyId = company.getCompanyId();

        String idStr = req.getParameter("id");
        if (idStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Missing job id")));
            return;
        }
        int id = Integer.parseInt(idStr);

        boolean ok = jobDAO.deleteJob(id, companyId);
        resp.setStatus(ok ? HttpServletResponse.SC_OK : HttpServletResponse.SC_FORBIDDEN);
        resp.getWriter().write(gson.toJson(Map.of("success", ok)));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            HttpSession session = req.getSession(false);
            String companyIdParam = req.getParameter("companyId");
            List<Job> jobs;
            if (session != null && session.getAttribute("company") != null && companyIdParam == null) {
                Company company = (Company) session.getAttribute("company");
                int companyId = company.getCompanyId();
                jobs = jobDAO.getJobsByCompanyId(companyId);
            } else if (companyIdParam != null) {
                int companyId = Integer.parseInt(companyIdParam);
                jobs = jobDAO.getJobsByCompanyId(companyId);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Missing companyId or login required")));
                return;
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(jobs));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Internal server error: " + e.getMessage())));
        }
    }
}
