package com.dinesh.ai_job_platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuralhire.platform.dto.ResumeRequest;
import com.neuralhire.platform.dto.ResumeResponse;
import com.neuralhire.platform.exception.ResourceNotFoundException;
import com.neuralhire.platform.service.ResumeService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc integration tests for ResumeController.
 *
 * Tests JSON serialization, HTTP status codes, validation, and error handling
 * without starting a real HTTP server.
 */
@WebMvcTest(ResumeController.class)
@DisplayName("ResumeController — MockMvc Tests")
class ResumeControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean  private ResumeService resumeService;

    // ── POST /api/resumes ─────────────────────────────────────

    @Nested
    @DisplayName("POST /api/resumes")
    class CreateResume {

        @Test
        @DisplayName("returns 201 and resume JSON on success")
        void creates() throws Exception {
            var response = ResumeResponse.builder()
                    .id(1L).name("Alice").email("alice@a.com")
                    .skills(List.of("Java")).build();
            given(resumeService.createResume(any())).willReturn(response);

            var body = new ResumeRequest("Alice","alice@a.com",null,null,List.of("Java"));

            mockMvc.perform(post("/api/resumes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Alice"))
                    .andExpect(jsonPath("$.skills[0]").value("Java"));
        }

        @Test
        @DisplayName("returns 400 when name is blank")
        void rejectsBlankName() throws Exception {
            var body = new ResumeRequest("","alice@a.com",null,null,null);
            mockMvc.perform(post("/api/resumes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 when email is missing")
        void rejectsMissingEmail() throws Exception {
            var body = new ResumeRequest("Alice",null,null,null,null);
            mockMvc.perform(post("/api/resumes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns structured error on bad request")
        void structuredError() throws Exception {
            var body = new ResumeRequest("","","","",List.of());
            mockMvc.perform(post("/api/resumes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").exists())
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }

    // ── GET /api/resumes ──────────────────────────────────────

    @Nested
    @DisplayName("GET /api/resumes")
    class GetAll {

        @Test
        @DisplayName("returns 200 with list")
        void returnsList() throws Exception {
            given(resumeService.getAllResumes()).willReturn(List.of(
                    ResumeResponse.builder().id(1L).name("A").email("a@a.com").skills(List.of()).build(),
                    ResumeResponse.builder().id(2L).name("B").email("b@b.com").skills(List.of()).build()
            ));

            mockMvc.perform(get("/api/resumes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[1].name").value("B"));
        }

        @Test
        @DisplayName("returns 200 with empty array when none exist")
        void returnsEmpty() throws Exception {
            given(resumeService.getAllResumes()).willReturn(List.of());
            mockMvc.perform(get("/api/resumes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    // ── GET /api/resumes/{id} ──────────────────────────────────

    @Nested
    @DisplayName("GET /api/resumes/{id}")
    class GetById {

        @Test
        @DisplayName("returns 200 when found")
        void found() throws Exception {
            given(resumeService.getResumeById(5L)).willReturn(
                    ResumeResponse.builder().id(5L).name("Sara").email("s@s.com").skills(List.of()).build()
            );
            mockMvc.perform(get("/api/resumes/5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(5));
        }

        @Test
        @DisplayName("returns 404 when not found")
        void notFound() throws Exception {
            given(resumeService.getResumeById(99L)).willThrow(new ResourceNotFoundException("Resume not found: 99"));
            mockMvc.perform(get("/api/resumes/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    // ── DELETE /api/resumes/{id} ───────────────────────────────

    @Nested
    @DisplayName("DELETE /api/resumes/{id}")
    class Delete {

        @Test
        @DisplayName("returns 204 on success")
        void deletes() throws Exception {
            willDoNothing().given(resumeService).deleteResume(3L);
            mockMvc.perform(delete("/api/resumes/3"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("returns 404 when not found")
        void notFound() throws Exception {
            willThrow(new ResourceNotFoundException("Resume not found: 999"))
                    .given(resumeService).deleteResume(999L);
            mockMvc.perform(delete("/api/resumes/999"))
                    .andExpect(status().isNotFound());
        }
    }
}
