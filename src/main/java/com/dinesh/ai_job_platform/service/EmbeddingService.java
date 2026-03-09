package com.dinesh.ai_job_platform.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.List;

@Service
public class EmbeddingService {

    private final RestTemplate restTemplate;

    public EmbeddingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public float[] generateEmbedding(String text) {

        String url = "http://localhost:11434/api/embeddings";

        Map<String, Object> request = Map.of(
                "model", "nomic-embed-text",
                "prompt", text
        );

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);

        Map body = restTemplate.postForObject(url, request, Map.class);
        System.out.println("Embedding raw response: " + body);

        List<Double> embeddingList = (List<Double>) body.get("embedding");

        float[] embedding = new float[embeddingList.size()];

        for (int i = 0; i < embeddingList.size(); i++) {
            embedding[i] = embeddingList.get(i).floatValue();
        }

        return embedding;
    }
}