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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final UserDAO userDAO = new UserDAO();
    private final CompanyDAO companyDAO = new CompanyDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String role = req.getParameter("role");
        if (role == null) role = "user";

        resp.setContentType("application/json");

        try {
            if ("company".equalsIgnoreCase(role)) {
                String name = req.getParameter("name");
                String email = req.getParameter("email");
                String password = req.getParameter("password");
                String phone = req.getParameter("phone");
                String description = req.getParameter("description");

                if (name == null || email == null || password == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(gson.toJson(Map.of("error", "Missing required fields for company")));
                    return;
                }

                if (companyDAO.emailExists(email)) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    resp.getWriter().write(gson.toJson(Map.of("error", "Email already registered")));
                    return;
                }

                Company c = new Company();
                c.setName(name);
                c.setEmail(email);
                c.setPassword(password);
                c.setPhone(phone);
                c.setDescription(description);

                Company created = companyDAO.registerCompany(c);
                if (created == null) {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write(gson.toJson(Map.of("error", "Failed to create company")));
                    return;
                }

                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(gson.toJson(created));
                return;
            }

            // Default: user registration
            String firstName = req.getParameter("first_name");
            String lastName = req.getParameter("last_name");
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            String phone = req.getParameter("phone");

            if (firstName == null || lastName == null || email == null || password == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(Map.of("error", "Missing required fields for user")));
                return;
            }

            if (new UserDAO().emailExists(email)) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write(gson.toJson(Map.of("error", "Email already registered")));
                return;
            }

            User u = new User();
            u.setFirstName(firstName);
            u.setLastName(lastName);
            u.setEmail(email);
            u.setPassword(password);
            u.setPhone(phone);

            User created = userDAO.registerUser(u);
            if (created == null) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(Map.of("error", "Failed to create user")));
                return;
            }

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(created));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> err = new HashMap<>();
            err.put("error", "Internal server error");
            err.put("message", e.getMessage());
            resp.getWriter().write(gson.toJson(err));
        }
    }
}
