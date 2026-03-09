package com.dinesh.ai_job_platform.controller;

import com.dinesh.ai_job_platform.model.Skill;
import com.dinesh.ai_job_platform.service.VectorSearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final VectorSearchService vectorSearchService;

    public SearchController(VectorSearchService vectorSearchService) {
        this.vectorSearchService = vectorSearchService;
    }

    @GetMapping("/skills")
    public List<Skill> searchSkills(@RequestParam String query) {

        return vectorSearchService.searchSkills(query);
    }
}
