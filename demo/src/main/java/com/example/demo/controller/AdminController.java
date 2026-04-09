package com.example.demo.controller;

import com.example.demo.dto.AdminActionLogDTO;
import com.example.demo.dto.BlockedContentDTO;
import com.example.demo.dto.ReportResponseDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Message;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AdminService;
import com.example.demo.valueobject.ContentType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    private final AdminService adminService;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public AdminController(AdminService adminService,
                           MessageRepository messageRepository,
                           UserRepository userRepository) {
        this.adminService = adminService;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    // Fetch a single message by ID (for viewing reported message content)
    @GetMapping("/messages/{id}")
    public Message getMessageById(@PathVariable Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
    }

    // Fetch a user by ID (for viewing reported user details)
    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // Mark a report as reviewed (admin has looked at it and it warrants action)
    @PatchMapping("/reports/{reportId}/review")
    public ReportResponseDTO reviewReport(
            @PathVariable Long reportId,
            @RequestParam Long adminId,
            @RequestParam(required = false, defaultValue = "") String notes) {
        return adminService.reviewReport(reportId, adminId, notes);
    }

    // Dismiss a report (admin has looked at it and it is not a problem)
    @PatchMapping("/reports/{reportId}/dismiss")
    public ReportResponseDTO dismissReport(
            @PathVariable Long reportId,
            @RequestParam Long adminId,
            @RequestParam(required = false, defaultValue = "") String notes) {
        return adminService.dismissReport(reportId, adminId, notes);
    }

    // Full audit log of all admin actions
    @GetMapping("/audit-log")
    public List<AdminActionLogDTO> getAuditLog() {
        return adminService.getAuditLog();
    }

    // Audit log scoped to a single report
    @GetMapping("/reports/{reportId}/audit-log")
    public List<AdminActionLogDTO> getAuditLogForReport(@PathVariable Long reportId) {
        return adminService.getAuditLogForReport(reportId);
    }

    // Block a piece of content
    @PostMapping("/block")
    @ResponseStatus(HttpStatus.CREATED)
    public BlockedContentDTO blockContent(
            @RequestParam ContentType contentType,
            @RequestParam Long contentId,
            @RequestParam Long adminId,
            @RequestParam(required = false, defaultValue = "") String reason
    ) {
        return adminService.blockContent(contentType, contentId, adminId, reason);
    }

    // Unblock a piece of content
    @DeleteMapping("/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unblockContent(
            @RequestParam ContentType contentType,
            @RequestParam Long contentId,
            @RequestParam Long adminId,
            @RequestParam(required = false, defaultValue = "") String reason
    ) {
        adminService.unblockContent(contentType, contentId, adminId, reason);
    }

    // List all blocked content, optionally filtered by type
    @GetMapping("/blocked")
    public List<BlockedContentDTO> getBlocked(
            @RequestParam(required = false) ContentType contentType
    ) {
        return adminService.getBlocked(contentType);
    }
}
