package com.dinesh.ai_job_platform.service;

import com.dinesh.ai_job_platform.model.Resume;
import com.dinesh.ai_job_platform.model.Skill;
import com.dinesh.ai_job_platform.repository.ResumeRepository;
import com.dinesh.ai_job_platform.repository.SkillRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ResumeProcessingService {

    private final ResumeRepository resumeRepository;
    private final SkillRepository skillRepository;
    private final AiService aiService;
    private final EmbeddingService embeddingService;

    public ResumeProcessingService(
            ResumeRepository resumeRepository,
            SkillRepository skillRepository,
            AiService aiService,
            EmbeddingService embeddingService) {

        this.resumeRepository = resumeRepository;
        this.skillRepository = skillRepository;
        this.aiService = aiService;
        this.embeddingService = embeddingService;
    }

    public void processResume(Long resumeId) {

        System.out.println("Processing resume id: " + resumeId);

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow();

        System.out.println("Resume text: " + resume.getRawText());

        String extractedSkills = aiService.extractSkills(resume.getRawText());

        System.out.println("AI response: " + extractedSkills);

        if (extractedSkills == null || extractedSkills.isBlank()) {
            System.out.println("No skills extracted");
            return;
        }

        String[] skills = extractedSkills.split(",");

        for (String skillName : skills) {
            String cleanSkill = skillName.trim();

            System.out.println("Saving skill: " + skillName);

            Skill skill = new Skill();
            skill.setName(cleanSkill);
            skill.setResume(resume);

            float[] embedding = embeddingService.generateEmbedding(cleanSkill);
            skill.setEmbedding(embedding);

            skillRepository.save(skill);
        }
    }
}