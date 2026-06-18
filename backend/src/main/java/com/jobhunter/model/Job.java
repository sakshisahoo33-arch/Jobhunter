package com.jobhunter.model;

public class Job {
    private int id;
    private int companyId;
    private String title;
    private String description;
    private String location;
    private String salary;
    private String status;

    public Job() {
    }

    public Job(int id, int companyId, String title, String description, String location, String salary, String status) {
        this.id = id;
        this.companyId = companyId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.salary = salary;
        this.status = status;
    }

    public Job(int companyId, String title, String description, String location, String salary, String status) {
        this.companyId = companyId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.salary = salary;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", companyId=" + companyId +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", salary='" + salary + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
