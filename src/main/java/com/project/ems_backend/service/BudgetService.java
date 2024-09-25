package com.project.ems_backend.service;

import com.project.ems_backend.model.Budget;
import com.project.ems_backend.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public Budget getBudgetById(long id) {
        return budgetRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Budget not found with id: " + id));
    }

    public Budget saveBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    public void deleteBudgetById(long id) {
        budgetRepository.deleteById(id);
    }

    public Budget updateBudget(Long id, Budget updatedBudget) {
        Budget existingBudget = getBudgetById(id);
        existingBudget.setCategory(updatedBudget.getCategory());
        existingBudget.setBudgetLimit(updatedBudget.getBudgetLimit());
        existingBudget.setStartDate(updatedBudget.getStartDate());
        existingBudget.setEndDate(updatedBudget.getEndDate());
        return budgetRepository.save(existingBudget);
    }
}
