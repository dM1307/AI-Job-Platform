package com.dinesh.ai_job_platform.service;

import com.dinesh.ai_job_platform.dto.ResumeRequest;
import com.dinesh.ai_job_platform.dto.ResumeResponse;
import com.dinesh.ai_job_platform.exception.ResourceNotFoundException;
import com.dinesh.ai_job_platform.model.Resume;
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
class ResumeServiceTest {

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private ResumeProcessingService processingService;

    @InjectMocks
    private ResumeService resumeService;

    @Test
    void createResume_shouldTrimAndNormalizeEmail() {
        ResumeRequest request = new ResumeRequest();
        request.setCandidateName("  Alice  ");
        request.setEmail(" Alice@Example.COM ");
        request.setRawText("  Java developer  ");

        Resume saved = new Resume("Alice", "alice@example.com", "Java developer");
        given(resumeRepository.save(any(Resume.class))).willReturn(saved);

        ResumeResponse response = resumeService.createResume(request);

        assertThat(response.getCandidateName()).isEqualTo("Alice");
        assertThat(response.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void getResume_shouldThrowWhenMissing() {
        given(resumeRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> resumeService.getResume(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Resume not found");
    }

    @Test
    void getAllResumes_shouldMapList() {
        Resume resume = new Resume("Bob", "bob@example.com", "text");
        given(resumeRepository.findAllWithSkills()).willReturn(List.of(resume));

        assertThat(resumeService.getAllResumes()).hasSize(1);
    }
}
