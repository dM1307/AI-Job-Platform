package com.dinesh.ai_job_platform.controller;

import com.dinesh.ai_job_platform.dto.ResumeResponse;
import com.dinesh.ai_job_platform.exception.ResourceNotFoundException;
import com.dinesh.ai_job_platform.service.JobMatchingService;
import com.dinesh.ai_job_platform.service.ResumeParsingService;
import com.dinesh.ai_job_platform.service.ResumeService;
import com.dinesh.ai_job_platform.service.SkillService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ResumeController.class)
class ResumeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResumeService resumeService;

    @MockBean
    private JobMatchingService jobMatchingService;

    @MockBean
    private SkillService skillService;

    @MockBean
    private ResumeParsingService resumeParsingService;

    @Test
    void getAllResumes_shouldReturnOk() throws Exception {
        ResumeResponse response = new ResumeResponse();
        response.setId(1L);
        response.setCandidateName("Alice");
        response.setEmail("alice@example.com");
        response.setSkills(List.of("Java"));

        given(resumeService.getAllResumes()).willReturn(List.of(response));

        mockMvc.perform(get("/api/resumes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].candidateName").value("Alice"));
    }

    @Test
    void createResume_withInvalidBody_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/resumes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getResume_whenNotFound_shouldReturnNotFound() throws Exception {
        given(resumeService.getResume(99L)).willThrow(new ResourceNotFoundException("Resume not found"));

        mockMvc.perform(get("/api/resumes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
