package com.fintrack.personalfinancemanager.controller;

import com.fintrack.personalfinancemanager.dto.ApiResponse;
import com.fintrack.personalfinancemanager.dto.CategoryDTO;
import com.fintrack.personalfinancemanager.mapper.CategoryMapper;
import com.fintrack.personalfinancemanager.model.Category;
import com.fintrack.personalfinancemanager.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for category management operations.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    /**
     * Create a new category.
     *
     * @param userId the ID of the authenticated user
     * @param categoryDTO the category data
     * @return the response entity
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(
            @RequestParam Long userId,
            @Valid @RequestBody CategoryDTO categoryDTO) {
        Category category = categoryService.createCategory(userId, categoryDTO.getName());
        CategoryDTO createdCategoryDTO = categoryMapper.toDTO(category);
        return new ResponseEntity<>(ApiResponse.success(createdCategoryDTO, "Category created successfully"), 
                HttpStatus.CREATED);
    }

    /**
     * Get a category by ID.
     *
     * @param userId the ID of the authenticated user
     * @param categoryId the ID of the category
     * @return the response entity
     */
    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryDTO>> getCategory(
            @RequestParam Long userId,
            @PathVariable Long categoryId) {
        Category category = categoryService.findCategoryByIdAndUser(categoryId, userId);
        CategoryDTO categoryDTO = categoryMapper.toDTO(category);
        return ResponseEntity.ok(ApiResponse.success(categoryDTO));
    }

    /**
     * Update a category.
     *
     * @param userId the ID of the authenticated user
     * @param categoryId the ID of the category
     * @param categoryDTO the updated category data
     * @return the response entity
     */
    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryDTO>> updateCategory(
            @RequestParam Long userId,
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryDTO categoryDTO) {
        Category category = categoryService.updateCategory(userId, categoryId, categoryDTO.getName());
        CategoryDTO updatedCategoryDTO = categoryMapper.toDTO(category);
        return ResponseEntity.ok(ApiResponse.success(updatedCategoryDTO, "Category updated successfully"));
    }

    /**
     * Delete a category.
     *
     * @param userId the ID of the authenticated user
     * @param categoryId the ID of the category
     * @return the response entity
     */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @RequestParam Long userId,
            @PathVariable Long categoryId) {
        categoryService.deleteCategory(userId, categoryId);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully"));
    }

    /**
     * Get all categories for a user.
     *
     * @param userId the ID of the authenticated user
     * @param includeTransactionCount whether to include transaction count in the response
     * @param includeTotalAmount whether to include total amount in the response
     * @return the response entity
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllCategories(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "false") boolean includeTransactionCount,
            @RequestParam(defaultValue = "false") boolean includeTotalAmount) {
        List<Category> categories = categoryService.findAllCategoriesByUser(userId);
        List<CategoryDTO> categoryDTOs = categoryMapper.toDTOList(categories, includeTransactionCount, includeTotalAmount);
        return ResponseEntity.ok(ApiResponse.success(categoryDTOs));
    }

    /**
     * Get default categories.
     *
     * @return the response entity
     */
    @GetMapping("/defaults")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getDefaultCategories() {
        List<Category> defaultCategories = categoryService.findDefaultCategories();
        List<CategoryDTO> categoryDTOs = categoryMapper.toDTOList(defaultCategories);
        return ResponseEntity.ok(ApiResponse.success(categoryDTOs));
    }

    /**
     * Check if a category can be deleted.
     *
     * @param userId the ID of the authenticated user
     * @param categoryId the ID of the category
     * @return the response entity
     */
    @GetMapping("/{categoryId}/can-delete")
    public ResponseEntity<ApiResponse<Boolean>> canDeleteCategory(
            @RequestParam Long userId,
            @PathVariable Long categoryId) {
        boolean canDelete = categoryService.canDeleteCategory(userId, categoryId);
        return ResponseEntity.ok(ApiResponse.success(canDelete));
    }
}