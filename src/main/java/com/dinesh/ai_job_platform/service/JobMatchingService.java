package com.dinesh.ai_job_platform.service;

import com.dinesh.ai_job_platform.model.Job;
import com.dinesh.ai_job_platform.model.Resume;
import com.dinesh.ai_job_platform.model.Skill;
import com.dinesh.ai_job_platform.repository.JobRepository;
import com.dinesh.ai_job_platform.repository.ResumeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobMatchingService {

    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final EmbeddingService embeddingService;

    public JobMatchingService(
            ResumeRepository resumeRepository,
            JobRepository jobRepository,
            EmbeddingService embeddingService) {

        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.embeddingService = embeddingService;
    }

    public List<Job> matchJobs(Long resumeId) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        String resumeText = resume.getRawText();

        String skillsText = resume.getSkills()
                .stream()
                .map(Skill::getName)
                .collect(Collectors.joining(" "));

        float[] resumeEmbedding =
                embeddingService.generateEmbedding(skillsText);

        return jobRepository.findSimilarJobs(resumeEmbedding);
    }
}