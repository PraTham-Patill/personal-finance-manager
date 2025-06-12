package com.fintrack.personalfinancemanager.service.impl;

import com.fintrack.personalfinancemanager.model.Budget;
import com.fintrack.personalfinancemanager.model.Category;
import com.fintrack.personalfinancemanager.model.Transaction;
import com.fintrack.personalfinancemanager.model.Transaction.TransactionType;
import com.fintrack.personalfinancemanager.model.User;
import com.fintrack.personalfinancemanager.repository.BudgetRepository;
import com.fintrack.personalfinancemanager.repository.CategoryRepository;
import com.fintrack.personalfinancemanager.repository.TransactionRepository;
import com.fintrack.personalfinancemanager.repository.UserRepository;
import com.fintrack.personalfinancemanager.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * Implementation of the BudgetService interface.
 */
@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public Budget createBudget(Budget budget, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Category category = categoryRepository.findById(budget.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        // Verify the category belongs to the user
        if (!category.getUser().getId().equals(userId) && !category.isDefault()) {
            throw new IllegalArgumentException("Category does not belong to the user");
        }

        // Check if a budget for the same category and month already exists
        if (budgetRepository.existsByUserAndCategoryAndMonth(user, category, budget.getMonth())) {
            throw new IllegalArgumentException("A budget for this category and month already exists");
        }

        budget.setUser(user);
        budget.setCategory(category);

        return budgetRepository.save(budget);
    }

    @Override
    @Transactional
    public Budget updateBudget(Budget budget, Long userId) {
        Budget existingBudget = budgetRepository.findById(budget.getId())
                .orElseThrow(() -> new IllegalArgumentException("Budget not found"));

        // Verify the budget belongs to the user
        if (!existingBudget.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Budget does not belong to the user");
        }

        // Update fields
        existingBudget.setAmount(budget.getAmount());

        return budgetRepository.save(existingBudget);
    }

    @Override
    @Transactional
    public void deleteBudget(Long budgetId, Long userId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found"));

        // Verify the budget belongs to the user
        if (!budget.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Budget does not belong to the user");
        }

        budgetRepository.delete(budget);
    }

    @Override
    public Budget findById(Long budgetId, Long userId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found"));

        // Verify the budget belongs to the user
        if (!budget.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Budget does not belong to the user");
        }

        return budget;
    }

    @Override
    public List<Budget> findAllByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return budgetRepository.findByUser(user);
    }

    @Override
    public List<Budget> findByUserAndMonth(Long userId, YearMonth month) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return budgetRepository.findByUserAndMonth(user, month);
    }

    @Override
    public Budget findByUserAndCategoryAndMonth(Long userId, Long categoryId, YearMonth month) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        return budgetRepository.findByUserAndCategoryAndMonth(user, category, month);
    }

    @Override
    public BigDecimal calculateTotalBudgetForMonth(Long userId, YearMonth month) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return budgetRepository.sumAmountByUserAndMonth(user, month);
    }

    @Override
    public double calculateBudgetPercentageSpent(Long budgetId, Long userId) {
        Budget budget = findById(budgetId, userId);
        BigDecimal spent = calculateSpentAmount(budget);
        return budget.calculatePercentageSpent(spent);
    }

    @Override
    public BigDecimal calculateBudgetRemainingAmount(Long budgetId, Long userId) {
        Budget budget = findById(budgetId, userId);
        BigDecimal spent = calculateSpentAmount(budget);
        return budget.calculateRemainingAmount(spent);
    }

    @Override
    public List<YearMonth> findDistinctMonthsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return budgetRepository.findDistinctMonthsByUserOrderByMonth(user);
    }

    @Override
    @Transactional
    public void deleteAllByUserAndMonth(Long userId, YearMonth month) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        budgetRepository.deleteByUserAndMonth(user, month);
    }

    /**
     * Calculate the amount spent for a budget.
     *
     * @param budget the budget
     * @return the amount spent
     */
    private BigDecimal calculateSpentAmount(Budget budget) {
        LocalDate startDate = budget.getMonth().atDay(1);
        LocalDate endDate = budget.getMonth().atEndOfMonth();

        return transactionRepository.sumAmountByUserAndTypeAndCategoryAndDateBetween(
                budget.getUser(), TransactionType.EXPENSE, budget.getCategory(), startDate, endDate);
    }
}