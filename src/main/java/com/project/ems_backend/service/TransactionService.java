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

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    //With pagination only
    public Page<Transaction> getAllTransactions(int page, int size, String sortBy) { // //Page is Spring Data interface that encapsulates pagination logic
        return transactionRepository.findAll(PageRequest.of(page, size, Sort.by(sortBy))); //PageRequest creates Pageable object, used by repository to fetch specific page of data with certain size and sorting
    }


    public Page<Transaction> getFilteredTransactions(int page, int size, String sortBy, Long id, String description) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy)); //Controls how many result and which page to fetch

        //check if id or description filter is applied
        if(id!= null){
            Transaction transaction = transactionRepository.findById(id).orElse(null);
            return new PageImpl<>(transaction == null ? List.of() : List.of(transaction), pageable, 1); //If transaction is found, returns single-item list else empty list is returned

        }else if(description != null && !description.isEmpty()){ //check for empty string
            return transactionRepository.findByDescriptionContaining(description,pageable);
        }else {
            return transactionRepository.findAll(pageable);
        }
    }

    public Transaction getTransactionById(long id) {
        return transactionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + id));
    }

    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    public Transaction updateTransaction(Long id, Transaction updatedTransaction) {
        Transaction existingTransaction = getTransactionById(id);
        existingTransaction.setAmount(updatedTransaction.getAmount());
        existingTransaction.setCategory(updatedTransaction.getCategory());
        existingTransaction.setDescription(updatedTransaction.getDescription());
        existingTransaction.setType(updatedTransaction.getType());

        return transactionRepository.save(existingTransaction);
    }

}
