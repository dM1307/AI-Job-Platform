package com.dinesh.ai_job_platform.dto;

public class ResumeRequest {

    private String candidateName;
    private String email;
    private String rawText;

    public ResumeRequest() {}

    public String getCandidateName() {
        return candidateName;
    }

    public String getEmail() {
        return email;
    }

    public String getRawText() {
        return rawText;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }
}