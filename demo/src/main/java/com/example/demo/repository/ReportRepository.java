package com.example.demo.repository;

import com.example.demo.entity.Report;
import com.example.demo.valueobject.ContentType;
import com.example.demo.valueobject.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status);

    List<Report> findByContentTypeOrderByCreatedAtDesc(ContentType contentType);

    List<Report> findAllByOrderByCreatedAtDesc();

    boolean existsByReportedByUserIdAndContentTypeAndContentId(
            Long reportedByUserId, ContentType contentType, Long contentId);
}
