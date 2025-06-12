package com.fintrack.personalfinancemanager.service.impl;

import com.fintrack.personalfinancemanager.model.Category;
import com.fintrack.personalfinancemanager.model.Transaction;
import com.fintrack.personalfinancemanager.model.Transaction.TransactionType;
import com.fintrack.personalfinancemanager.model.User;
import com.fintrack.personalfinancemanager.repository.CategoryRepository;
import com.fintrack.personalfinancemanager.repository.TransactionRepository;
import com.fintrack.personalfinancemanager.repository.UserRepository;
import com.fintrack.personalfinancemanager.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of the TransactionService interface.
 */
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Transaction createTransaction(Transaction transaction, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Category category = categoryRepository.findById(transaction.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        // Verify the category belongs to the user
        if (!category.getUser().getId().equals(userId) && !category.isDefault()) {
            throw new IllegalArgumentException("Category does not belong to the user");
        }

        transaction.setUser(user);
        transaction.setCategory(category);

        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public Transaction updateTransaction(Transaction transaction, Long userId) {
        Transaction existingTransaction = transactionRepository.findById(transaction.getId())
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        // Verify the transaction belongs to the user
        if (!existingTransaction.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Transaction does not belong to the user");
        }

        Category category = categoryRepository.findById(transaction.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        // Verify the category belongs to the user
        if (!category.getUser().getId().equals(userId) && !category.isDefault()) {
            throw new IllegalArgumentException("Category does not belong to the user");
        }

        // Update fields
        existingTransaction.setAmount(transaction.getAmount());
        existingTransaction.setDate(transaction.getDate());
        existingTransaction.setDescription(transaction.getDescription());
        existingTransaction.setNotes(transaction.getNotes());
        existingTransaction.setType(transaction.getType());
        existingTransaction.setCategory(category);

        return transactionRepository.save(existingTransaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        // Verify the transaction belongs to the user
        if (!transaction.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Transaction does not belong to the user");
        }

        transactionRepository.delete(transaction);
    }

    @Override
    public Transaction findById(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        // Verify the transaction belongs to the user
        if (!transaction.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Transaction does not belong to the user");
        }

        return transaction;
    }

    @Override
    public List<Transaction> findAllByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return transactionRepository.findByUser(user);
    }

    @Override
    public List<Transaction> findTransactions(Long userId, TransactionType type, Long categoryId, 
                                             LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Apply filters based on provided parameters
        if (type != null && categoryId != null && startDate != null && endDate != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            return transactionRepository.findByUserAndTypeAndCategoryAndDateBetween(user, type, category, startDate, endDate);
        } else if (type != null && categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            return transactionRepository.findByUserAndTypeAndCategory(user, type, category);
        } else if (type != null && startDate != null && endDate != null) {
            return transactionRepository.findByUserAndTypeAndDateBetween(user, type, startDate, endDate);
        } else if (categoryId != null && startDate != null && endDate != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            return transactionRepository.findByUserAndCategoryAndDateBetween(user, category, startDate, endDate);
        } else if (startDate != null && endDate != null) {
            return transactionRepository.findByUserAndDateBetween(user, startDate, endDate);
        } else if (type != null) {
            return transactionRepository.findByUserAndType(user, type);
        } else if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            return transactionRepository.findByUserAndCategory(user, category);
        } else {
            return transactionRepository.findByUser(user);
        }
    }

    @Override
    public BigDecimal calculateTotalIncome(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return transactionRepository.sumAmountByUserAndType(user, TransactionType.INCOME);
    }

    @Override
    public BigDecimal calculateTotalExpenses(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return transactionRepository.sumAmountByUserAndType(user, TransactionType.EXPENSE);
    }

    @Override
    public BigDecimal calculateTotalIncomeForPeriod(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return transactionRepository.sumAmountByUserAndTypeAndDateBetween(user, TransactionType.INCOME, startDate, endDate);
    }

    @Override
    public BigDecimal calculateTotalExpensesForPeriod(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return transactionRepository.sumAmountByUserAndTypeAndDateBetween(user, TransactionType.EXPENSE, startDate, endDate);
    }

    @Override
    public BigDecimal calculateTotalExpensesByCategory(Long userId, Long categoryId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        return transactionRepository.sumAmountByUserAndTypeAndCategory(user, TransactionType.EXPENSE, category);
    }

    @Override
    public BigDecimal calculateTotalExpensesByCategoryForPeriod(Long userId, Long categoryId, 
                                                              LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        return transactionRepository.sumAmountByUserAndTypeAndCategoryAndDateBetween(
                user, TransactionType.EXPENSE, category, startDate, endDate);
    }

    @Override
    public List<Transaction> getRecentTransactions(Long userId, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return transactionRepository.findByUser(user, 
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "date"))).getContent();
    }
}