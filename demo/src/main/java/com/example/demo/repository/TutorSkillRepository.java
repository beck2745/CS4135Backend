package com.example.demo.repository;

import com.example.demo.entity.TutorSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TutorSkillRepository extends JpaRepository<TutorSkill, Long> {

    @Query("SELECT DISTINCT s.name FROM TutorSkill s WHERE s.name IS NOT NULL AND TRIM(s.name) <> ''")
    List<String> findDistinctSkillNames();
}
