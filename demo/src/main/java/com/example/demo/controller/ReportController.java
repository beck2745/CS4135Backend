package com.example.demo.controller;

import com.example.demo.dto.ReportRequestDTO;
import com.example.demo.dto.ReportResponseDTO;
import com.example.demo.service.ReportService;
import com.example.demo.valueobject.ContentType;
import com.example.demo.valueobject.ReportStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/api/reports/health")
    public String health() { return "OK"; }

    // Any logged-in user can submit a report
    @PostMapping("/api/reports")
    @ResponseStatus(HttpStatus.CREATED)
    public ReportResponseDTO submit(@RequestBody ReportRequestDTO body) {
        return reportService.submit(body);
    }

    // Admin: view all reports, optionally filtered by status or content type
    @GetMapping("/api/admin/reports")
    public List<ReportResponseDTO> getReports(
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) ContentType contentType
    ) {
        if (status != null) {
            return reportService.getByStatus(status);
        }
        if (contentType != null) {
            return reportService.getByContentType(contentType);
        }
        return reportService.getAll();
    }

    // Admin: view a single report by ID
    @GetMapping("/api/admin/reports/{id}")
    public ReportResponseDTO getById(@PathVariable Long id) {
        return reportService.getById(id);
    }
}
