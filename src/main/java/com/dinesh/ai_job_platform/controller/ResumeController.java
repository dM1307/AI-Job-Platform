package com.dinesh.ai_job_platform.controller;

import com.dinesh.ai_job_platform.dto.ResumeRequest;
import com.dinesh.ai_job_platform.dto.ResumeResponse;
import com.dinesh.ai_job_platform.model.Job;
import com.dinesh.ai_job_platform.service.JobMatchingService;
import com.dinesh.ai_job_platform.service.ResumeParsingService;
import com.dinesh.ai_job_platform.service.ResumeService;
import com.dinesh.ai_job_platform.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;
    private final JobMatchingService jobMatchingService;
    private final SkillService skillService;
    private final ResumeParsingService resumeParsingService;

    public ResumeController(
            ResumeService resumeService,
            JobMatchingService jobMatchingService,
            SkillService skillService,
            ResumeParsingService resumeParsingService) {

        this.resumeService = resumeService;
        this.jobMatchingService = jobMatchingService;
        this.skillService = skillService;
        this.resumeParsingService = resumeParsingService;
    }

    @PostMapping
    public ResumeResponse createResume(@Valid @RequestBody ResumeRequest request) {
        return resumeService.createResume(request);
    }

    @PostMapping("/upload")
    public ResumeResponse uploadResume(@RequestParam("file") MultipartFile file) {

        String text = resumeParsingService.extractText(file);

        ResumeRequest request = new ResumeRequest();
        request.setCandidateName(file.getOriginalFilename() == null ? "uploaded-resume" : file.getOriginalFilename());
        request.setEmail("uploaded-" + UUID.randomUUID() + "@example.com");
        request.setRawText(text);

        return resumeService.createResume(request);
    }

    @GetMapping
    public List<ResumeResponse> getAllResumes() {
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
