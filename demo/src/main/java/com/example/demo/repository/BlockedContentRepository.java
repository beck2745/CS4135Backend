package com.example.demo.repository;

import com.example.demo.entity.BlockedContent;
import com.example.demo.valueobject.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlockedContentRepository extends JpaRepository<BlockedContent, Long> {

    boolean existsByContentTypeAndContentId(ContentType contentType, Long contentId);

    Optional<BlockedContent> findByContentTypeAndContentId(ContentType contentType, Long contentId);

    List<BlockedContent> findByContentTypeOrderByBlockedAtDesc(ContentType contentType);

    List<BlockedContent> findAllByOrderByBlockedAtDesc();
}
