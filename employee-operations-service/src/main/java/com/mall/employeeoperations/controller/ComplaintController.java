package com.mall.employeeoperations.controller;

import com.mall.employeeoperations.dto.ComplaintRequest;
import com.mall.employeeoperations.dto.ComplaintResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    @PostMapping
    public ResponseEntity<ComplaintResponse> createComplaint(@RequestBody ComplaintRequest request) {
        return ResponseEntity.ok(new ComplaintResponse());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintResponse> getComplaint(@PathVariable String id) {
        return ResponseEntity.ok(new ComplaintResponse());
    }

    @GetMapping
    public ResponseEntity<List<ComplaintResponse>> getAllComplaints() {
        return ResponseEntity.ok(List.of());
    }
}
