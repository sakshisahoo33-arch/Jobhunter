package com.jobhunter.model;

public class Company {
    private int companyId;
    private String companyName;
    private String email;
    private String password;
    private String industry;

    public Company() {
    }

    public Company(int companyId, String companyName, String email, String password, String industry) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.email = email;
        this.password = password;
        this.industry = industry;
    }

    public Company(String companyName, String email, String password, String industry) {
        this.companyName = companyName;
        this.email = email;
        this.password = password;
        this.industry = industry;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    @Override
    public String toString() {
        return "Company{" +
                "companyId=" + companyId +
                ", companyName='" + companyName + '\'' +
                ", email='" + email + '\'' +
                ", industry='" + industry + '\'' +
                '}';
    }
}
