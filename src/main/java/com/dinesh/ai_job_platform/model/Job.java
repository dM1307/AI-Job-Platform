package com.dinesh.ai_job_platform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "job")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @JsonIgnore
    @Transient
    @Column(columnDefinition = "vector(768)")
    private float[] embedding;

    public Job() {}

    public Job(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public float[] getEmbedding() {
        return embedding;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }
}