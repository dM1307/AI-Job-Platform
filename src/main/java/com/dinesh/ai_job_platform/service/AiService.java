package com.dinesh.ai_job_platform.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AiService {

    private final RestTemplate restTemplate;

    public AiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String extractSkills(String resumeText) {

        String prompt = """
        Extract only technical skills from the following resume.
        
        Rules:
        - Return ONLY a comma separated list.
        - Do NOT include any explanation.
        - Do NOT include sentences.
        - Only skill names.
        
        Resume:
        """ + resumeText;

        Map<String, Object> request = Map.of(
                "model", "llama3",
                "prompt", prompt,
                "stream", false
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        String url = "http://localhost:11434/api/generate";

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, entity, Map.class);

        Map responseBody = response.getBody();
        String result = responseBody == null ? null : (String) responseBody.get("response");

        if (result == null || result.isBlank()) {
            return "";
        }

// clean formatting
        result = result.replace("\n", " ").trim();

// remove explanation text if present
        if (result.toLowerCase().startsWith("skills:")) {
            result = result.substring("skills:".length()).trim();
        }

        return result;
    }
}