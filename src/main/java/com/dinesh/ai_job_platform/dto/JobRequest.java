package com.dinesh.ai_job_platform.dto;

public class JobRequest {

    private String title;
    private String description;

    public JobRequest() {}

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}