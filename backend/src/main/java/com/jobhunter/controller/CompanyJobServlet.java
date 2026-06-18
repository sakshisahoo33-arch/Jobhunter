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

@WebServlet(name = "CompanyJobServlet", urlPatterns = {"/company/jobs"})
public class CompanyJobServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final JobDAO jobDAO = new JobDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        var session = req.getSession(false);
        if (session == null || session.getAttribute("company") == null) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(gson.toJson(Map.of("error", "Company authentication required")));
            return;
        }

        try {
            Object company = session.getAttribute("company");
            int companyId = (int) company.getClass().getMethod("getId").invoke(company);
            List<Job> jobs = jobDAO.getJobsByCompanyId(companyId);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(jobs));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of("error", e.getMessage())));
        }
    }
}
