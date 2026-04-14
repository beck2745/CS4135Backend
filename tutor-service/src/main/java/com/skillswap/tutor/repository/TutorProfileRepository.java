package com.skillswap.tutor.repository;

import com.skillswap.tutor.entity.TutorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long> {

    Optional<TutorProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    List<TutorProfile> findByBlockedFalse();

    @Query("SELECT DISTINCT p FROM TutorProfile p LEFT JOIN p.skills s WHERE "
            + "p.blocked = false AND "
            + "(:skill IS NULL OR :skill = '' OR LOWER(s.name) LIKE LOWER(CONCAT('%', :skill, '%')))")
    List<TutorProfile> findDistinctBySkillNameContaining(@Param("skill") String skill);
}
