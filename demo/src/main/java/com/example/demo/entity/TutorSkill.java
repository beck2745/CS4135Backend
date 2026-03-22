package com.example.demo.entity;

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

    public TutorSkill() {}

    public TutorSkill(String name, String proficiencyLevel) {
        this.name = name;
        this.proficiencyLevel = proficiencyLevel;
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
}
