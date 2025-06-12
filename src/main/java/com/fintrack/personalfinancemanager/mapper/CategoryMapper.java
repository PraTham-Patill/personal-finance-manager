package com.fintrack.personalfinancemanager.mapper;

import com.fintrack.personalfinancemanager.dto.CategoryDTO;
import com.fintrack.personalfinancemanager.model.Category;
import com.fintrack.personalfinancemanager.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to convert between Category entity and DTOs.
 */
@Component
public class CategoryMapper {

    /**
     * Convert Category entity to CategoryDTO.
     *
     * @param category the category entity
     * @return the category DTO
     */
    public CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }
        
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .isDefault(category.isDefault())
                .build();
    }

    /**
     * Convert Category entity to CategoryDTO with transaction count.
     *
     * @param category the category entity
     * @param transactionCount the number of transactions in this category
     * @return the category DTO
     */
    public CategoryDTO toDTOWithTransactionCount(Category category, Long transactionCount) {
        CategoryDTO dto = toDTO(category);
        if (dto != null) {
            dto.setTransactionCount(transactionCount);
        }
        return dto;
    }

    /**
     * Convert Category entity to CategoryDTO with total amount.
     *
     * @param category the category entity
     * @param totalAmount the total amount spent in this category
     * @return the category DTO
     */
    public CategoryDTO toDTOWithTotalAmount(Category category, Double totalAmount) {
        CategoryDTO dto = toDTO(category);
        if (dto != null) {
            dto.setTotalAmount(totalAmount);
        }
        return dto;
    }

    /**
     * Convert a list of Category entities to a list of CategoryDTOs.
     *
     * @param categories the list of category entities
     * @return the list of category DTOs
     */
    public List<CategoryDTO> toDTOList(List<Category> categories) {
        if (categories == null) {
            return null;
        }
        
        return categories.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert CategoryDTO to Category entity.
     *
     * @param dto the category DTO
     * @param user the user entity
     * @return the category entity
     */
    public Category toEntity(CategoryDTO dto, User user) {
        if (dto == null) {
            return null;
        }
        
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setDefault(dto.isDefault());
        category.setUser(user);
        
        return category;
    }

    /**
     * Update Category entity from CategoryDTO.
     *
     * @param category the category entity to update
     * @param dto the category DTO with updated values
     * @return the updated category entity
     */
    public Category updateEntityFromDTO(Category category, CategoryDTO dto) {
        if (category == null || dto == null) {
            return category;
        }
        
        category.setName(dto.getName());
        // We don't update isDefault from DTO for security reasons
        // Only admins should be able to change default status
        
        return category;
    }
}