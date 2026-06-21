package com.jobhunter.model;

public class Application {
    private int applicationId;
    private int userId;
    private int jobId;
    private String status;

    public Application() {
    }

    public Application(int applicationId, int userId, int jobId, String status) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.jobId = jobId;
        this.status = status;
    }

    public Application(int userId, int jobId, String status) {
        this.userId = userId;
        this.jobId = jobId;
        this.status = status;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Application{" +
                "applicationId=" + applicationId +
                ", userId=" + userId +
                ", jobId=" + jobId +
                ", status='" + status + '\'' +
                '}';
    }
}
