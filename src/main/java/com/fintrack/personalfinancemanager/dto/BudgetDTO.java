package com.fintrack.personalfinancemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * Data Transfer Object for Budget entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDTO {

    private Long id;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Month is required")
    private YearMonth month;

    @NotNull(message = "Category is required")
    private Long categoryId;
    
    private String categoryName;
    
    // Calculated fields
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private double percentageSpent;
}
