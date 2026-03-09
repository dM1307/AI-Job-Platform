package com.dinesh.ai_job_platform.service;

import com.dinesh.ai_job_platform.repository.SkillRepository;
import org.springframework.stereotype.Service;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public void deleteAllSkills() {
        skillRepository.deleteAll();
    }

    public void deleteSkillsByResume(Long resumeId) {
        skillRepository.deleteByResumeId(resumeId);
    }
}