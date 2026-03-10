package com.dinesh.ai_job_platform.service;

import com.dinesh.ai_job_platform.dto.JobRequest;
import com.dinesh.ai_job_platform.model.Job;
import com.dinesh.ai_job_platform.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private EmbeddingService embeddingService;

    @InjectMocks
    private JobService jobService;

    @Test
    void createJob_shouldSaveAndReturnJob() {
        JobRequest request = new JobRequest();
        request.setTitle("Backend Engineer");
        request.setDescription("Spring Boot + PostgreSQL");

        Job saved = new Job();
        saved.setTitle("Backend Engineer");
        saved.setDescription("Spring Boot + PostgreSQL");

        given(embeddingService.generateEmbedding(any())).willReturn(new float[]{0.1f, 0.2f});
        given(jobRepository.save(any(Job.class))).willReturn(saved);

        Job result = jobService.createJob(request);

        assertThat(result.getTitle()).isEqualTo("Backend Engineer");
        assertThat(result.getDescription()).isEqualTo("Spring Boot + PostgreSQL");
    }

    @Test
    void getAllJobs_shouldReturnRepositoryValues() {
        Job job = new Job();
        job.setTitle("Data Engineer");

        given(jobRepository.findAll()).willReturn(List.of(job));

        assertThat(jobService.getAllJobs()).hasSize(1);
    }
}
