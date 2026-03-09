package com.dinesh.ai_job_platform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @JsonIgnore
    @Transient
    private float[] embedding;

    @ManyToOne
    @JoinColumn(name = "resume_id")
    @JsonBackReference
    private Resume resume;

    public Skill() {}

    public Skill(String name, Resume resume) {
        this.name = name;
        this.resume = resume;
    }

    public Long getId() { return id; }

    public String getName() { return name; }

    public float[] getEmbedding() {
        return embedding;
    }

    public Resume getResume() { return resume; }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }

    public void setName(String name) { this.name = name; }
    public void setResume(Resume resume) { this.resume = resume; }
}