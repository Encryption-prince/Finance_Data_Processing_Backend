package com.assignment.fdp.controller;

import com.assignment.fdp.dto.RecordRequest;
import com.assignment.fdp.model.FinancialRecord;
import com.assignment.fdp.service.FinancialRecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
@Tag(name="Financial Records API")
public class FinancialRecordController {

    private final FinancialRecordService service;

    // Viewers, Analysts and Admins can all read the data
    @GetMapping
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<List<FinancialRecord>> getAllRecords() {
        return ResponseEntity.ok(service.getAllRecords());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<FinancialRecord> getRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getRecordById(id));
    }

    // Only Admins can create new records
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FinancialRecord> createRecord(@RequestBody RecordRequest request) {
        return ResponseEntity.ok(service.createRecord(request));
    }

    // Only Admins can update records that are existing
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FinancialRecord> updateRecord(
            @PathVariable Long id,
            @RequestBody RecordRequest request) {
        return ResponseEntity.ok(service.updateRecord(id, request));
    }

    // Only Admins can delete records
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        service.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }

    //Search functionality given only to Analyst and Admins
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<List<FinancialRecord>> searchRecords(@RequestParam String keyword) {
        return ResponseEntity.ok(service.searchRecords(keyword));
    }
}