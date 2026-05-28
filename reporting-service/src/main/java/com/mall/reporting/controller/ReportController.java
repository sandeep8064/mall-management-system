package com.mall.reporting.controller;

import com.mall.reporting.dto.ReportRequest;
import com.mall.reporting.dto.ReportResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(@RequestBody ReportRequest request) {
        return ResponseEntity.ok(new ReportResponse());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getReport(@PathVariable String id) {
        return ResponseEntity.ok(new ReportResponse());
    }

    @GetMapping
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        return ResponseEntity.ok(List.of());
    }
}
