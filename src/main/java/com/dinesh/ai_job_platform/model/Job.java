package com.dinesh.ai_job_platform.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String company;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<JobSkill> requiredSkills = new ArrayList<>();

    public Job() {}

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public List<JobSkill> getRequiredSkills() {
        return requiredSkills;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setRequiredSkills(List<JobSkill> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }
}