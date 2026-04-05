package com.assignment.fdp.dto;

import com.assignment.fdp.model.RecordType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecordRequest {
    private BigDecimal amount;
    private RecordType type;
    private String category;
    private LocalDate date;
    private String description;
}