package com.dinesh.ai_job_platform.service;

import com.dinesh.ai_job_platform.model.Job;
import com.dinesh.ai_job_platform.model.JobSkill;
import com.dinesh.ai_job_platform.model.Resume;
import com.dinesh.ai_job_platform.model.Skill;
import com.dinesh.ai_job_platform.repository.JobRepository;
import com.dinesh.ai_job_platform.repository.ResumeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JobMatchingService {

    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;

    public JobMatchingService(
            ResumeRepository resumeRepository,
            JobRepository jobRepository) {

        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
    }

    public List<Job> matchJobs(Long resumeId) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        Set<String> candidateSkills = resume.getSkills()
                .stream()
                .map(Skill::getName)
                .collect(Collectors.toSet());

        List<Job> jobs = jobRepository.findAll();

        List<Job> matchedJobs = new ArrayList<>();

        for (Job job : jobs) {

            for (JobSkill jobSkill : job.getRequiredSkills()) {

                if (candidateSkills.contains(jobSkill.getSkillName())) {
                    matchedJobs.add(job);
                    break;
                }
            }
        }

        return matchedJobs;
    }
}