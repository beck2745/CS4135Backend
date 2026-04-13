package com.skillswap.tutor.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tutor_skills")
public class TutorSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutor_profile_id")
    private TutorProfile profile;

    @Column(nullable = false)
    private String name;

    private String proficiencyLevel;
    private String category;
    private String subcategory;

    @Column(length = 2000)
    private String experienceNote;

    public TutorSkill() {}

    public TutorSkill(
            String name,
            String proficiencyLevel,
            String category,
            String subcategory,
            String experienceNote) {
        this.name = name;
        this.proficiencyLevel = proficiencyLevel;
        this.category = category;
        this.subcategory = subcategory;
        this.experienceNote = experienceNote;
    }

    public Long getId() {
        return id;
    }

    public TutorProfile getProfile() {
        return profile;
    }

    public void setProfile(TutorProfile profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(String proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getExperienceNote() {
        return experienceNote;
    }

    public void setExperienceNote(String experienceNote) {
        this.experienceNote = experienceNote;
    }
}
