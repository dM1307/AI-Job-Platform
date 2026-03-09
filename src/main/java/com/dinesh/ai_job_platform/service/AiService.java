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
        Extract the technical skills from the following resume text.
        Return only a comma separated list.

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

        String result = (String) response.getBody().get("response");

// clean formatting
        result = result.replace("\n", " ").trim();

// remove explanation text if present
        if (result.contains(":")) {
            result = result.substring(result.indexOf(":") + 1).trim();
        }

        return result;
    }
}