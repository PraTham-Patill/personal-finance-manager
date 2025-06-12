package com.fintrack.personalfinancemanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity class representing a savings goal.
 */
@Entity
@Table(name = "savings_goals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Goal name is required")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Target amount is required")
    @Positive(message = "Target amount must be positive")
    @Column(nullable = false)
    private BigDecimal targetAmount;

    @NotNull(message = "Target date is required")
    @Future(message = "Target date must be in the future")
    @Column(nullable = false)
    private LocalDate targetDate;

    @NotNull(message = "Start date is required")
    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private BigDecimal currentAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Calculates the percentage completion of the savings goal.
     *
     * @return the percentage completion
     */
    @Transient
    public double getPercentageCompletion() {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        return currentAmount.divide(targetAmount, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    /**
     * Calculates the remaining amount to reach the target.
     *
     * @return the remaining amount
     */
    @Transient
    public BigDecimal getRemainingAmount() {
        return targetAmount.subtract(currentAmount);
    }
}