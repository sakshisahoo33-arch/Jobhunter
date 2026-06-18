package com.jobhunter.model;

public class Application {
    private int id;
    private int userId;
    private int jobId;
    private int resumeId;
    private String coverLetter;
    private String status;

    public Application() {
    }

    public Application(int id, int userId, int jobId, int resumeId, String coverLetter, String status) {
        this.id = id;
        this.userId = userId;
        this.jobId = jobId;
        this.resumeId = resumeId;
        this.coverLetter = coverLetter;
        this.status = status;
    }

    public Application(int userId, int jobId, int resumeId, String coverLetter, String status) {
        this.userId = userId;
        this.jobId = jobId;
        this.resumeId = resumeId;
        this.coverLetter = coverLetter;
        this.status = status;
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

    public int getResumeId() {
        return resumeId;
    }

    public void setResumeId(int resumeId) {
        this.resumeId = resumeId;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
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
                "id=" + id +
                ", userId=" + userId +
                ", jobId=" + jobId +
                ", resumeId=" + resumeId +
                ", status='" + status + '\'' +
                '}';
    }
}
