package com.dinesh.ai_job_platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ResumeRequest {

    @NotBlank(message = "Candidate name is required")
    private String candidateName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Resume content is required")
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
