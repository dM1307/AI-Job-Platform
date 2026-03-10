package com.dinesh.ai_job_platform.service;

import com.dinesh.ai_job_platform.model.Job;
import com.dinesh.ai_job_platform.model.Resume;
import com.dinesh.ai_job_platform.repository.JobRepository;
import com.dinesh.ai_job_platform.repository.ResumeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private EmbeddingService embeddingService;

    @InjectMocks
    private JobMatchingService jobMatchingService;

    @Test
    void matchJobs_shouldReturnSimilarJobs() {
        Resume resume = new Resume("Alice", "alice@example.com", "Spring Boot Java");
        Job job = new Job("Backend Engineer", "Java");

        given(resumeRepository.findById(1L)).willReturn(Optional.of(resume));
        given(embeddingService.generateEmbedding(any())).willReturn(new float[]{0.1f, 0.2f});
        given(jobRepository.findSimilarJobs(any(float[].class))).willReturn(List.of(job));

        List<Job> result = jobMatchingService.matchJobs(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Backend Engineer");
    }

    @Test
    void matchJobs_shouldThrowWhenResumeMissing() {
        given(resumeRepository.findById(2L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> jobMatchingService.matchJobs(2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Resume not found");
    }
}
