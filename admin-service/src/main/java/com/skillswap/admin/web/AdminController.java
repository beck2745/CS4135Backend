package com.skillswap.admin.web;

import com.skillswap.admin.dto.AdminActionLogDTO;
import com.skillswap.admin.dto.BlockedContentDTO;
import com.skillswap.admin.dto.ReportResponseDTO;
import com.skillswap.admin.dto.UserSummaryDTO;
import com.skillswap.admin.exception.ResourceNotFoundException;
import com.skillswap.admin.model.Message;
import com.skillswap.admin.repository.MessageRepository;
import com.skillswap.admin.service.AdminService;
import com.skillswap.admin.valueobject.ContentType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final MessageRepository messageRepository;
    private final RestTemplate restTemplate;

    public AdminController(AdminService adminService,
            MessageRepository messageRepository,
            RestTemplate restTemplate) {
        this.adminService = adminService;
        this.messageRepository = messageRepository;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/messages/{id}")
    public Message getMessageById(@PathVariable Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
    }

    @GetMapping("/users/{id}")
    public UserSummaryDTO getUserById(@PathVariable Long id) {
        try {
            return restTemplate.getForObject(
                    "http://identity-service/api/internal/users/" + id,
                    UserSummaryDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("User not found");
        }
    }

    @PostMapping("/reports/{reportId}/block")
    public ReportResponseDTO blockReport(
            @PathVariable Long reportId,
            @RequestParam Long adminId,
            @RequestParam(required = false, defaultValue = "") String notes) {
        return adminService.blockReport(reportId, adminId, notes);
    }

    @PatchMapping("/reports/{reportId}/dismiss")
    public ReportResponseDTO dismissReport(
            @PathVariable Long reportId,
            @RequestParam Long adminId,
            @RequestParam(required = false, defaultValue = "") String notes) {
        return adminService.dismissReport(reportId, adminId, notes);
    }

    @GetMapping("/audit-log")
    public List<AdminActionLogDTO> getAuditLog() {
        return adminService.getAuditLog();
    }

    @GetMapping("/reports/{reportId}/audit-log")
    public List<AdminActionLogDTO> getAuditLogForReport(@PathVariable Long reportId) {
        return adminService.getAuditLogForReport(reportId);
    }

    @DeleteMapping("/block")
    public void unblockContent(
            @RequestParam ContentType contentType,
            @RequestParam Long contentId,
            @RequestParam Long adminId,
            @RequestParam(required = false, defaultValue = "") String reason) {
        adminService.unblockContent(contentType, contentId, adminId, reason);
    }

    @GetMapping("/blocked")
    public List<BlockedContentDTO> getBlocked(
            @RequestParam(required = false) ContentType contentType) {
        return adminService.getBlocked(contentType);
    }
}
