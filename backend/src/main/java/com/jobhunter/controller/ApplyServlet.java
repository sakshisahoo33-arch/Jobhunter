package com.jobhunter.controller;

import com.google.gson.Gson;
import com.jobhunter.dao.ApplicationDAO;
import com.jobhunter.model.Application;
import com.jobhunter.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebServlet(name = "ApplyServlet", urlPatterns = {"/apply"})
public class ApplyServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "User authentication required")));
            return;
        }

        try {
            User user = (User) session.getAttribute("user");
            int userId = user.getUserId();

            String jobIdStr = req.getParameter("jobId");
            if (jobIdStr == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Missing jobId")));
                return;
            }

            int jobId = Integer.parseInt(jobIdStr);

            if (applicationDAO.hasAlreadyApplied(userId, jobId)) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Already applied")));
                return;
            }

            Application a = new Application();
            a.setUserId(userId);
            a.setJobId(jobId);
            a.setStatus("Pending");

            Application created = applicationDAO.applyJob(a);
            if (created == null) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Failed to apply")));
                return;
            }

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(Map.of("success", true, "application", created)));
        } catch (NumberFormatException nfe) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Invalid numeric parameter")));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Internal server error: " + e.getMessage())));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "User authentication required")));
            return;
        }

        try {
            User user = (User) session.getAttribute("user");
            int userId = user.getUserId();
            var list = applicationDAO.getApplicationsByUser(userId);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(list));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Internal server error: " + e.getMessage())));
        }
    }
}
