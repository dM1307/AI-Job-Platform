package com.dinesh.ai_job_platform.service;

import com.dinesh.ai_job_platform.dto.ResumeRequest;
import com.dinesh.ai_job_platform.dto.ResumeResponse;
import com.dinesh.ai_job_platform.exception.ResourceNotFoundException;
import com.dinesh.ai_job_platform.model.Resume;
import com.dinesh.ai_job_platform.model.Skill;
import com.dinesh.ai_job_platform.repository.ResumeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ResumeProcessingService processingService;

    public ResumeService(
            ResumeRepository resumeRepository,
            ResumeProcessingService processingService) {

        this.resumeRepository = resumeRepository;
        this.processingService = processingService;
    }

    public ResumeResponse createResume(ResumeRequest request) {

        Resume resume = new Resume();
        resume.setCandidateName(request.getCandidateName());
        resume.setEmail(request.getEmail());
        resume.setRawText(request.getRawText());

        Resume saved = resumeRepository.save(resume);

        // 🚀 trigger async AI processing
        processingService.processResume(saved.getId());

        return mapToResponse(saved);
    }

    public ResumeResponse getResume(Long id) {

        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        resume.getSkills().size();

        return mapToResponse(resume);
    }

    public List<ResumeResponse> getAllResumes() {
        System.out.println("Service started");

        List<Resume> resumes = resumeRepository.findAllWithSkills();

        System.out.println("Resumes fetched: " + resumes.size());

        return resumes.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ResumeResponse mapToResponse(Resume resume) {

        ResumeResponse response = new ResumeResponse();

        response.setId(resume.getId());
        response.setCandidateName(resume.getCandidateName());
        response.setEmail(resume.getEmail());

        List<String> skills = resume.getSkills()
                .stream()
                .map(Skill::getName)
                .toList();

        System.out.println("Mapping resume: " + resume.getId());
        System.out.println("Skills: " + resume.getSkills().size());
        response.setSkills(skills);

        return response;
    }

    public void deleteResume(Long id) {

        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        resumeRepository.delete(resume);
    }

    public void deleteAllResumes() {
        resumeRepository.deleteAll();
    }
}