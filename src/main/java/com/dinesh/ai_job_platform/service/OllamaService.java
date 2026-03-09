package com.dinesh.ai_job_platform.service;

import org.springframework.stereotype.Service;
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

        Map response = restTemplate.postForObject(url, request, Map.class);

        return (String) response.get("response");
    }
}