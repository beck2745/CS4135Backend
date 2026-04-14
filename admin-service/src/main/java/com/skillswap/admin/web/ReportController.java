package com.skillswap.admin.web;

import com.skillswap.admin.dto.ReportRequestDTO;
import com.skillswap.admin.dto.ReportResponseDTO;
import com.skillswap.admin.service.ReportService;
import com.skillswap.admin.valueobject.ContentType;
import com.skillswap.admin.valueobject.ReportStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/api/reports")
    @ResponseStatus(HttpStatus.CREATED)
    public ReportResponseDTO submit(@RequestBody ReportRequestDTO body) {
        return reportService.submit(body);
    }

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

    @GetMapping("/api/admin/reports/{id}")
    public ReportResponseDTO getById(@PathVariable Long id) {
        return reportService.getById(id);
    }
}
