package com.dinesh.ai_job_platform.controller;

import com.dinesh.ai_job_platform.dto.JobRequest;
import com.dinesh.ai_job_platform.model.Job;
import com.dinesh.ai_job_platform.service.JobService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public Job createJob(@RequestBody JobRequest request) {

        return jobService.createJob(request);
    }
}