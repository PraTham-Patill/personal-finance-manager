package com.fintrack.personalfinancemanager.service;

import com.fintrack.personalfinancemanager.model.Budget;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

/**
 * Service interface for Budget operations.
 */
public interface BudgetService {

    /**
     * Create a new budget.
     *
     * @param budget the budget to create
     * @param userId the user ID
     * @return the created budget
     * @throws IllegalArgumentException if a budget for the same category and month already exists
     */
    Budget createBudget(Budget budget, Long userId);

    /**
     * Update an existing budget.
     *
     * @param budget the budget with updated information
     * @param userId the user ID
     * @return the updated budget
     * @throws IllegalArgumentException if the budget does not exist or does not belong to the user
     */
    Budget updateBudget(Budget budget, Long userId);

    /**
     * Delete a budget by ID.
     *
     * @param budgetId the budget ID
     * @param userId the user ID
     * @throws IllegalArgumentException if the budget does not exist or does not belong to the user
     */
    void deleteBudget(Long budgetId, Long userId);

    /**
     * Find a budget by ID.
     *
     * @param budgetId the budget ID
     * @param userId the user ID
     * @return the budget if found
     * @throws IllegalArgumentException if the budget does not exist or does not belong to the user
     */
    Budget findById(Long budgetId, Long userId);

    /**
     * Find all budgets for a user.
     *
     * @param userId the user ID
     * @return list of budgets
     */
    List<Budget> findAllByUser(Long userId);

    /**
     * Find all budgets for a user and month.
     *
     * @param userId the user ID
     * @param month the month
     * @return list of budgets
     */
    List<Budget> findByUserAndMonth(Long userId, YearMonth month);

    /**
     * Find a budget for a user, category, and month.
     *
     * @param userId the user ID
     * @param categoryId the category ID
     * @param month the month
     * @return the budget if found, null otherwise
     */
    Budget findByUserAndCategoryAndMonth(Long userId, Long categoryId, YearMonth month);

    /**
     * Calculate the total budget amount for a user and month.
     *
     * @param userId the user ID
     * @param month the month
     * @return the total budget amount
     */
    BigDecimal calculateTotalBudgetForMonth(Long userId, YearMonth month);

    /**
     * Calculate the percentage spent of a budget.
     *
     * @param budgetId the budget ID
     * @param userId the user ID
     * @return the percentage spent (0-100)
     */
    double calculateBudgetPercentageSpent(Long budgetId, Long userId);

    /**
     * Calculate the remaining amount in a budget.
     *
     * @param budgetId the budget ID
     * @param userId the user ID
     * @return the remaining amount
     */
    BigDecimal calculateBudgetRemainingAmount(Long budgetId, Long userId);

    /**
     * Find all distinct months for which a user has budgets, ordered by month.
     *
     * @param userId the user ID
     * @return list of distinct months
     */
    List<YearMonth> findDistinctMonthsByUser(Long userId);

    /**
     * Delete all budgets for a user and month.
     *
     * @param userId the user ID
     * @param month the month
     */
    void deleteAllByUserAndMonth(Long userId, YearMonth month);
}