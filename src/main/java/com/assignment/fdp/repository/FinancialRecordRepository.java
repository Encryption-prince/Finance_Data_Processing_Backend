package com.assignment.fdp.repository;

import com.assignment.fdp.model.FinancialRecord;
import com.assignment.fdp.model.RecordType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long>, JpaSpecificationExecutor<FinancialRecord> {
    @Query("SELECT SUM(r.amount) FROM FinancialRecord r WHERE r.type = :type")
    BigDecimal sumByType(@Param("type") RecordType type);

    @Query("SELECT r.category, SUM(r.amount) FROM FinancialRecord r GROUP BY r.category")
    List<Object[]> getCategoryWiseTotals();

    List<FinancialRecord> findTop5ByOrderByDateDesc();

    List<FinancialRecord> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT r FROM FinancialRecord r WHERE " +
            "LOWER(r.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<FinancialRecord> searchRecords(@Param("keyword") String keyword);
}