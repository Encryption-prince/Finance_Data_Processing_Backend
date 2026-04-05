package com.assignment.fdp.controller;

import com.assignment.fdp.dto.DashboardSummaryResponse;
import com.assignment.fdp.service.FinancialRecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name="Dashboard API")
public class DashboardController {

    private final FinancialRecordService service;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')") // As per requirements, Viewers might be restricted or allowed
    public ResponseEntity<DashboardSummaryResponse> getSummary() {
        return ResponseEntity.ok(service.getDashboardSummary());
    }
}
