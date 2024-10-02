package com.project.ems_backend.service;

import com.project.ems_backend.model.Budget;
import com.project.ems_backend.repository.BudgetRepository;
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



}
