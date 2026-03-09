package com.dinesh.ai_job_platform.repository;

import com.dinesh.ai_job_platform.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    @Query("SELECT DISTINCT r FROM Resume r LEFT JOIN FETCH r.skills")
    List<Resume> findAllWithSkills();
}