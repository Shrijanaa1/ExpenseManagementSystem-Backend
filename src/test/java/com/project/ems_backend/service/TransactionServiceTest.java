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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock //simulate the behavior of the repository without needing to interact with a real database
    private TransactionRepository transactionRepository;

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

//    @Test
//    void testGetAllTransactions() {
//        when(transactionRepository.findAll()).thenReturn(Arrays.asList(transaction));   //it should return a list containing the transaction object created in the @BeforeEach method
//        List<Transaction> transactions = transactionService.getAllTransactions();
//        assertNotNull(transactions);   //Ensures that the list of transactions is not null
//        assertEquals(1, transactions.size());    //Verifies that the list contains exactly one transaction
//        assertEquals(transaction, transactions.get(0));      //Ensures that the first (and only) transaction in the list is equal to the transaction object created during the setup phase
//
//        verify(transactionRepository, times(1)).findAll();      //verifies that the findAll() method of the transactionRepository mock was called exactly once during the execution of getAllTransactions()
//    }


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
    }

    @Test
    void testDeleteTransaction() {
        doNothing().when(transactionRepository).deleteById(1L);

        transactionService.deleteTransaction(1L);
        verify(transactionRepository, times(1)).deleteById(1L);
    }


    @Test
    void testUpdateTransaction() {
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setAmount(BigDecimal.valueOf(150.00));
        updatedTransaction.setType(TransactionType.INCOME);
        updatedTransaction.setCategory(CategoryType.FREELANCING);
        updatedTransaction.setDescription("Project");

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Transaction result = transactionService.updateTransaction(1L, updatedTransaction);
        assertEquals(BigDecimal.valueOf(150.00), result.getAmount());
        assertEquals(TransactionType.INCOME, result.getType());
        assertEquals(CategoryType.FREELANCING, result.getCategory());
        assertEquals("Project", result.getDescription());

        verify(transactionRepository, times(1)).findById(1L);
        verify(transactionRepository, times(1)).save(transaction);
    }

}