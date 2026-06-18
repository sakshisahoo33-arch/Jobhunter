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
}
