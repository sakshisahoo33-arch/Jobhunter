package com.jobhunter.model;

public class SavedJob {
    private int id;
    private int userId;
    private int jobId;

    public SavedJob() {
    }

    public SavedJob(int id, int userId, int jobId) {
        this.id = id;
        this.userId = userId;
        this.jobId = jobId;
    }

    public SavedJob(int userId, int jobId) {
        this.userId = userId;
        this.jobId = jobId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "SavedJob{" +
                "id=" + id +
                ", userId=" + userId +
                ", jobId=" + jobId +
                '}';
    }
}
