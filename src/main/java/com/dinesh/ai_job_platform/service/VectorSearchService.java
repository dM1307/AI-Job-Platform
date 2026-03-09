package com.dinesh.ai_job_platform.service;

import com.dinesh.ai_job_platform.model.Skill;
import com.dinesh.ai_job_platform.repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VectorSearchService {

    private final SkillRepository skillRepository;
    private final EmbeddingService embeddingService;

    public VectorSearchService(
            SkillRepository skillRepository,
            EmbeddingService embeddingService) {

        this.skillRepository = skillRepository;
        this.embeddingService = embeddingService;
    }

    public List<Skill> searchSkills(String query) {

        float[] embedding = embeddingService.generateEmbedding(query);

        return skillRepository.searchSimilarSkills(embedding);
    }
}
