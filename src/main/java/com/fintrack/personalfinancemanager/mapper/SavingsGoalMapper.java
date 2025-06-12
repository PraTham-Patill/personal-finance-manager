package com.fintrack.personalfinancemanager.mapper;

import com.fintrack.personalfinancemanager.dto.SavingsGoalDTO;
import com.fintrack.personalfinancemanager.model.SavingsGoal;
import com.fintrack.personalfinancemanager.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to convert between SavingsGoal entity and DTOs.
 */
@Component
public class SavingsGoalMapper {

    /**
     * Convert SavingsGoal entity to SavingsGoalDTO.
     *
     * @param savingsGoal the savings goal entity
     * @return the savings goal DTO
     */
    public SavingsGoalDTO toDTO(SavingsGoal savingsGoal) {
        if (savingsGoal == null) {
            return null;
        }
        
        SavingsGoalDTO dto = SavingsGoalDTO.builder()
                .id(savingsGoal.getId())
                .name(savingsGoal.getName())
                .targetAmount(savingsGoal.getTargetAmount())
                .targetDate(savingsGoal.getTargetDate())
                .startDate(savingsGoal.getStartDate())
                .currentAmount(savingsGoal.getCurrentAmount())
                .build();
        
        // Calculate additional fields
        dto.setPercentageComplete(savingsGoal.getPercentageComplete());
        dto.setRemainingAmount(savingsGoal.getRemainingAmount());
        
        // Calculate days remaining
        LocalDate now = LocalDate.now();
        if (savingsGoal.getTargetDate().isAfter(now)) {
            dto.setDaysRemaining(ChronoUnit.DAYS.between(now, savingsGoal.getTargetDate()));
        } else {
            dto.setDaysRemaining(0);
        }
        
        // Check if completed
        dto.setCompleted(savingsGoal.getCurrentAmount().compareTo(savingsGoal.getTargetAmount()) >= 0);
        
        return dto;
    }

    /**
     * Convert a list of SavingsGoal entities to a list of SavingsGoalDTOs.
     *
     * @param savingsGoals the list of savings goal entities
     * @return the list of savings goal DTOs
     */
    public List<SavingsGoalDTO> toDTOList(List<SavingsGoal> savingsGoals) {
        if (savingsGoals == null) {
            return null;
        }
        
        return savingsGoals.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert SavingsGoalDTO to SavingsGoal entity.
     *
     * @param dto the savings goal DTO
     * @param user the user entity
     * @return the savings goal entity
     */
    public SavingsGoal toEntity(SavingsGoalDTO dto, User user) {
        if (dto == null) {
            return null;
        }
        
        SavingsGoal savingsGoal = new SavingsGoal();
        savingsGoal.setId(dto.getId());
        savingsGoal.setName(dto.getName());
        savingsGoal.setTargetAmount(dto.getTargetAmount());
        savingsGoal.setTargetDate(dto.getTargetDate());
        
        // Set start date to now if it's a new goal
        if (dto.getStartDate() == null) {
            savingsGoal.setStartDate(LocalDate.now());
        } else {
            savingsGoal.setStartDate(dto.getStartDate());
        }
        
        // Set current amount to 0 if it's a new goal
        if (dto.getCurrentAmount() == null) {
            savingsGoal.setCurrentAmount(java.math.BigDecimal.ZERO);
        } else {
            savingsGoal.setCurrentAmount(dto.getCurrentAmount());
        }
        
        savingsGoal.setUser(user);
        
        return savingsGoal;
    }

    /**
     * Update SavingsGoal entity from SavingsGoalDTO.
     *
     * @param savingsGoal the savings goal entity to update
     * @param dto the savings goal DTO with updated values
     * @return the updated savings goal entity
     */
    public SavingsGoal updateEntityFromDTO(SavingsGoal savingsGoal, SavingsGoalDTO dto) {
        if (savingsGoal == null || dto == null) {
            return savingsGoal;
        }
        
        savingsGoal.setName(dto.getName());
        savingsGoal.setTargetAmount(dto.getTargetAmount());
        savingsGoal.setTargetDate(dto.getTargetDate());
        
        // Don't update currentAmount from DTO for security reasons
        // This should be done through a dedicated service method
        
        return savingsGoal;
    }
}