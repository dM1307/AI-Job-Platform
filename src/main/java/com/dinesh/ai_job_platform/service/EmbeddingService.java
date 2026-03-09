package com.dinesh.ai_job_platform.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            Map body = response.getBody();
            List<Double> embeddingList = body == null
                    ? Collections.emptyList()
                    : (List<Double>) body.getOrDefault("embedding", Collections.emptyList());

            if (embeddingList.isEmpty()) {
                throw new IllegalStateException("Embedding service returned an empty embedding vector");
            }

            float[] embedding = new float[embeddingList.size()];

            for (int i = 0; i < embeddingList.size(); i++) {
                embedding[i] = embeddingList.get(i).floatValue();
            }

            return embedding;
        } catch (RestClientException ex) {
            throw new IllegalStateException("Failed to generate embedding", ex);
        }
    }
}
