package com.fintrack.personalfinancemanager.service;

import com.fintrack.personalfinancemanager.model.SavingsGoal;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for SavingsGoal operations.
 */
public interface SavingsGoalService {

    /**
     * Create a new savings goal.
     *
     * @param savingsGoal the savings goal to create
     * @param userId the user ID
     * @return the created savings goal
     * @throws IllegalArgumentException if a savings goal with the same name already exists for the user
     */
    SavingsGoal createSavingsGoal(SavingsGoal savingsGoal, Long userId);

    /**
     * Update an existing savings goal.
     *
     * @param savingsGoal the savings goal with updated information
     * @param userId the user ID
     * @return the updated savings goal
     * @throws IllegalArgumentException if the savings goal does not exist or does not belong to the user
     */
    SavingsGoal updateSavingsGoal(SavingsGoal savingsGoal, Long userId);

    /**
     * Delete a savings goal by ID.
     *
     * @param savingsGoalId the savings goal ID
     * @param userId the user ID
     * @throws IllegalArgumentException if the savings goal does not exist or does not belong to the user
     */
    void deleteSavingsGoal(Long savingsGoalId, Long userId);

    /**
     * Find a savings goal by ID.
     *
     * @param savingsGoalId the savings goal ID
     * @param userId the user ID
     * @return the savings goal if found
     * @throws IllegalArgumentException if the savings goal does not exist or does not belong to the user
     */
    SavingsGoal findById(Long savingsGoalId, Long userId);

    /**
     * Find all savings goals for a user.
     *
     * @param userId the user ID
     * @return list of savings goals
     */
    List<SavingsGoal> findAllByUser(Long userId);

    /**
     * Find all active savings goals for a user (where target date is in the future).
     *
     * @param userId the user ID
     * @return list of active savings goals
     */
    List<SavingsGoal> findActiveGoalsByUser(Long userId);

    /**
     * Find all completed savings goals for a user (where current amount >= target amount).
     *
     * @param userId the user ID
     * @return list of completed savings goals
     */
    List<SavingsGoal> findCompletedGoalsByUser(Long userId);

    /**
     * Add funds to a savings goal.
     *
     * @param savingsGoalId the savings goal ID
     * @param userId the user ID
     * @param amount the amount to add
     * @return the updated savings goal
     * @throws IllegalArgumentException if the savings goal does not exist, does not belong to the user, or the amount is negative
     */
    SavingsGoal addFunds(Long savingsGoalId, Long userId, BigDecimal amount);

    /**
     * Calculate the total amount saved across all savings goals for a user.
     *
     * @param userId the user ID
     * @return the total amount saved
     */
    BigDecimal calculateTotalSaved(Long userId);

    /**
     * Calculate the total target amount across all savings goals for a user.
     *
     * @param userId the user ID
     * @return the total target amount
     */
    BigDecimal calculateTotalTargetAmount(Long userId);

    /**
     * Calculate the overall progress percentage across all savings goals for a user.
     *
     * @param userId the user ID
     * @return the overall progress percentage (0-100)
     */
    double calculateOverallProgress(Long userId);
}