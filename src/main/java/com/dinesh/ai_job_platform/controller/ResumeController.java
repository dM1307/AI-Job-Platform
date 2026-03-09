package com.dinesh.ai_job_platform.controller;

import com.dinesh.ai_job_platform.dto.ResumeRequest;
import com.dinesh.ai_job_platform.dto.ResumeResponse;
import com.dinesh.ai_job_platform.model.Job;
import com.dinesh.ai_job_platform.service.JobMatchingService;
import com.dinesh.ai_job_platform.service.ResumeService;
import com.dinesh.ai_job_platform.service.SkillExtractionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;
    private final JobMatchingService jobMatchingService;

    public ResumeController(
            ResumeService resumeService,
            JobMatchingService jobMatchingService) {

        this.resumeService = resumeService;
        this.jobMatchingService = jobMatchingService;
    }

    @PostMapping
    public ResumeResponse createResume(@RequestBody ResumeRequest request) {
        return resumeService.createResume(request);
    }

    @GetMapping
    public List<ResumeResponse> getAllResumes() {
        System.out.println("Controller hit");
        return resumeService.getAllResumes();
    }

    @GetMapping("/{id}")
    public ResumeResponse getResume(@PathVariable Long id) {
        return resumeService.getResume(id);
    }

    @GetMapping("/{id}/match-jobs")
    public List<Job> matchJobs(@PathVariable Long id) {
        return jobMatchingService.matchJobs(id);
    }

    @DeleteMapping("/{id}")
    public String deleteResume(@PathVariable Long id) {

        resumeService.deleteResume(id);

        return "Resume deleted successfully";
    }

    @DeleteMapping
    public String deleteAllResumes() {

        resumeService.deleteAllResumes();

        return "All resumes deleted";
    }
}