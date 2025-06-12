package com.fintrack.personalfinancemanager.service;

import com.fintrack.personalfinancemanager.model.Transaction;
import com.fintrack.personalfinancemanager.model.Transaction.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Transaction operations.
 */
public interface TransactionService {

    /**
     * Create a new transaction.
     *
     * @param transaction the transaction to create
     * @param userId the user ID
     * @return the created transaction
     */
    Transaction createTransaction(Transaction transaction, Long userId);

    /**
     * Update an existing transaction.
     *
     * @param transaction the transaction with updated information
     * @param userId the user ID
     * @return the updated transaction
     */
    Transaction updateTransaction(Transaction transaction, Long userId);

    /**
     * Delete a transaction by ID.
     *
     * @param transactionId the transaction ID
     * @param userId the user ID
     */
    void deleteTransaction(Long transactionId, Long userId);

    /**
     * Find a transaction by ID.
     *
     * @param transactionId the transaction ID
     * @param userId the user ID
     * @return the transaction if found
     */
    Transaction findById(Long transactionId, Long userId);

    /**
     * Find all transactions for a user.
     *
     * @param userId the user ID
     * @return list of transactions
     */
    List<Transaction> findAllByUser(Long userId);

    /**
     * Find transactions for a user with optional filtering.
     *
     * @param userId the user ID
     * @param type the transaction type (optional)
     * @param categoryId the category ID (optional)
     * @param startDate the start date (optional)
     * @param endDate the end date (optional)
     * @return list of filtered transactions
     */
    List<Transaction> findTransactions(Long userId, TransactionType type, Long categoryId, LocalDate startDate, LocalDate endDate);

    /**
     * Calculate the total income for a user.
     *
     * @param userId the user ID
     * @return the total income
     */
    BigDecimal calculateTotalIncome(Long userId);

    /**
     * Calculate the total expenses for a user.
     *
     * @param userId the user ID
     * @return the total expenses
     */
    BigDecimal calculateTotalExpenses(Long userId);

    /**
     * Calculate the total income for a user within a date range.
     *
     * @param userId the user ID
     * @param startDate the start date
     * @param endDate the end date
     * @return the total income within the date range
     */
    BigDecimal calculateTotalIncomeForPeriod(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Calculate the total expenses for a user within a date range.
     *
     * @param userId the user ID
     * @param startDate the start date
     * @param endDate the end date
     * @return the total expenses within the date range
     */
    BigDecimal calculateTotalExpensesForPeriod(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Calculate the total expenses for a user by category.
     *
     * @param userId the user ID
     * @param categoryId the category ID
     * @return the total expenses for the category
     */
    BigDecimal calculateTotalExpensesByCategory(Long userId, Long categoryId);

    /**
     * Calculate the total expenses for a user by category within a date range.
     *
     * @param userId the user ID
     * @param categoryId the category ID
     * @param startDate the start date
     * @param endDate the end date
     * @return the total expenses for the category within the date range
     */
    BigDecimal calculateTotalExpensesByCategoryForPeriod(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate);

    /**
     * Get recent transactions for a user.
     *
     * @param userId the user ID
     * @param limit the maximum number of transactions to return
     * @return list of recent transactions
     */
    List<Transaction> getRecentTransactions(Long userId, int limit);
}