package com.fintrack.personalfinancemanager.repository;

import com.fintrack.personalfinancemanager.model.Budget;
import com.fintrack.personalfinancemanager.model.Category;
import com.fintrack.personalfinancemanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

/**
 * Repository interface for Budget entity operations.
 */
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    /**
     * Find all budgets for a specific user.
     *
     * @param user the user
     * @return list of budgets
     */
    List<Budget> findByUser(User user);

    /**
     * Find all budgets for a specific user and month.
     *
     * @param user the user
     * @param month the month
     * @return list of budgets
     */
    List<Budget> findByUserAndMonth(User user, YearMonth month);

    /**
     * Find a budget for a specific user, category, and month.
     *
     * @param user the user
     * @param category the category
     * @param month the month
     * @return the budget if found
     */
    Budget findByUserAndCategoryAndMonth(User user, Category category, YearMonth month);

    /**
     * Check if a budget exists for a specific user, category, and month.
     *
     * @param user the user
     * @param category the category
     * @param month the month
     * @return true if the budget exists, false otherwise
     */
    boolean existsByUserAndCategoryAndMonth(User user, Category category, YearMonth month);

    /**
     * Calculate the total budget amount for a specific user and month.
     *
     * @param user the user
     * @param month the month
     * @return the total budget amount
     */
    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Budget b WHERE b.user = ?1 AND b.month = ?2")
    BigDecimal sumAmountByUserAndMonth(User user, YearMonth month);

    /**
     * Find all distinct months for which a user has budgets, ordered by month.
     *
     * @param user the user
     * @return list of distinct months
     */
    @Query("SELECT DISTINCT b.month FROM Budget b WHERE b.user = ?1 ORDER BY b.month")
    List<YearMonth> findDistinctMonthsByUserOrderByMonth(User user);

    /**
     * Delete all budgets for a specific user, category, and month.
     *
     * @param user the user
     * @param category the category
     * @param month the month
     */
    void deleteByUserAndCategoryAndMonth(User user, Category category, YearMonth month);

    /**
     * Delete all budgets for a specific user and month.
     *
     * @param user the user
     * @param month the month
     */
    void deleteByUserAndMonth(User user, YearMonth month);
}