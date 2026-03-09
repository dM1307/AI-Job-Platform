package com.dinesh.ai_job_platform.service;

import com.dinesh.ai_job_platform.exception.ExternalServiceException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class OllamaService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String extractSkills(String resumeText) {

        String url = "http://localhost:11434/api/generate";

        String prompt = """
                Extract the technical skills from this resume.
                Return only a comma separated list.

                Resume:
                """ + resumeText;

        Map<String, Object> request = Map.of(
                "model", "llama3",
                "prompt", prompt,
                "stream", false
        );

        try {
            Map response = restTemplate.postForObject(url, request, Map.class);
            Object raw = response == null ? null : response.get("response");
            if (raw == null) {
                throw new ExternalServiceException("Skill extraction service returned an invalid response");
            }

            return raw.toString();
        } catch (RestClientException ex) {
            throw new ExternalServiceException("Failed to call skill extraction service", ex);
        }
    }
}
