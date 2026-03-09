package com.dinesh.ai_job_platform.model;

import jakarta.persistence.*;

@Entity
public class JobSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String skillName;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    public JobSkill() {}

    public Long getId() {
        return id;
    }

    public String getSkillName() {
        return skillName;
    }

    public Job getJob() {
        return job;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public void setJob(Job job) {
        this.job = job;
    }
}