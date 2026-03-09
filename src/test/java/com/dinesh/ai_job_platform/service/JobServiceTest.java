package com.dinesh.ai_job_platform.service;

import com.neuralhire.platform.dto.JobRequest;
import com.neuralhire.platform.dto.JobResponse;
import com.neuralhire.platform.exception.JobBadRequestException;
import com.neuralhire.platform.exception.ResourceNotFoundException;
import com.neuralhire.platform.model.Job;
import com.neuralhire.platform.repository.JobRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JobService — Unit Tests")
class JobServiceTest {

    @Mock  private JobRepository jobRepository;
    @InjectMocks private JobService jobService;

    @Nested
    @DisplayName("createJob()")
    class CreateJob {

        @Test
        @DisplayName("saves job and returns response with ID")
        void createsJob() {
            var req = new JobRequest("ML Engineer", "NeuralCorp",
                    "Remote", "Build AI pipelines", List.of("Python","PyTorch"), "$140k");
            var saved = Job.builder().id(10L).title("ML Engineer")
                    .company("NeuralCorp").location("Remote")
                    .requiredSkills(List.of("Python","PyTorch")).build();
            given(jobRepository.save(any(Job.class))).willReturn(saved);

            JobResponse result = jobService.createJob(req);

            assertThat(result.getId()).isEqualTo(10L);
            assertThat(result.getTitle()).isEqualTo("ML Engineer");
            assertThat(result.getRequiredSkills()).containsExactly("Python","PyTorch");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("rejects blank title")
        void rejectsBlankTitle(String title) {
            var req = new JobRequest(title, "Corp", null, null, null, null);
            assertThatThrownBy(() -> jobService.createJob(req))
                    .isInstanceOf(JobBadRequestException.class);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("rejects blank company")
        void rejectsBlankCompany(String company) {
            var req = new JobRequest("Engineer", company, null, null, null, null);
            assertThatThrownBy(() -> jobService.createJob(req))
                    .isInstanceOf(JobBadRequestException.class);
        }
    }

    @Nested
    @DisplayName("getAllJobs()")
    class GetAll {

        @Test
        @DisplayName("returns paginated job list")
        void returnsAll() {
            var jobs = List.of(
                    Job.builder().id(1L).title("Dev").company("A").requiredSkills(List.of()).build(),
                    Job.builder().id(2L).title("Ops").company("B").requiredSkills(List.of()).build()
            );
            given(jobRepository.findAll()).willReturn(jobs);
            assertThat(jobService.getAllJobs()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("getJobById()")
    class GetById {

        @Test
        @DisplayName("returns job when found")
        void found() {
            var job = Job.builder().id(7L).title("Dev").company("Co").requiredSkills(List.of()).build();
            given(jobRepository.findById(7L)).willReturn(Optional.of(job));
            assertThat(jobService.getJobById(7L).getId()).isEqualTo(7L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when missing")
        void notFound() {
            given(jobRepository.findById(0L)).willReturn(Optional.empty());
            assertThatThrownBy(() -> jobService.getJobById(0L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteJob()")
    class Delete {

        @Test
        void deletesExisting() {
            given(jobRepository.existsById(5L)).willReturn(true);
            assertThatCode(() -> jobService.deleteJob(5L)).doesNotThrowAnyException();
            then(jobRepository).should().deleteById(5L);
        }

        @Test
        void throwsWhenNotFound() {
            given(jobRepository.existsById(5L)).willReturn(false);
            assertThatThrownBy(() -> jobService.deleteJob(5L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
