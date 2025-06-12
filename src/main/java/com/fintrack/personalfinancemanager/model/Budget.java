package com.fintrack.personalfinancemanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;

/**
 * Entity class representing a budget for a specific category and month.
 */
@Entity
@Table(name = "budgets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Budget amount is required")
    @Positive(message = "Budget amount must be positive")
    @Column(nullable = false)
    private BigDecimal amount;

    @NotNull(message = "Month is required")
    @Column(nullable = false)
    private YearMonth month;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Category is required")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;

    /**
     * Calculate the percentage spent of this budget based on actual spending.
     *
     * @param spent the amount spent in this category
     * @return the percentage spent (0-100)
     */
    @Transient
    public double calculatePercentageSpent(BigDecimal spent) {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return spent.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        
        return spent.divide(amount, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * Calculate the remaining amount in this budget.
     *
     * @param spent the amount spent in this category
     * @return the remaining amount
     */
    @Transient
    public BigDecimal calculateRemainingAmount(BigDecimal spent) {
        return amount.subtract(spent).max(BigDecimal.ZERO);
    }
}