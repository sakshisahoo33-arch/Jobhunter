package com.jobhunter.controller;

import com.google.gson.Gson;
import com.jobhunter.dao.UserDAO;
import com.jobhunter.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            String fullName = req.getParameter("full_name");
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            String phone = req.getParameter("phone");

            if (fullName == null || email == null || password == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Missing required fields")));
                return;
            }

            if (userDAO.emailExists(email)) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Email already registered")));
                return;
            }

            User u = new User();
            u.setFullName(fullName);
            u.setEmail(email);
            u.setPassword(password);
            u.setPhone(phone);

            User created = userDAO.registerUser(u);
            if (created == null) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(Map.of("success", false, "message", "Failed to create user")));
                return;
            }

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(Map.of("success", true, "message", "User registered", "user", created)));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of(
                "success", false,
                "message", "Internal server error"
            )));
        }
    }
}
