package com.skillswap.tutor.entity;

import com.skillswap.tutor.model.VerificationStatus;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tutor_profiles", uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
public class TutorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tutorProfileId;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(length = 4000)
    private String biography;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    private Double averageRating;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TutorSkill> skills = new ArrayList<>();

    public TutorProfile() {}

    public Long getTutorProfileId() {
        return tutorProfileId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public List<TutorSkill> getSkills() {
        return skills;
    }

    public void replaceSkills(List<TutorSkill> newSkills) {
        skills.clear();
        for (TutorSkill s : newSkills) {
            s.setProfile(this);
            skills.add(s);
        }
    }
}
