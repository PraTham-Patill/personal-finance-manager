package com.fintrack.personalfinancemanager.controller;

import com.fintrack.personalfinancemanager.dto.ApiResponse;
import com.fintrack.personalfinancemanager.dto.SavingsGoalDTO;
import com.fintrack.personalfinancemanager.mapper.SavingsGoalMapper;
import com.fintrack.personalfinancemanager.model.SavingsGoal;
import com.fintrack.personalfinancemanager.service.SavingsGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Controller for savings goal management operations.
 */
@RestController
@RequestMapping("/api/savings-goals")
@RequiredArgsConstructor
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;
    private final SavingsGoalMapper savingsGoalMapper;

    /**
     * Create a new savings goal.
     *
     * @param userId the ID of the authenticated user
     * @param savingsGoalDTO the savings goal data
     * @return the response entity
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SavingsGoalDTO>> createSavingsGoal(
            @RequestParam Long userId,
            @Valid @RequestBody SavingsGoalDTO savingsGoalDTO) {
        SavingsGoal savingsGoal = savingsGoalService.createSavingsGoal(userId, savingsGoalDTO);
        SavingsGoalDTO createdSavingsGoalDTO = savingsGoalMapper.toDTO(savingsGoal);
        return new ResponseEntity<>(ApiResponse.success(createdSavingsGoalDTO, "Savings goal created successfully"), 
                HttpStatus.CREATED);
    }

    /**
     * Get a savings goal by ID.
     *
     * @param userId the ID of the authenticated user
     * @param goalId the ID of the savings goal
     * @return the response entity
     */
    @GetMapping("/{goalId}")
    public ResponseEntity<ApiResponse<SavingsGoalDTO>> getSavingsGoal(
            @RequestParam Long userId,
            @PathVariable Long goalId) {
        SavingsGoal savingsGoal = savingsGoalService.findSavingsGoalByIdAndUser(goalId, userId);
        SavingsGoalDTO savingsGoalDTO = savingsGoalMapper.toDTO(savingsGoal);
        return ResponseEntity.ok(ApiResponse.success(savingsGoalDTO));
    }

    /**
     * Update a savings goal.
     *
     * @param userId the ID of the authenticated user
     * @param goalId the ID of the savings goal
     * @param savingsGoalDTO the updated savings goal data
     * @return the response entity
     */
    @PutMapping("/{goalId}")
    public ResponseEntity<ApiResponse<SavingsGoalDTO>> updateSavingsGoal(
            @RequestParam Long userId,
            @PathVariable Long goalId,
            @Valid @RequestBody SavingsGoalDTO savingsGoalDTO) {
        SavingsGoal savingsGoal = savingsGoalService.updateSavingsGoal(userId, goalId, savingsGoalDTO);
        SavingsGoalDTO updatedSavingsGoalDTO = savingsGoalMapper.toDTO(savingsGoal);
        return ResponseEntity.ok(ApiResponse.success(updatedSavingsGoalDTO, "Savings goal updated successfully"));
    }

    /**
     * Delete a savings goal.
     *
     * @param userId the ID of the authenticated user
     * @param goalId the ID of the savings goal
     * @return the response entity
     */
    @DeleteMapping("/{goalId}")
    public ResponseEntity<ApiResponse<Void>> deleteSavingsGoal(
            @RequestParam Long userId,
            @PathVariable Long goalId) {
        savingsGoalService.deleteSavingsGoal(userId, goalId);
        return ResponseEntity.ok(ApiResponse.success("Savings goal deleted successfully"));
    }

    /**
     * Get all savings goals for a user.
     *
     * @param userId the ID of the authenticated user
     * @param active filter for active goals (null for all goals)
     * @return the response entity
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SavingsGoalDTO>>> getAllSavingsGoals(
            @RequestParam Long userId,
            @RequestParam(required = false) Boolean active) {
        List<SavingsGoal> savingsGoals;
        
        if (active == null) {
            savingsGoals = savingsGoalService.findAllSavingsGoalsByUser(userId);
        } else if (active) {
            savingsGoals = savingsGoalService.findActiveSavingsGoalsByUser(userId);
        } else {
            savingsGoals = savingsGoalService.findCompletedSavingsGoalsByUser(userId);
        }
        
        List<SavingsGoalDTO> savingsGoalDTOs = savingsGoalMapper.toDTOList(savingsGoals);
        return ResponseEntity.ok(ApiResponse.success(savingsGoalDTOs));
    }

    /**
     * Add funds to a savings goal.
     *
     * @param userId the ID of the authenticated user
     * @param goalId the ID of the savings goal
     * @param amount the amount to add
     * @return the response entity
     */
    @PostMapping("/{goalId}/add-funds")
    public ResponseEntity<ApiResponse<SavingsGoalDTO>> addFundsToSavingsGoal(
            @RequestParam Long userId,
            @PathVariable Long goalId,
            @RequestParam BigDecimal amount) {
        SavingsGoal savingsGoal = savingsGoalService.addFundsToSavingsGoal(userId, goalId, amount);
        SavingsGoalDTO savingsGoalDTO = savingsGoalMapper.toDTO(savingsGoal);
        return ResponseEntity.ok(ApiResponse.success(savingsGoalDTO, "Funds added successfully"));
    }

    /**
     * Get savings goals summary for a user.
     *
     * @param userId the ID of the authenticated user
     * @return the response entity
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSavingsGoalsSummary(@RequestParam Long userId) {
        BigDecimal totalSaved = savingsGoalService.calculateTotalSavedAmount(userId);
        BigDecimal totalTarget = savingsGoalService.calculateTotalTargetAmount(userId);
        double overallProgress = savingsGoalService.calculateOverallProgress(userId);
        
        Map<String, Object> summary = Map.of(
                "totalSaved", totalSaved,
                "totalTarget", totalTarget,
                "overallProgress", overallProgress,
                "activeGoalsCount", savingsGoalService.findActiveSavingsGoalsByUser(userId).size(),
                "completedGoalsCount", savingsGoalService.findCompletedSavingsGoalsByUser(userId).size()
        );
        
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}