package com.project.ems_backend.service;

import com.project.ems_backend.model.Budget;
import com.project.ems_backend.model.CategoryType;
import com.project.ems_backend.repository.BudgetRepository;
import com.project.ems_backend.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private BudgetService budgetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllBudgetsWithoutPagination() {
        //Arrange(Setup necessary preconditions: creating objects, configuring mocks)
        List<Budget> budgets = Arrays.asList(new Budget(), new Budget()); //Two objects of budget to test how method handles multiple budgets
        when(budgetRepository.findAll()).thenReturn(budgets);

        //Act(Execute method being tested)
        List<Budget> result = budgetService.getAllBudgetsWithoutPagination();

        //Assert(Verify expected outcome has occurred)
        assertEquals(2, result.size());
        verify(budgetRepository, times(1)).findAll();
    }

    @Test
    void getBudgetById() {
        Budget budget = new Budget();
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(budget));

        Budget result = budgetService.getBudgetById(1L);

        assertNotNull(result);
        verify(budgetRepository, times(1)).findById(1L);
    }


    @Test
    void getAllBudgets() {
        Budget budget = new Budget();
        Page<Budget> budgetPage = new PageImpl<>(Collections.singletonList(budget));
        when(budgetRepository.findAll(any(PageRequest.class))).thenReturn(budgetPage);

        Page<Budget> result = budgetService.getAllBudgets(0, 10, "category");

        assertEquals(1, result.getTotalElements());
        verify(budgetRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void saveBudget() {
        Budget budget = new Budget();
        budget.setBudgetLimit(BigDecimal.valueOf(1000));
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);

        Budget result = budgetService.saveBudget(budget);

        assertEquals(BigDecimal.valueOf(1000), result.getRemainingAmount());
        verify(budgetRepository, times(1)).save(budget);
    }

    @Test
    void updateBudget() {
        Budget existingBudget = new Budget();
        existingBudget.setCategory(CategoryType.FOOD);
        existingBudget.setBudgetLimit(BigDecimal.valueOf(1000));
        existingBudget.setRemainingAmount(BigDecimal.valueOf(1000));

        Budget updatedBudget = new Budget();
        updatedBudget.setCategory(CategoryType.FOOD);
        updatedBudget.setBudgetLimit(BigDecimal.valueOf(2000));
        updatedBudget.setRemainingAmount(BigDecimal.valueOf(2000));

        when(budgetRepository.findById(1L)).thenReturn(Optional.of(existingBudget));
        when(budgetRepository.save(any(Budget.class))).thenReturn(existingBudget);

        Budget result = budgetService.updateBudget(1L, updatedBudget);

        assertEquals(BigDecimal.valueOf(2000), result.getRemainingAmount());
        assertEquals(BigDecimal.valueOf(2000), result.getBudgetLimit());
        verify(budgetRepository, times(1)).findById(1L);
        verify(budgetRepository, times(1)).save(existingBudget);
    }


}
