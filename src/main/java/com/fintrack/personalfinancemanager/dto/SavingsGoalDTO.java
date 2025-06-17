package com.fintrack.personalfinancemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for SavingsGoal entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsGoalDTO {

    private Long id;

    // @NotBlank(message = "Name is required")
    // @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    // @NotNull(message = "Target amount is required")
    // @Positive(message = "Target amount must be positive")
    private BigDecimal targetAmount;

    // @NotNull(message = "Target date is required")
    // @Future(message = "Target date must be in the future")
    private LocalDate targetDate;

    private LocalDate startDate;

    private BigDecimal currentAmount;

    // Calculated fields
    private double percentageComplete;
    private BigDecimal remainingAmount;
    private long daysRemaining;
    private boolean isCompleted;
}
