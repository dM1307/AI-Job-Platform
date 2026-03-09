package com.dinesh.ai_job_platform.service;

import com.dinesh.ai_job_platform.dto.JobRequest;
import com.dinesh.ai_job_platform.model.Job;
import com.dinesh.ai_job_platform.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final EmbeddingService embeddingService;

    public JobService(JobRepository jobRepository,
                      EmbeddingService embeddingService) {

        this.jobRepository = jobRepository;
        this.embeddingService = embeddingService;
    }

    public Job createJob(JobRequest request) {

        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());

        float[] embedding =
                embeddingService.generateEmbedding(
                        request.getTitle() + " " + request.getDescription()
                );

        job.setEmbedding(embedding);

        return jobRepository.save(job);
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }
}
