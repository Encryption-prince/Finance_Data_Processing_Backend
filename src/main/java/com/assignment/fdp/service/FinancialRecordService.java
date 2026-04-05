package com.assignment.fdp.service;

import com.assignment.fdp.dto.DashboardSummaryResponse;
import com.assignment.fdp.dto.RecordRequest;
import com.assignment.fdp.model.FinancialRecord;
import com.assignment.fdp.model.RecordType;
import com.assignment.fdp.repository.FinancialRecordRepository;
import com.assignment.fdp.repository.FinancialRecordSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository repository;

    public FinancialRecord createRecord(RecordRequest request) {
        FinancialRecord record = FinancialRecord.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .date(request.getDate())
                .description(request.getDescription())
                .build();

        return repository.save(record);
    }

    public List<FinancialRecord> getAllRecords() {
        return repository.findAll();
    }

    public FinancialRecord getRecordById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));
    }

    public FinancialRecord updateRecord(Long id, RecordRequest request) {
        FinancialRecord existingRecord = getRecordById(id);

        existingRecord.setAmount(request.getAmount());
        existingRecord.setType(request.getType());
        existingRecord.setCategory(request.getCategory());
        existingRecord.setDate(request.getDate());
        existingRecord.setDescription(request.getDescription());

        return repository.save(existingRecord);
    }

    public void deleteRecord(Long id) {
        repository.deleteById(id);
    }

    public List<FinancialRecord> searchRecords(String keyword) {
        return repository.searchRecords(keyword);
    }

    public List<FinancialRecord> filterRecords(String category, RecordType type, LocalDate startDate, LocalDate endDate) {
        return repository.findAll(FinancialRecordSpecification.filter(category, type, startDate, endDate));
    }


    public DashboardSummaryResponse getDashboardSummary() {
        BigDecimal totalIncome = repository.sumByType(RecordType.INCOME);
        BigDecimal totalExpenses = repository.sumByType(RecordType.EXPENSE);

        // Handle nulls if there are no records yet
        totalIncome = (totalIncome != null) ? totalIncome : BigDecimal.ZERO;
        totalExpenses = (totalExpenses != null) ? totalExpenses : BigDecimal.ZERO;

        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        List<Object[]> categoryData = repository.getCategoryWiseTotals();
        Map<String, BigDecimal> categoryTotals = new HashMap<>();
        for (Object[] result : categoryData) {
            categoryTotals.put((String) result[0], (BigDecimal) result[1]);
        }

        List<FinancialRecord> recentActivity = repository.findTop5ByOrderByDateDesc();

        //Finding new trends
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(7);
        LocalDate thirtyDaysAgo = today.minusDays(30);

        //Finding weekly trends
        List<FinancialRecord> lastWeekRecords = repository.findByDateBetween(sevenDaysAgo, today);
        Map<String, BigDecimal> weeklyIncome = lastWeekRecords.stream()
                .filter(r -> r.getType() == RecordType.INCOME)
                .collect(Collectors.groupingBy(
                        r -> r.getDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                        Collectors.reducing(BigDecimal.ZERO, FinancialRecord::getAmount, BigDecimal::add)
                ));

        //Finding monthly trends
        List<FinancialRecord> lastMonthRecords = repository.findByDateBetween(thirtyDaysAgo, today);
        Map<String, BigDecimal> monthlyIncome = lastMonthRecords.stream()
                .filter(r -> r.getType() == RecordType.INCOME)
                .collect(Collectors.groupingBy(
                        r -> r.getDate().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                        Collectors.reducing(BigDecimal.ZERO, FinancialRecord::getAmount, BigDecimal::add)
                ));

        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .categoryTotals(categoryTotals)
                .recentActivity(recentActivity)
                .weeklyIncomeTrend(weeklyIncome)
                .monthlyIncomeTrend(monthlyIncome)
                .build();
    }

}