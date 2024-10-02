package com.project.ems_backend.service;

import com.project.ems_backend.model.Budget;
import com.project.ems_backend.repository.BudgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

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
        List<Budget> budgets = Arrays.asList(new Budget(), new Budget());
        when(budgetRepository.findAll()).thenReturn(budgets);

        List<Budget> result = budgetService.getAllBudgetsWithoutPagination();

        assertEquals(2, result.size());
        verify(budgetRepository, times(1)).findAll();
    }
}
