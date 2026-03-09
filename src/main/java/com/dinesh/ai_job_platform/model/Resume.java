package com.dinesh.ai_job_platform.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resumes")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String candidateName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String rawText;

    @OneToMany(
            mappedBy = "resume",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonManagedReference
    private List<Skill> skills = new ArrayList<>();

    public Resume() {}

    public Resume(String candidateName, String email, String rawText) {
        this.candidateName = candidateName;
        this.email = email;
        this.rawText = rawText;
    }

    public Long getId() {
        return id;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public String getEmail() {
        return email;
    }

    public String getRawText() {
        return rawText;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
        skill.setResume(this);
    }

    public void removeSkill(Skill skill) {
        skills.remove(skill);
        skill.setResume(null);
    }
}