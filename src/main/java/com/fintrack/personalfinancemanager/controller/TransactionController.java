package com.fintrack.personalfinancemanager.controller;

import com.fintrack.personalfinancemanager.dto.ApiResponse;
import com.fintrack.personalfinancemanager.dto.TransactionDTO;
import com.fintrack.personalfinancemanager.mapper.TransactionMapper;
import com.fintrack.personalfinancemanager.model.Transaction;
import com.fintrack.personalfinancemanager.model.TransactionType;
import com.fintrack.personalfinancemanager.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller for transaction management operations.
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    /**
     * Create a new transaction.
     *
     * @param userId the ID of the authenticated user
     * @param transactionDTO the transaction data
     * @return the response entity
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TransactionDTO>> createTransaction(
            @RequestParam Long userId,
            @Valid @RequestBody TransactionDTO transactionDTO) {
        Transaction transaction = transactionService.createTransaction(userId, transactionDTO);
        TransactionDTO createdTransactionDTO = transactionMapper.toDTO(transaction);
        return new ResponseEntity<>(ApiResponse.success(createdTransactionDTO, "Transaction created successfully"), 
                HttpStatus.CREATED);
    }

    /**
     * Get a transaction by ID.
     *
     * @param userId the ID of the authenticated user
     * @param transactionId the ID of the transaction
     * @return the response entity
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionDTO>> getTransaction(
            @RequestParam Long userId,
            @PathVariable Long transactionId) {
        Transaction transaction = transactionService.findTransactionByIdAndUser(transactionId, userId);
        TransactionDTO transactionDTO = transactionMapper.toDTO(transaction);
        return ResponseEntity.ok(ApiResponse.success(transactionDTO));
    }

    /**
     * Update a transaction.
     *
     * @param userId the ID of the authenticated user
     * @param transactionId the ID of the transaction
     * @param transactionDTO the updated transaction data
     * @return the response entity
     */
    @PutMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionDTO>> updateTransaction(
            @RequestParam Long userId,
            @PathVariable Long transactionId,
            @Valid @RequestBody TransactionDTO transactionDTO) {
        Transaction transaction = transactionService.updateTransaction(userId, transactionId, transactionDTO);
        TransactionDTO updatedTransactionDTO = transactionMapper.toDTO(transaction);
        return ResponseEntity.ok(ApiResponse.success(updatedTransactionDTO, "Transaction updated successfully"));
    }

    /**
     * Delete a transaction.
     *
     * @param userId the ID of the authenticated user
     * @param transactionId the ID of the transaction
     * @return the response entity
     */
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(
            @RequestParam Long userId,
            @PathVariable Long transactionId) {
        transactionService.deleteTransaction(userId, transactionId);
        return ResponseEntity.ok(ApiResponse.success("Transaction deleted successfully"));
    }

    /**
     * Get all transactions for a user with optional filtering.
     *
     * @param userId the ID of the authenticated user
     * @param type the transaction type filter
     * @param categoryId the category ID filter
     * @param startDate the start date filter
     * @param endDate the end date filter
     * @return the response entity
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getAllTransactions(
            @RequestParam Long userId,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Transaction> transactions = transactionService.findTransactionsWithFilters(
                userId, type, categoryId, startDate, endDate);
        List<TransactionDTO> transactionDTOs = transactionMapper.toDTOList(transactions);
        return ResponseEntity.ok(ApiResponse.success(transactionDTOs));
    }

    /**
     * Get recent transactions for a user.
     *
     * @param userId the ID of the authenticated user
     * @param limit the maximum number of transactions to return
     * @return the response entity
     */
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getRecentTransactions(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "5") int limit) {
        List<Transaction> transactions = transactionService.getRecentTransactions(userId, limit);
        List<TransactionDTO> transactionDTOs = transactionMapper.toDTOList(transactions);
        return ResponseEntity.ok(ApiResponse.success(transactionDTOs));
    }

    /**
     * Get total income and expenses for a user.
     *
     * @param userId the ID of the authenticated user
     * @return the response entity
     */
    @GetMapping("/totals")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getTotals(@RequestParam Long userId) {
        BigDecimal totalIncome = transactionService.calculateTotalIncome(userId);
        BigDecimal totalExpenses = transactionService.calculateTotalExpenses(userId);
        
        Map<String, BigDecimal> totals = Map.of(
                "totalIncome", totalIncome,
                "totalExpenses", totalExpenses,
                "balance", totalIncome.subtract(totalExpenses)
        );
        
        return ResponseEntity.ok(ApiResponse.success(totals));
    }

    /**
     * Get total income and expenses for a user within a date range.
     *
     * @param userId the ID of the authenticated user
     * @param startDate the start date
     * @param endDate the end date
     * @return the response entity
     */
    @GetMapping("/totals/period")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getTotalsForPeriod(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        BigDecimal totalIncome = transactionService.calculateTotalIncomeForPeriod(userId, startDate, endDate);
        BigDecimal totalExpenses = transactionService.calculateTotalExpensesForPeriod(userId, startDate, endDate);
        
        Map<String, BigDecimal> totals = Map.of(
                "totalIncome", totalIncome,
                "totalExpenses", totalExpenses,
                "balance", totalIncome.subtract(totalExpenses)
        );
        
        return ResponseEntity.ok(ApiResponse.success(totals));
    }

    /**
     * Get total expenses by category for a user.
     *
     * @param userId the ID of the authenticated user
     * @return the response entity
     */
    @GetMapping("/expenses/by-category")
    public ResponseEntity<ApiResponse<Map<Long, BigDecimal>>> getExpensesByCategory(@RequestParam Long userId) {
        Map<Long, BigDecimal> expensesByCategory = transactionService.calculateTotalExpensesByCategory(userId);
        return ResponseEntity.ok(ApiResponse.success(expensesByCategory));
    }

    /**
     * Get total expenses by category for a user within a date range.
     *
     * @param userId the ID of the authenticated user
     * @param startDate the start date
     * @param endDate the end date
     * @return the response entity
     */
    @GetMapping("/expenses/by-category/period")
    public ResponseEntity<ApiResponse<Map<Long, BigDecimal>>> getExpensesByCategoryForPeriod(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<Long, BigDecimal> expensesByCategory = 
                transactionService.calculateTotalExpensesByCategoryForPeriod(userId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(expensesByCategory));
    }
}