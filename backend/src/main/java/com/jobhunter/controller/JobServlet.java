package com.jobhunter.controller;

import com.google.gson.Gson;
import com.jobhunter.dao.JobDAO;
import com.jobhunter.model.Job;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "JobServlet", urlPatterns = {"/jobs"})
public class JobServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final JobDAO jobDAO = new JobDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String q = req.getParameter("q");
        try {
            List<Job> results;
            if (q != null && !q.isBlank()) {
                results = jobDAO.searchJobs(q.trim());
            } else {
                results = jobDAO.getAllJobs();
            }
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(results));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of("error", e.getMessage())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        var session = req.getSession(false);
        if (session == null || session.getAttribute("company") == null) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(gson.toJson(Map.of("error", "Company authentication required")));
            return;
        }

        try {
            var company = session.getAttribute("company");
            // parse parameters
            String title = req.getParameter("title");
            String description = req.getParameter("description");
            String location = req.getParameter("location");
            String salary = req.getParameter("salary");

            if (title == null || title.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(Map.of("error", "Missing title")));
                return;
            }

            Job job = new Job();
            // company model likely has getId method
            int companyId = (int) company.getClass().getMethod("getId").invoke(company);
            job.setCompanyId(companyId);
            job.setTitle(title);
            job.setDescription(description);
            job.setLocation(location);
            job.setSalary(salary);

            Job created = jobDAO.createJob(job);
            if (created == null) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(Map.of("error", "Failed to create job")));
                return;
            }

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(created));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of("error", e.getMessage())));
        }
    }
}
