package com.dinesh.ai_job_platform.service;

import com.dinesh.ai_job_platform.exception.ResourceNotFoundException;
import com.dinesh.ai_job_platform.model.Resume;
import com.dinesh.ai_job_platform.model.Skill;
import com.dinesh.ai_job_platform.repository.ResumeRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

@Service
public class ResumeProcessingService {

    private final ResumeRepository resumeRepository;
    private final AiService aiService;
    private final EmbeddingService embeddingService;

    public ResumeProcessingService(
            ResumeRepository resumeRepository,
            AiService aiService,
            EmbeddingService embeddingService) {

        this.resumeRepository = resumeRepository;
        this.aiService = aiService;
        this.embeddingService = embeddingService;
    }

    @Async
    @Transactional
    public void processResume(Long resumeId) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        String extractedSkills = aiService.extractSkills(resume.getRawText());

        if (extractedSkills == null || extractedSkills.isBlank()) {
            return;
        }

        Set<String> uniqueSkills = new LinkedHashSet<>();
        for (String skillName : extractedSkills.split(",")) {
            String cleanSkill = skillName.trim();
            if (!cleanSkill.isBlank()) {
                uniqueSkills.add(cleanSkill);
            }
        }

        resume.getSkills().clear();

        for (String cleanSkill : uniqueSkills) {
            Skill skill = new Skill();
            skill.setName(cleanSkill.toLowerCase(Locale.ROOT));
            skill.setResume(resume);
            skill.setEmbedding(embeddingService.generateEmbedding(cleanSkill));
            resume.getSkills().add(skill);
        }

        resumeRepository.save(resume);
    }
}
