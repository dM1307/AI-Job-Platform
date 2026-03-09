package com.dinesh.ai_job_platform.controller;

import com.dinesh.ai_job_platform.service.SkillService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @DeleteMapping("/all")
    public String deleteAllSkills() {
        skillService.deleteAllSkills();
        return "All skills deleted";
    }

    @DeleteMapping("/resume/{resumeId}")
    public String deleteSkillsByResume(@PathVariable Long resumeId) {
        skillService.deleteSkillsByResume(resumeId);
        return "Skills deleted for resume " + resumeId;
    }
}