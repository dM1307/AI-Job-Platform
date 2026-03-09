package com.dinesh.ai_job_platform.service;

import com.neuralhire.platform.exception.ResourceNotFoundException;
import com.neuralhire.platform.model.Job;
import com.neuralhire.platform.model.Resume;
import com.neuralhire.platform.repository.JobRepository;
import com.neuralhire.platform.repository.ResumeRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

/**
 * Unit tests for MatchService.
 * Tests semantic skill-overlap scoring and edge cases.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MatchService — Unit Tests")
class MatchServiceTest {

    @Mock  private ResumeRepository resumeRepository;
    @Mock  private JobRepository jobRepository;
    @InjectMocks private MatchService matchService;

    private Resume buildResume(Long id, String... skills) {
        return Resume.builder().id(id).name("Test").email("t@t.com")
                .skills(Arrays.asList(skills)).build();
    }
    private Job buildJob(Long id, String... skills) {
        return Job.builder().id(id).title("Role").company("Co")
                .requiredSkills(Arrays.asList(skills)).build();
    }

    @Nested
    @DisplayName("match()")
    class Match {

        @Test
        @DisplayName("returns 1.0 when all skills match")
        void perfectMatch() {
            given(resumeRepository.findById(1L)).willReturn(Optional.of(buildResume(1L,"Java","Python","Docker")));
            given(jobRepository.findById(1L)).willReturn(Optional.of(buildJob(1L,"Java","Python","Docker")));
            double score = matchService.match(1L, 1L);
            assertThat(score).isEqualTo(1.0);
        }

        @Test
        @DisplayName("returns 0.0 when no skills overlap")
        void noMatch() {
            given(resumeRepository.findById(1L)).willReturn(Optional.of(buildResume(1L,"Java")));
            given(jobRepository.findById(1L)).willReturn(Optional.of(buildJob(1L,"Rust","Go")));
            double score = matchService.match(1L, 1L);
            assertThat(score).isEqualTo(0.0);
        }

        @Test
        @DisplayName("partial overlap returns score between 0 and 1")
        void partialMatch() {
            given(resumeRepository.findById(1L)).willReturn(Optional.of(buildResume(1L,"Java","Python","Go")));
            given(jobRepository.findById(1L)).willReturn(Optional.of(buildJob(1L,"Java","Python","Rust")));
            double score = matchService.match(1L, 1L);
            assertThat(score).isBetween(0.0, 1.0).isGreaterThan(0.0);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when resume not found")
        void missingResume() {
            given(resumeRepository.findById(99L)).willReturn(Optional.empty());
            assertThatThrownBy(() -> matchService.match(99L, 1L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when job not found")
        void missingJob() {
            given(resumeRepository.findById(1L)).willReturn(Optional.of(buildResume(1L,"Java")));
            given(jobRepository.findById(99L)).willReturn(Optional.empty());
            assertThatThrownBy(() -> matchService.match(1L, 99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("handles empty resume skills gracefully")
        void emptyResumeSkills() {
            given(resumeRepository.findById(1L)).willReturn(Optional.of(buildResume(1L)));
            given(jobRepository.findById(1L)).willReturn(Optional.of(buildJob(1L,"Java","Python")));
            double score = matchService.match(1L, 1L);
            assertThat(score).isEqualTo(0.0);
        }

        @Test
        @DisplayName("handles empty job skills gracefully")
        void emptyJobSkills() {
            given(resumeRepository.findById(1L)).willReturn(Optional.of(buildResume(1L,"Java","Python")));
            given(jobRepository.findById(1L)).willReturn(Optional.of(buildJob(1L)));
            double score = matchService.match(1L, 1L);
            assertThat(score).isEqualTo(0.0);
        }

        @Test
        @DisplayName("is case-insensitive for skill matching")
        void caseInsensitiveMatch() {
            given(resumeRepository.findById(1L)).willReturn(Optional.of(buildResume(1L,"java","PYTHON")));
            given(jobRepository.findById(1L)).willReturn(Optional.of(buildJob(1L,"Java","Python")));
            double score = matchService.match(1L, 1L);
            assertThat(score).isEqualTo(1.0);
        }
    }
}
