package com.fintrack.personalfinancemanager.controller;

import com.fintrack.personalfinancemanager.dto.ApiResponse;
import com.fintrack.personalfinancemanager.dto.BudgetDTO;
import com.fintrack.personalfinancemanager.mapper.BudgetMapper;
import com.fintrack.personalfinancemanager.model.Budget;
import com.fintrack.personalfinancemanager.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

/**
 * Controller for budget management operations.
 */
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;
    private final BudgetMapper budgetMapper;

    /**
     * Create a new budget.
     *
     * @param userId the ID of the authenticated user
     * @param budgetDTO the budget data
     * @return the response entity
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BudgetDTO>> createBudget(
            @RequestParam Long userId,
            @Valid @RequestBody BudgetDTO budgetDTO) {
        Budget budget = budgetService.createBudget(userId, budgetDTO);
        BudgetDTO createdBudgetDTO = budgetMapper.toDTO(budget);
        return new ResponseEntity<>(ApiResponse.success(createdBudgetDTO, "Budget created successfully"), 
                HttpStatus.CREATED);
    }

    /**
     * Get a budget by ID.
     *
     * @param userId the ID of the authenticated user
     * @param budgetId the ID of the budget
     * @return the response entity
     */
    @GetMapping("/{budgetId}")
    public ResponseEntity<ApiResponse<BudgetDTO>> getBudget(
            @RequestParam Long userId,
            @PathVariable Long budgetId) {
        Budget budget = budgetService.findBudgetByIdAndUser(budgetId, userId);
        BigDecimal spentAmount = budgetService.calculateSpentAmount(budget);
        BudgetDTO budgetDTO = budgetMapper.toDTOWithCalculatedFields(budget, spentAmount);
        return ResponseEntity.ok(ApiResponse.success(budgetDTO));
    }

    /**
     * Update a budget.
     *
     * @param userId the ID of the authenticated user
     * @param budgetId the ID of the budget
     * @param budgetDTO the updated budget data
     * @return the response entity
     */
    @PutMapping("/{budgetId}")
    public ResponseEntity<ApiResponse<BudgetDTO>> updateBudget(
            @RequestParam Long userId,
            @PathVariable Long budgetId,
            @Valid @RequestBody BudgetDTO budgetDTO) {
        Budget budget = budgetService.updateBudget(userId, budgetId, budgetDTO);
        BigDecimal spentAmount = budgetService.calculateSpentAmount(budget);
        BudgetDTO updatedBudgetDTO = budgetMapper.toDTOWithCalculatedFields(budget, spentAmount);
        return ResponseEntity.ok(ApiResponse.success(updatedBudgetDTO, "Budget updated successfully"));
    }

    /**
     * Delete a budget.
     *
     * @param userId the ID of the authenticated user
     * @param budgetId the ID of the budget
     * @return the response entity
     */
    @DeleteMapping("/{budgetId}")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(
            @RequestParam Long userId,
            @PathVariable Long budgetId) {
        budgetService.deleteBudget(userId, budgetId);
        return ResponseEntity.ok(ApiResponse.success("Budget deleted successfully"));
    }

    /**
     * Get all budgets for a user and month.
     *
     * @param userId the ID of the authenticated user
     * @param month the month in format YYYY-MM
     * @return the response entity
     */
    @GetMapping("/month/{month}")
    public ResponseEntity<ApiResponse<List<BudgetDTO>>> getBudgetsByMonth(
            @RequestParam Long userId,
            @PathVariable String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        List<Budget> budgets = budgetService.findBudgetsByUserAndMonth(userId, yearMonth);
        
        List<BudgetDTO> budgetDTOs = budgets.stream()
                .map(budget -> {
                    BigDecimal spentAmount = budgetService.calculateSpentAmount(budget);
                    return budgetMapper.toDTOWithCalculatedFields(budget, spentAmount);
                })
                .toList();
        
        return ResponseEntity.ok(ApiResponse.success(budgetDTOs));
    }

    /**
     * Get total budget amount for a user and month.
     *
     * @param userId the ID of the authenticated user
     * @param month the month in format YYYY-MM
     * @return the response entity
     */
    @GetMapping("/month/{month}/total")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalBudgetForMonth(
            @RequestParam Long userId,
            @PathVariable String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        BigDecimal totalBudget = budgetService.calculateTotalBudgetForMonth(userId, yearMonth);
        return ResponseEntity.ok(ApiResponse.success(totalBudget));
    }

    /**
     * Get all distinct budget months for a user.
     *
     * @param userId the ID of the authenticated user
     * @return the response entity
     */
    @GetMapping("/months")
    public ResponseEntity<ApiResponse<List<YearMonth>>> getDistinctBudgetMonths(@RequestParam Long userId) {
        List<YearMonth> months = budgetService.findDistinctBudgetMonthsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(months));
    }

    /**
     * Delete all budgets for a user and month.
     *
     * @param userId the ID of the authenticated user
     * @param month the month in format YYYY-MM
     * @return the response entity
     */
    @DeleteMapping("/month/{month}")
    public ResponseEntity<ApiResponse<Void>> deleteBudgetsByMonth(
            @RequestParam Long userId,
            @PathVariable String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        budgetService.deleteBudgetsByUserAndMonth(userId, yearMonth);
        return ResponseEntity.ok(ApiResponse.success("Budgets for month " + month + " deleted successfully"));
    }

    /**
     * Get budget summary for a user and month.
     *
     * @param userId the ID of the authenticated user
     * @param month the month in format YYYY-MM
     * @return the response entity
     */
    @GetMapping("/month/{month}/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBudgetSummaryForMonth(
            @RequestParam Long userId,
            @PathVariable String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        
        BigDecimal totalBudget = budgetService.calculateTotalBudgetForMonth(userId, yearMonth);
        BigDecimal totalSpent = budgetService.calculateTotalSpentForMonth(userId, yearMonth);
        BigDecimal remainingAmount = totalBudget.subtract(totalSpent);
        double percentageSpent = totalBudget.compareTo(BigDecimal.ZERO) > 0 ?
                totalSpent.multiply(new BigDecimal(100)).divide(totalBudget, 2, BigDecimal.ROUND_HALF_UP).doubleValue() : 0;
        
        Map<String, Object> summary = Map.of(
                "totalBudget", totalBudget,
                "totalSpent", totalSpent,
                "remainingAmount", remainingAmount,
                "percentageSpent", percentageSpent
        );
        
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}