package com.fintrack.personalfinancemanager.repository;

import com.fintrack.personalfinancemanager.model.SavingsGoal;
import com.fintrack.personalfinancemanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for SavingsGoal entity operations.
 */
@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {

    /**
     * Find all savings goals for a specific user.
     *
     * @param user the user
     * @return list of savings goals
     */
    List<SavingsGoal> findByUser(User user);

    /**
     * Find all savings goals for a specific user ordered by target date.
     *
     * @param user the user
     * @return list of savings goals ordered by target date
     */
    List<SavingsGoal> findByUserOrderByTargetDate(User user);

    /**
     * Find all active savings goals for a specific user (where target date is in the future).
     *
     * @param user the user
     * @param currentDate the current date
     * @return list of active savings goals
     */
    List<SavingsGoal> findByUserAndTargetDateAfter(User user, LocalDate currentDate);

    /**
     * Find all completed savings goals for a specific user (where current amount >= target amount).
     *
     * @param user the user
     * @return list of completed savings goals
     */
    @Query("SELECT g FROM SavingsGoal g WHERE g.user = ?1 AND g.currentAmount >= g.targetAmount")
    List<SavingsGoal> findCompletedGoalsByUser(User user);

    /**
     * Calculate the total amount saved across all savings goals for a specific user.
     *
     * @param user the user
     * @return the total amount saved
     */
    @Query("SELECT COALESCE(SUM(g.currentAmount), 0) FROM SavingsGoal g WHERE g.user = ?1")
    BigDecimal sumCurrentAmountByUser(User user);

    /**
     * Calculate the total target amount across all savings goals for a specific user.
     *
     * @param user the user
     * @return the total target amount
     */
    @Query("SELECT COALESCE(SUM(g.targetAmount), 0) FROM SavingsGoal g WHERE g.user = ?1")
    BigDecimal sumTargetAmountByUser(User user);

    /**
     * Find a savings goal by name and user.
     *
     * @param name the savings goal name
     * @param user the user
     * @return the savings goal if found
     */
    SavingsGoal findByNameAndUser(String name, User user);

    /**
     * Check if a savings goal with the given name exists for a user.
     *
     * @param name the savings goal name
     * @param user the user
     * @return true if the savings goal exists, false otherwise
     */
    boolean existsByNameAndUser(String name, User user);
}