package com.skillswap.admin.repository;

import com.skillswap.admin.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByThreadIdOrderBySentAtAsc(Long threadId);
}
