package com.jobhunter.controller;

import com.google.gson.Gson;
import com.jobhunter.dao.CompanyDAO;
import com.jobhunter.dao.UserDAO;
import com.jobhunter.model.Company;
import com.jobhunter.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final UserDAO userDAO = new UserDAO();
    private final CompanyDAO companyDAO = new CompanyDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String role = req.getParameter("role");

        if (email == null || password == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(Map.of("error", "Missing credentials")));
            return;
        }

        try {
            HttpSession session = req.getSession(true);
            if ("company".equalsIgnoreCase(role)) {
                Company c = companyDAO.loginCompany(email, password);
                if (c == null) {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    resp.getWriter().write(gson.toJson(Map.of("error", "Invalid credentials")));
                    return;
                }
                session.setAttribute("company", c);
                session.setAttribute("role", "company");
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(c));
                return;
            }

            // try user login by default
            User u = userDAO.loginUser(email, password);
            if (u == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(gson.toJson(Map.of("error", "Invalid credentials")));
                return;
            }
            session.setAttribute("user", u);
            session.setAttribute("role", "user");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(u));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of("error", "Internal server error", "message", e.getMessage())));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("role") == null) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        Object principal = session.getAttribute("user");
        if (principal == null) principal = session.getAttribute("company");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(gson.toJson(principal));
    }
}
