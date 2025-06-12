package com.fintrack.personalfinancemanager.mapper;

import com.fintrack.personalfinancemanager.dto.TransactionDTO;
import com.fintrack.personalfinancemanager.model.Category;
import com.fintrack.personalfinancemanager.model.Transaction;
import com.fintrack.personalfinancemanager.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to convert between Transaction entity and DTOs.
 */
@Component
public class TransactionMapper {

    /**
     * Convert Transaction entity to TransactionDTO.
     *
     * @param transaction the transaction entity
     * @return the transaction DTO
     */
    public TransactionDTO toDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        
        TransactionDTO dto = TransactionDTO.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .date(transaction.getDate())
                .description(transaction.getDescription())
                .notes(transaction.getNotes())
                .type(transaction.getType())
                .build();
        
        if (transaction.getCategory() != null) {
            dto.setCategoryId(transaction.getCategory().getId());
            dto.setCategoryName(transaction.getCategory().getName());
        }
        
        return dto;
    }

    /**
     * Convert a list of Transaction entities to a list of TransactionDTOs.
     *
     * @param transactions the list of transaction entities
     * @return the list of transaction DTOs
     */
    public List<TransactionDTO> toDTOList(List<Transaction> transactions) {
        if (transactions == null) {
            return null;
        }
        
        return transactions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert TransactionDTO to Transaction entity.
     *
     * @param dto the transaction DTO
     * @param user the user entity
     * @param category the category entity
     * @return the transaction entity
     */
    public Transaction toEntity(TransactionDTO dto, User user, Category category) {
        if (dto == null) {
            return null;
        }
        
        Transaction transaction = new Transaction();
        transaction.setId(dto.getId());
        transaction.setAmount(dto.getAmount());
        transaction.setDate(dto.getDate());
        transaction.setDescription(dto.getDescription());
        transaction.setNotes(dto.getNotes());
        transaction.setType(dto.getType());
        transaction.setUser(user);
        transaction.setCategory(category);
        
        return transaction;
    }

    /**
     * Update Transaction entity from TransactionDTO.
     *
     * @param transaction the transaction entity to update
     * @param dto the transaction DTO with updated values
     * @param category the category entity
     * @return the updated transaction entity
     */
    public Transaction updateEntityFromDTO(Transaction transaction, TransactionDTO dto, Category category) {
        if (transaction == null || dto == null) {
            return transaction;
        }
        
        transaction.setAmount(dto.getAmount());
        transaction.setDate(dto.getDate());
        transaction.setDescription(dto.getDescription());
        transaction.setNotes(dto.getNotes());
        transaction.setType(dto.getType());
        transaction.setCategory(category);
        
        return transaction;
    }
}