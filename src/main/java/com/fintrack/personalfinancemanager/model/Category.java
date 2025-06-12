package com.fintrack.personalfinancemanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity class representing a transaction category.
 */
@Entity
@Table(name = "categories", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "user_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Category name is required")
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean isDefault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private Set<Transaction> transactions = new HashSet<>();

    /**
     * Checks if the category can be deleted.
     * A category cannot be deleted if it's a default category or if it has transactions.
     *
     * @return true if the category can be deleted, false otherwise
     */
    @Transient
    public boolean canBeDeleted() {
        return !isDefault && (transactions == null || transactions.isEmpty());
    }
}