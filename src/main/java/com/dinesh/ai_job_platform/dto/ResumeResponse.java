package com.dinesh.ai_job_platform.dto;

import java.util.List;

public class ResumeResponse {

    private Long id;
    private String candidateName;
    private String email;
    private List<String> skills;

    public ResumeResponse() {}

    public Long getId() {
        return id;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
}