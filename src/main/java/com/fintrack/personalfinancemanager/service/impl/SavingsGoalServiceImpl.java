package com.fintrack.personalfinancemanager.service.impl;

import com.fintrack.personalfinancemanager.model.SavingsGoal;
import com.fintrack.personalfinancemanager.model.User;
import com.fintrack.personalfinancemanager.repository.SavingsGoalRepository;
import com.fintrack.personalfinancemanager.repository.UserRepository;
import com.fintrack.personalfinancemanager.service.SavingsGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of the SavingsGoalService interface.
 */
@Service
@RequiredArgsConstructor
public class SavingsGoalServiceImpl implements SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SavingsGoal createSavingsGoal(SavingsGoal savingsGoal, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if a savings goal with the same name already exists for the user
        if (savingsGoalRepository.existsByNameAndUser(savingsGoal.getName(), user)) {
            throw new IllegalArgumentException("A savings goal with this name already exists");
        }

        savingsGoal.setUser(user);
        savingsGoal.setStartDate(LocalDate.now());
        savingsGoal.setCurrentAmount(BigDecimal.ZERO);

        return savingsGoalRepository.save(savingsGoal);
    }

    @Override
    @Transactional
    public SavingsGoal updateSavingsGoal(SavingsGoal savingsGoal, Long userId) {
        SavingsGoal existingGoal = savingsGoalRepository.findById(savingsGoal.getId())
                .orElseThrow(() -> new IllegalArgumentException("Savings goal not found"));

        // Verify the savings goal belongs to the user
        if (!existingGoal.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Savings goal does not belong to the user");
        }

        // Check if a different savings goal with the same name already exists for the user
        SavingsGoal goalWithSameName = savingsGoalRepository.findByNameAndUser(savingsGoal.getName(), existingGoal.getUser());
        if (goalWithSameName != null && !goalWithSameName.getId().equals(savingsGoal.getId())) {
            throw new IllegalArgumentException("A savings goal with this name already exists");
        }

        // Update fields
        existingGoal.setName(savingsGoal.getName());
        existingGoal.setTargetAmount(savingsGoal.getTargetAmount());
        existingGoal.setTargetDate(savingsGoal.getTargetDate());

        return savingsGoalRepository.save(existingGoal);
    }

    @Override
    @Transactional
    public void deleteSavingsGoal(Long savingsGoalId, Long userId) {
        SavingsGoal savingsGoal = savingsGoalRepository.findById(savingsGoalId)
                .orElseThrow(() -> new IllegalArgumentException("Savings goal not found"));

        // Verify the savings goal belongs to the user
        if (!savingsGoal.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Savings goal does not belong to the user");
        }

        savingsGoalRepository.delete(savingsGoal);
    }

    @Override
    public SavingsGoal findById(Long savingsGoalId, Long userId) {
        SavingsGoal savingsGoal = savingsGoalRepository.findById(savingsGoalId)
                .orElseThrow(() -> new IllegalArgumentException("Savings goal not found"));

        // Verify the savings goal belongs to the user
        if (!savingsGoal.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Savings goal does not belong to the user");
        }

        return savingsGoal;
    }

    @Override
    public List<SavingsGoal> findAllByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return savingsGoalRepository.findByUserOrderByTargetDate(user);
    }

    @Override
    public List<SavingsGoal> findActiveGoalsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return savingsGoalRepository.findByUserAndTargetDateAfter(user, LocalDate.now());
    }

    @Override
    public List<SavingsGoal> findCompletedGoalsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return savingsGoalRepository.findCompletedGoalsByUser(user);
    }

    @Override
    @Transactional
    public SavingsGoal addFunds(Long savingsGoalId, Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        SavingsGoal savingsGoal = findById(savingsGoalId, userId);
        savingsGoal.setCurrentAmount(savingsGoal.getCurrentAmount().add(amount));

        return savingsGoalRepository.save(savingsGoal);
    }

    @Override
    public BigDecimal calculateTotalSaved(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return savingsGoalRepository.sumCurrentAmountByUser(user);
    }

    @Override
    public BigDecimal calculateTotalTargetAmount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return savingsGoalRepository.sumTargetAmountByUser(user);
    }

    @Override
    public double calculateOverallProgress(Long userId) {
        BigDecimal totalSaved = calculateTotalSaved(userId);
        BigDecimal totalTarget = calculateTotalTargetAmount(userId);

        if (totalTarget.compareTo(BigDecimal.ZERO) == 0) {
            return totalSaved.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }

        return totalSaved.divide(totalTarget, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}