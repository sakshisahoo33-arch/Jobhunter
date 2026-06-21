package com.jobhunter.model;

public class SavedJob {
    private int savedId;
    private int userId;
    private int jobId;

    public SavedJob() {
    }

    public SavedJob(int savedId, int userId, int jobId) {
        this.savedId = savedId;
        this.userId = userId;
        this.jobId = jobId;
    }

    public SavedJob(int userId, int jobId) {
        this.userId = userId;
        this.jobId = jobId;
    }

    public int getSavedId() {
        return savedId;
    }

    public void setSavedId(int savedId) {
        this.savedId = savedId;
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
                "savedId=" + savedId +
                ", userId=" + userId +
                ", jobId=" + jobId +
                '}';
    }
}
