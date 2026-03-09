package com.dinesh.ai_job_platform.service;

import com.dinesh.ai_job_platform.model.Resume;
import com.dinesh.ai_job_platform.model.Skill;
import com.dinesh.ai_job_platform.repository.ResumeRepository;
import com.dinesh.ai_job_platform.repository.SkillRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SkillExtractionService {

    private final ResumeRepository resumeRepository;
    private final SkillRepository skillRepository;
    private final AiService aiService;
    private final EmbeddingService embeddingService;

    public SkillExtractionService(
            ResumeRepository resumeRepository,
            SkillRepository skillRepository,
            AiService aiService,
            EmbeddingService embeddingService) {

        this.resumeRepository = resumeRepository;
        this.skillRepository = skillRepository;
        this.aiService = aiService;
        this.embeddingService = embeddingService;
    }

    @Async
    public void extractSkills(Long resumeId) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        String aiResponse = aiService.extractSkills(resume.getRawText());

        String[] skills = aiResponse.split(",");

        for (String skill : skills) {
            float[] embedding = embeddingService.generateEmbedding(skill.trim());

            Skill skillEntity = new Skill();
            skillEntity.setName(skill.trim());
            skillEntity.setResume(resume);

            skillRepository.save(skillEntity);
        }
    }
}