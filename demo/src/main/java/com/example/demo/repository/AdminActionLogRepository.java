package com.example.demo.repository;

import com.example.demo.entity.AdminActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminActionLogRepository extends JpaRepository<AdminActionLog, Long> {

    List<AdminActionLog> findByReportIdOrderByPerformedAtDesc(Long reportId);

    List<AdminActionLog> findAllByOrderByPerformedAtDesc();
}
