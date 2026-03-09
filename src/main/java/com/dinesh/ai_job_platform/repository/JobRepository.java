package com.dinesh.ai_job_platform.repository;

import com.dinesh.ai_job_platform.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
}