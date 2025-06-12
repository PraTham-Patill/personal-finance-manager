package com.fintrack.personalfinancemanager.repository;

import com.fintrack.personalfinancemanager.model.Category;
import com.fintrack.personalfinancemanager.model.Transaction;
import com.fintrack.personalfinancemanager.model.Transaction.TransactionType;
import com.fintrack.personalfinancemanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Transaction entity operations.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Find all transactions for a specific user.
     *
     * @param user the user
     * @return list of transactions
     */
    List<Transaction> findByUser(User user);

    /**
     * Find all transactions for a specific user and transaction type.
     *
     * @param user the user
     * @param type the transaction type
     * @return list of transactions
     */
    List<Transaction> findByUserAndType(User user, TransactionType type);

    /**
     * Find all transactions for a specific user and category.
     *
     * @param user     the user
     * @param category the category
     * @return list of transactions
     */
    List<Transaction> findByUserAndCategory(User user, Category category);

    /**
     * Find all transactions for a specific user within a date range.
     *
     * @param user      the user
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of transactions
     */
    List<Transaction> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);

    /**
     * Find all transactions for a specific user, transaction type, and within a date range.
     *
     * @param user      the user
     * @param type      the transaction type
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of transactions
     */
    List<Transaction> findByUserAndTypeAndDateBetween(User user, TransactionType type, LocalDate startDate, LocalDate endDate);

    /**
     * Find all transactions for a specific user, category, and within a date range.
     *
     * @param user      the user
     * @param category  the category
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of transactions
     */
    List<Transaction> findByUserAndCategoryAndDateBetween(User user, Category category, LocalDate startDate, LocalDate endDate);

    /**
     * Find all transactions for a specific user, transaction type, and category.
     *
     * @param user     the user
     * @param type     the transaction type
     * @param category the category
     * @return list of transactions
     */
    List<Transaction> findByUserAndTypeAndCategory(User user, TransactionType type, Category category);

    /**
     * Find all transactions for a specific user, transaction type, category, and within a date range.
     *
     * @param user      the user
     * @param type      the transaction type
     * @param category  the category
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of transactions
     */
    List<Transaction> findByUserAndTypeAndCategoryAndDateBetween(User user, TransactionType type, Category category, LocalDate startDate, LocalDate endDate);

    /**
     * Calculate the sum of transaction amounts for a specific user and transaction type.
     *
     * @param user the user
     * @param type the transaction type
     * @return the sum of transaction amounts
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user = ?1 AND t.type = ?2")
    BigDecimal sumAmountByUserAndType(User user, TransactionType type);

    /**
     * Calculate the sum of transaction amounts for a specific user, transaction type, and within a date range.
     *
     * @param user      the user
     * @param type      the transaction type
     * @param startDate the start date
     * @param endDate   the end date
     * @return the sum of transaction amounts
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user = ?1 AND t.type = ?2 AND t.date BETWEEN ?3 AND ?4")
    BigDecimal sumAmountByUserAndTypeAndDateBetween(User user, TransactionType type, LocalDate startDate, LocalDate endDate);

    /**
     * Calculate the sum of transaction amounts for a specific user, transaction type, and category.
     *
     * @param user     the user
     * @param type     the transaction type
     * @param category the category
     * @return the sum of transaction amounts
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user = ?1 AND t.type = ?2 AND t.category = ?3")
    BigDecimal sumAmountByUserAndTypeAndCategory(User user, TransactionType type, Category category);

    /**
     * Calculate the sum of transaction amounts for a specific user, transaction type, category, and within a date range.
     *
     * @param user      the user
     * @param type      the transaction type
     * @param category  the category
     * @param startDate the start date
     * @param endDate   the end date
     * @return the sum of transaction amounts
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user = ?1 AND t.type = ?2 AND t.category = ?3 AND t.date BETWEEN ?4 AND ?5")
    BigDecimal sumAmountByUserAndTypeAndCategoryAndDateBetween(User user, TransactionType type, Category category, LocalDate startDate, LocalDate endDate);
}