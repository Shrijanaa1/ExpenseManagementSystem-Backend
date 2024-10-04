package com.project.ems_backend.service;

import com.project.ems_backend.model.CategoryType;
import com.project.ems_backend.model.Transaction;
import com.project.ems_backend.model.TransactionType;
import com.project.ems_backend.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock //simulate the behavior of the repository without needing to interact with a real database
    private TransactionRepository transactionRepository;

    @Mock
    private BudgetService budgetService;

    @InjectMocks // create an instance of the TransactionService and inject the TransactionRepository mock into it(focus on testing it independently)
    private TransactionService transactionService;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); //initialize mocks object (in this case, transactionRepository) before each test method runs

        //create a simple transaction
        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(BigDecimal.valueOf(100.00));
        transaction.setType(TransactionType.EXPENSE);
        transaction.setCategory(CategoryType.FOOD);
        transaction.setDescription("Lunch");
    }


    @Test
    void testGetTransactionById() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction)); //Optional.of(transaction) wraps the transaction object to simulate a successful search
        Transaction foundTransaction = transactionService.getTransactionById(1L);
        assertNotNull(foundTransaction);
        assertEquals(1L, foundTransaction.getId());
        assertEquals(TransactionType.EXPENSE, foundTransaction.getType());
        assertEquals(CategoryType.FOOD, foundTransaction.getCategory());

        verify(transactionRepository, times(1)).findById(1L);
    }


    @Test
    void testSaveTransaction() {
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Transaction transactionSaved = transactionService.saveTransaction(transaction);
        assertNotNull(transactionSaved);
        assertEquals(transaction, transactionSaved);

        verify(transactionRepository, times(1)).save(transaction);
        verify(budgetService, times(1)).updateBudgetForTransaction(transaction);
    }

    @Test
    void testDeleteTransaction() {
        // Mock findById and deleteById methods
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        doNothing().when(transactionRepository).deleteById(1L);

        // Call the method to test
        transactionService.deleteTransaction(1L);

        // Verify repository and service interactions
        verify(transactionRepository, times(1)).findById(1L);
        verify(budgetService, times(1)).reverseBudgetForTransaction(transaction);
        verify(transactionRepository, times(1)).deleteById(1L);
    }


    @Test
    void testUpdateTransaction() {
        // Create the updated transaction details
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setAmount(BigDecimal.valueOf(150.00));
        updatedTransaction.setType(TransactionType.INCOME);
        updatedTransaction.setCategory(CategoryType.FREELANCING);
        updatedTransaction.setDescription("Project");

        // Mock repository findById and save methods
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        // Call the method to test
        Transaction result = transactionService.updateTransaction(1L, updatedTransaction);

        // Verify that the original transaction was updated correctly
        assertEquals(BigDecimal.valueOf(150.00), result.getAmount());
        assertEquals(TransactionType.INCOME, result.getType());
        assertEquals(CategoryType.FREELANCING, result.getCategory());
        assertEquals("Project", result.getDescription());

        // Verify repository and service interactions
        verify(transactionRepository, times(1)).findById(1L);
        verify(transactionRepository, times(1)).save(transaction);
        verify(budgetService, times(1)).reverseBudgetForTransaction(transaction);
        verify(budgetService, times(1)).updateBudgetForTransaction(transaction);
    }

    @Test
    void testGetFilteredTransactions_ById() {
        // Mock repository response for finding by ID
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        // Call the method to test with ID filter
        Page<Transaction> result = transactionService.getFilteredTransactions(0, 10, "amount", 1L, null, null);

        // Verify and assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(transaction, result.getContent().get(0));

        verify(transactionRepository, times(1)).findById(1L);
    }
}