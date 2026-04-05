package com.assignment.fdp.dto;

import com.assignment.fdp.model.FinancialRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardSummaryResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;
    private Map<String, BigDecimal> categoryTotals;
    private List<FinancialRecord> recentActivity;
    private Map<String, BigDecimal> weeklyIncomeTrend;
    private Map<String, BigDecimal> monthlyIncomeTrend;
}