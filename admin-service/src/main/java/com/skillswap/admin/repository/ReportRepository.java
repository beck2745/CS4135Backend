package com.skillswap.admin.repository;

import com.skillswap.admin.entity.Report;
import com.skillswap.admin.valueobject.ContentType;
import com.skillswap.admin.valueobject.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status);

    List<Report> findByContentTypeOrderByCreatedAtDesc(ContentType contentType);

    List<Report> findAllByOrderByCreatedAtDesc();

    boolean existsByReportedByUserIdAndContentTypeAndContentId(
            Long reportedByUserId, ContentType contentType, Long contentId);
}
