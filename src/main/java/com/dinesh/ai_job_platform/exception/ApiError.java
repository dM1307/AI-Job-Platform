package com.dinesh.ai_job_platform.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ApiError {

    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String path;
    private final List<String> details;

    public ApiError(int status, String error, String path, List<String> details) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.path = path;
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }

    public List<String> getDetails() {
        return details;
    }
}
