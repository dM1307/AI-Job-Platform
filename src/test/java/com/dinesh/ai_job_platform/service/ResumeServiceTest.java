package com.dinesh.ai_job_platform.service;

import com.neuralhire.platform.dto.ResumeRequest;
import com.neuralhire.platform.dto.ResumeResponse;
import com.neuralhire.platform.exception.ResourceNotFoundException;
import com.neuralhire.platform.exception.ResumeBadRequestException;
import com.neuralhire.platform.model.Resume;
import com.neuralhire.platform.repository.ResumeRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * Unit tests for ResumeService.
 *
 * Uses Mockito for repository isolation. No Spring context needed.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ResumeService — Unit Tests")
class ResumeServiceTest {

    @Mock  private ResumeRepository resumeRepository;
    @InjectMocks private ResumeService resumeService;

    // ── createResume ─────────────────────────────────────────────

    @Nested
    @DisplayName("createResume()")
    class CreateResume {

        @Test
        @DisplayName("saves and returns response with generated ID")
        void savesResume() {
            var req = new ResumeRequest("Alice Chen", "alice@dev.io",
                    "+1-555-0000", "ML engineer summary", List.of("Java","Python"));
            var saved = Resume.builder()
                    .id(1L).name("Alice Chen").email("alice@dev.io")
                    .phone("+1-555-0000").summary("ML engineer summary")
                    .skills(List.of("Java","Python")).build();
            given(resumeRepository.save(any(Resume.class))).willReturn(saved);

            ResumeResponse result = resumeService.createResume(req);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("Alice Chen");
            assertThat(result.getSkills()).containsExactly("Java","Python");
            then(resumeRepository).should(times(1)).save(any(Resume.class));
        }

        @ParameterizedTest(name = "name=''{0}'' should throw")
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("rejects blank name")
        void rejectsBlankName(String name) {
            var req = new ResumeRequest(name, "alice@dev.io", null, null, null);
            assertThatThrownBy(() -> resumeService.createResume(req))
                    .isInstanceOf(ResumeBadRequestException.class)
                    .hasMessageContaining("name");
        }

        @ParameterizedTest(name = "email=''{0}'' should throw")
        @NullAndEmptySource
        @ValueSource(strings = {"notAnEmail", "missing@", "@nodomain"})
        @DisplayName("rejects invalid email")
        void rejectsInvalidEmail(String email) {
            var req = new ResumeRequest("Alice", email, null, null, null);
            assertThatThrownBy(() -> resumeService.createResume(req))
                    .isInstanceOf(ResumeBadRequestException.class)
                    .hasMessageContaining("email");
        }

        @Test
        @DisplayName("stores empty skills list when null provided")
        void handlesNullSkills() {
            var req = new ResumeRequest("Bob", "bob@x.com", null, null, null);
            var saved = Resume.builder().id(2L).name("Bob").email("bob@x.com").skills(Collections.emptyList()).build();
            given(resumeRepository.save(any())).willReturn(saved);

            var result = resumeService.createResume(req);
            assertThat(result.getSkills()).isNotNull().isEmpty();
        }
    }

    // ── getResumeById ─────────────────────────────────────────────

    @Nested
    @DisplayName("getResumeById()")
    class GetById {

        @Test
        @DisplayName("returns resume when found")
        void foundResume() {
            var resume = Resume.builder().id(5L).name("Sara").email("sara@a.com").skills(List.of()).build();
            given(resumeRepository.findById(5L)).willReturn(Optional.of(resume));

            ResumeResponse result = resumeService.getResumeById(5L);
            assertThat(result.getId()).isEqualTo(5L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when not found")
        void notFound() {
            given(resumeRepository.findById(99L)).willReturn(Optional.empty());
            assertThatThrownBy(() -> resumeService.getResumeById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    // ── getAllResumes ─────────────────────────────────────────────

    @Nested
    @DisplayName("getAllResumes()")
    class GetAll {

        @Test
        @DisplayName("returns all resumes")
        void returnsAll() {
            var list = List.of(
                    Resume.builder().id(1L).name("A").email("a@a.com").skills(List.of()).build(),
                    Resume.builder().id(2L).name("B").email("b@b.com").skills(List.of()).build()
            );
            given(resumeRepository.findAll()).willReturn(list);

            var results = resumeService.getAllResumes();
            assertThat(results).hasSize(2);
        }

        @Test
        @DisplayName("returns empty list when none exist")
        void returnsEmpty() {
            given(resumeRepository.findAll()).willReturn(Collections.emptyList());
            assertThat(resumeService.getAllResumes()).isEmpty();
        }
    }

    // ── deleteResume ─────────────────────────────────────────────

    @Nested
    @DisplayName("deleteResume()")
    class Delete {

        @Test
        @DisplayName("deletes existing resume")
        void deletesSuccessfully() {
            given(resumeRepository.existsById(3L)).willReturn(true);
            assertThatCode(() -> resumeService.deleteResume(3L)).doesNotThrowAnyException();
            then(resumeRepository).should().deleteById(3L);
        }

        @Test
        @DisplayName("throws when resume not found for deletion")
        void throwsOnMissing() {
            given(resumeRepository.existsById(404L)).willReturn(false);
            assertThatThrownBy(() -> resumeService.deleteResume(404L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
