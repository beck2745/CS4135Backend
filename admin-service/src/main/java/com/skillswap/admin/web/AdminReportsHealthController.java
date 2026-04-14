package com.skillswap.admin.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AdminReportsHealthController {

    @GetMapping("/api/admin/health")
    public Map<String, Object> adminHealth() {
        return Map.of("service", "admin-service", "context", "admin", "status", "UP");
    }

    @GetMapping("/api/reports/health")
    public Map<String, Object> reportsHealth() {
        return Map.of("service", "admin-service", "context", "reports", "status", "UP");
    }
}
