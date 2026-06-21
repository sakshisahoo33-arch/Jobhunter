package com.jobhunter.controller;

import com.google.gson.Gson;
import com.jobhunter.dao.ResumeDAO;
import com.jobhunter.model.Resume;
import com.jobhunter.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@WebServlet(name = "ResumeUploadServlet", urlPatterns = {"/resume/upload"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 5 * 1024 * 1024, // 5MB
        maxRequestSize = 10 * 1024 * 1024)
public class ResumeUploadServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final ResumeDAO resumeDAO = new ResumeDAO();

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
            User user = (User) session.getAttribute("user");
            int userId = user.getUserId();

            Part filePart = req.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(Map.of("error", "Missing file")));
                return;
            }

            String submitted = filePart.getSubmittedFileName();
            String uploadDir = getServletContext().getRealPath("/resumes");
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists() && !uploadDirFile.mkdirs()) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(Map.of("error", "Unable to create upload directory")));
                return;
            }

            // Generate a safe filename
            String safeName = System.currentTimeMillis() + "_" + submitted.replaceAll("[^a-zA-Z0-9._-]", "_");
            File out = new File(uploadDirFile, safeName);
            try (InputStream in = filePart.getInputStream(); FileOutputStream fos = new FileOutputStream(out)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
            }

            Resume r = new Resume();
            r.setUserId(userId);
            r.setFileName(submitted);
            // store a web-accessible path if desired
            String path = "/resumes/" + out.getName();
            r.setFilePath(path);

            Resume created = resumeDAO.uploadResume(r);
            if (created == null) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write(gson.toJson(Map.of("error", "Failed to persist resume")));
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
