package com.dinesh.ai_job_platform.controller;

import com.dinesh.ai_job_platform.dto.ResumeRequest;
import com.dinesh.ai_job_platform.dto.ResumeResponse;
import com.dinesh.ai_job_platform.model.Job;
import com.dinesh.ai_job_platform.service.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;
    private final JobMatchingService jobMatchingService;
    private final SkillService skillService;
    private final ResumeParsingService resumeParsingService;
    private final ResumeProcessingService resumeProcessingService;

    public ResumeController(
            ResumeService resumeService,
            JobMatchingService jobMatchingService,
            SkillService skillService,
            ResumeParsingService resumeParsingService,
            ResumeProcessingService resumeProcessingService) {

        this.resumeService = resumeService;
        this.jobMatchingService = jobMatchingService;
        this.skillService = skillService;
        this.resumeParsingService = resumeParsingService;
        this.resumeProcessingService = resumeProcessingService;
    }

    @PostMapping
    public ResumeResponse createResume(@RequestBody ResumeRequest request) {
        return resumeService.createResume(request);
    }

    @PostMapping("/upload")
    public ResumeResponse uploadResume(@RequestParam("file") MultipartFile file) {

        String text = resumeParsingService.extractText(file);

        ResumeRequest request = new ResumeRequest();
        request.setCandidateName(file.getOriginalFilename());
        request.setEmail("unknown@example.com");
        request.setRawText(text);

        ResumeResponse resume = resumeService.createResume(request);

        resumeProcessingService.processResume(resume.getId());

        return resume;
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
        skillService.deleteSkillsByResume(id);

        return "Resume and skill deleted successfully";
    }

    @DeleteMapping
    public String deleteAllResumes() {

        resumeService.deleteAllResumes();
        skillService.deleteAllSkills();

        return "All resumes deleted";
    }
}