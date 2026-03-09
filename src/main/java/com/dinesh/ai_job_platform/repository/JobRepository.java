package com.dinesh.ai_job_platform.repository;

import com.dinesh.ai_job_platform.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    @Modifying
    @Transactional
    @Query(
            value = """
        SELECT * FROM job
        ORDER BY embedding <-> CAST(:embedding AS vector)
        LIMIT 5
        """,
            nativeQuery = true
    )
    List<Job> findSimilarJobs(float[] embedding);
}