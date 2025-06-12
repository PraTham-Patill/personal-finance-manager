package com.fintrack.personalfinancemanager.service.impl;

import com.fintrack.personalfinancemanager.model.Category;
import com.fintrack.personalfinancemanager.model.Transaction;
import com.fintrack.personalfinancemanager.model.User;
import com.fintrack.personalfinancemanager.repository.CategoryRepository;
import com.fintrack.personalfinancemanager.repository.TransactionRepository;
import com.fintrack.personalfinancemanager.repository.UserRepository;
import com.fintrack.personalfinancemanager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the CategoryService interface.
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public Category createCategory(Category category, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if a category with the same name already exists for the user
        if (categoryRepository.existsByNameAndUser(category.getName(), user)) {
            throw new IllegalArgumentException("A category with this name already exists");
        }

        category.setUser(user);
        category.setDefault(false); // User-created categories are not default

        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(Category category, Long userId) {
        Category existingCategory = categoryRepository.findById(category.getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        // Verify the category belongs to the user
        if (!existingCategory.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Category does not belong to the user");
        }

        // Check if a different category with the same name already exists for the user
        Category categoryWithSameName = categoryRepository.findByNameAndUser(category.getName(), existingCategory.getUser());
        if (categoryWithSameName != null && !categoryWithSameName.getId().equals(category.getId())) {
            throw new IllegalArgumentException("A category with this name already exists");
        }

        // Update fields
        existingCategory.setName(category.getName());

        return categoryRepository.save(existingCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        // Verify the category belongs to the user
        if (!category.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Category does not belong to the user");
        }

        // Check if the category can be deleted
        if (!canDeleteCategory(categoryId, userId)) {
            throw new IllegalArgumentException("Category cannot be deleted because it is used in transactions");
        }

        categoryRepository.delete(category);
    }

    @Override
    public Category findById(Long categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        // Verify the category belongs to the user
        if (!category.getUser().getId().equals(userId) && !category.isDefault()) {
            throw new IllegalArgumentException("Category does not belong to the user");
        }

        return category;
    }

    @Override
    public List<Category> findAllByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return categoryRepository.findByUser(user);
    }

    @Override
    public Category findByNameAndUser(String name, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return categoryRepository.findByNameAndUser(name, user);
    }

    @Override
    public boolean existsByNameAndUser(String name, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return categoryRepository.existsByNameAndUser(name, user);
    }

    @Override
    public List<Category> findDefaultCategories() {
        return categoryRepository.findByIsDefaultTrue();
    }

    @Override
    public boolean canDeleteCategory(Long categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        // Verify the category belongs to the user
        if (!category.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Category does not belong to the user");
        }

        // Check if the category is used in any transactions
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<Transaction> transactions = transactionRepository.findByUserAndCategory(user, category);

        return transactions.isEmpty();
    }
}