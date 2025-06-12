package com.fintrack.personalfinancemanager.mapper;

import com.fintrack.personalfinancemanager.dto.BudgetDTO;
import com.fintrack.personalfinancemanager.model.Budget;
import com.fintrack.personalfinancemanager.model.Category;
import com.fintrack.personalfinancemanager.model.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to convert between Budget entity and DTOs.
 */
@Component
public class BudgetMapper {

    /**
     * Convert Budget entity to BudgetDTO.
     *
     * @param budget the budget entity
     * @return the budget DTO
     */
    public BudgetDTO toDTO(Budget budget) {
        if (budget == null) {
            return null;
        }
        
        BudgetDTO dto = BudgetDTO.builder()
                .id(budget.getId())
                .amount(budget.getAmount())
                .month(budget.getMonth())
                .build();
        
        if (budget.getCategory() != null) {
            dto.setCategoryId(budget.getCategory().getId());
            dto.setCategoryName(budget.getCategory().getName());
        }
        
        return dto;
    }

    /**
     * Convert Budget entity to BudgetDTO with calculated fields.
     *
     * @param budget the budget entity
     * @param spentAmount the amount spent for this budget
     * @return the budget DTO with calculated fields
     */
    public BudgetDTO toDTOWithCalculatedFields(Budget budget, BigDecimal spentAmount) {
        BudgetDTO dto = toDTO(budget);
        if (dto != null) {
            dto.setSpentAmount(spentAmount);
            dto.setRemainingAmount(budget.getRemainingAmount(spentAmount));
            dto.setPercentageSpent(budget.getPercentageSpent(spentAmount));
        }
        return dto;
    }

    /**
     * Convert a list of Budget entities to a list of BudgetDTOs.
     *
     * @param budgets the list of budget entities
     * @return the list of budget DTOs
     */
    public List<BudgetDTO> toDTOList(List<Budget> budgets) {
        if (budgets == null) {
            return null;
        }
        
        return budgets.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert BudgetDTO to Budget entity.
     *
     * @param dto the budget DTO
     * @param user the user entity
     * @param category the category entity
     * @return the budget entity
     */
    public Budget toEntity(BudgetDTO dto, User user, Category category) {
        if (dto == null) {
            return null;
        }
        
        Budget budget = new Budget();
        budget.setId(dto.getId());
        budget.setAmount(dto.getAmount());
        budget.setMonth(dto.getMonth());
        budget.setUser(user);
        budget.setCategory(category);
        
        return budget;
    }

    /**
     * Update Budget entity from BudgetDTO.
     *
     * @param budget the budget entity to update
     * @param dto the budget DTO with updated values
     * @param category the category entity
     * @return the updated budget entity
     */
    public Budget updateEntityFromDTO(Budget budget, BudgetDTO dto, Category category) {
        if (budget == null || dto == null) {
            return budget;
        }
        
        budget.setAmount(dto.getAmount());
        budget.setMonth(dto.getMonth());
        budget.setCategory(category);
        
        return budget;
    }
}