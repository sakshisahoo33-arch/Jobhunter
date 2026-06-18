package com.jobhunter.controller;

import com.google.gson.Gson;
import com.jobhunter.dao.ApplicationDAO;
import com.jobhunter.model.Application;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebServlet(name = "ApplyJobServlet", urlPatterns = {"/apply"})
public class ApplyJobServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(gson.toJson(Map.of("error", "User authentication required")));
            return;
        }

        try {
            Object user = session.getAttribute("user");
            int userId = (int) user.getClass().getMethod("getId").invoke(user);

            String jobIdStr = req.getParameter("jobId");
            String resumeIdStr = req.getParameter("resumeId");
            String coverLetter = req.getParameter("coverLetter");

            if (jobIdStr == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(Map.of("error", "Missing jobId")));
                return;
            }

            int jobId = Integer.parseInt(jobIdStr);
            int resumeId = (resumeIdStr != null) ? Integer.parseInt(resumeIdStr) : 0;

            if (applicationDAO.hasAlreadyApplied(userId, jobId)) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write(gson.toJson(Map.of("error", "Already applied")));
                return;
            }

            Application a = new Application();
            a.setUserId(userId);
            a.setJobId(jobId);
            a.setResumeId(resumeId);
            a.setCoverLetter(coverLetter);

            Application created = applicationDAO.applyJob(a);
            if (created == null) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(Map.of("error", "Failed to apply")));
                return;
            }

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(created));
        } catch (NumberFormatException nfe) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(Map.of("error", "Invalid numeric parameter")));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of("error", e.getMessage())));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(gson.toJson(Map.of("error", "User authentication required")));
            return;
        }

        try {
            Object user = session.getAttribute("user");
            int userId = (int) user.getClass().getMethod("getId").invoke(user);
            var list = applicationDAO.getApplicationsByUser(userId);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(list));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of("error", e.getMessage())));
        }
    }
}
