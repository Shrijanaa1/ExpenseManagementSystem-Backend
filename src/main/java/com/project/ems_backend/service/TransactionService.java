package com.project.ems_backend.service;

import com.project.ems_backend.model.Transaction;
import com.project.ems_backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BudgetService budgetService;


    @Autowired
    public TransactionService(TransactionRepository transactionRepository, BudgetService budgetService) {
        this.transactionRepository = transactionRepository;
        this.budgetService = budgetService;
    }


    //With pagination only
    public Page<Transaction> getAllTransactions(int page, int size, String sortBy) { // //Page is Spring Data interface that encapsulates pagination logic
        return transactionRepository.findAll(PageRequest.of(page, size, Sort.by(sortBy))); //PageRequest creates Pageable object, used by repository to fetch specific page of data with certain size and sorting
    }

    public Page<Transaction> getFilteredTransactions(int page, int size, String sortBy, Long id, String description, String filterType) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy)); //Pagination object to define pagination and sorting behaviour

        if (id != null) {
            Transaction transaction = transactionRepository.findById(id).orElse(null);
            return new PageImpl<>(transaction == null ? List.of() : List.of(transaction), pageable, 1); //if transaction is null, returns an empty list else returns a list with single transaction // 1 indicates that the total number of elements is 1

        } else if (description != null ) {
            return switch (filterType) {
                case "contains" -> transactionRepository.findByDescriptionContaining(description, pageable);
                case "startsWith" -> transactionRepository.findByDescriptionStartingWith(description, pageable);
                case "endsWith" -> transactionRepository.findByDescriptionEndingWith(description, pageable);
                case "equals" -> transactionRepository.findByDescriptionEquals(description, pageable);
                case "notEquals" -> transactionRepository.findByDescriptionNotEquals(description, pageable);
                case "notContains" -> transactionRepository.findByDescriptionNotContains(description, pageable);
                default -> transactionRepository.findAll(pageable);
            };
        } else {
            return transactionRepository.findAll(pageable);
        }
    }


    public Transaction getTransactionById(long id) {
        return transactionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + id));
    }

    public Transaction saveTransaction(Transaction transaction) {
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Delegate to BudgetService to update the budget
        budgetService.updateBudgetForTransaction(savedTransaction);

        return savedTransaction;
    }

    public void deleteTransaction(Long id) {
        Transaction transactionToDelete = getTransactionById(id);

        // Reverse the budget effect before deletion
        budgetService.reverseBudgetForTransaction(transactionToDelete);

        // Now delete the transaction
        transactionRepository.deleteById(id);
    }

    public Transaction updateTransaction(Long id, Transaction updatedTransaction) {
        Transaction existingTransaction = getTransactionById(id);

        // Reverse the original transaction's impact on the budget
        budgetService.reverseBudgetForTransaction(existingTransaction);

        existingTransaction.setAmount(updatedTransaction.getAmount());
        existingTransaction.setCategory(updatedTransaction.getCategory());
        existingTransaction.setDescription(updatedTransaction.getDescription());
        existingTransaction.setType(updatedTransaction.getType());

        Transaction savedTransaction = transactionRepository.save(existingTransaction);

        // Update budget for the new transaction
        budgetService.updateBudgetForTransaction(savedTransaction);

        return savedTransaction;
    }
}