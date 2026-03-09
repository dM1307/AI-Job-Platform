package com.dinesh.ai_job_platform.repository;

import com.dinesh.ai_job_platform.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    @Query(
            value = "SELECT * FROM skills ORDER BY embedding <-> CAST(:embedding AS vector) LIMIT 5",
            nativeQuery = true
    )
    List<Skill> searchSimilarSkills(float[] embedding);

    void deleteByResume_Id(Long resumeId);
}
