package com.project.ems_backend.service;

import com.project.ems_backend.model.Budget;
import com.project.ems_backend.model.CategoryType;
import com.project.ems_backend.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    // Fetch all budgets without pagination (used by TransactionService)
    public List<Budget> getAllBudgetsWithoutPagination() {
        return budgetRepository.findAll();
    }

    public Budget getBudgetById(long id) {
        return budgetRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Budget not found with id: " + id));
    }

    public Page<Budget> getAllBudgets(int page, int size, String sortBy) { // //Page is Spring Data interface that encapsulates pagination logic
        return budgetRepository.findAll(PageRequest.of(page, size, Sort.by(sortBy)));
    }


    public Budget saveBudget(Budget budget) {
        // Initialize remainingAmount to budgetLimit if it's a new budget
        if(budget.getId() == null){
            budget.setRemainingAmount(budget.getBudgetLimit());
        }

        return budgetRepository.save(budget);
    }

    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }

    public Budget updateBudget(Long id, Budget updatedBudget) {
        Budget existingBudget = getBudgetById(id);
        existingBudget.setCategory(updatedBudget.getCategory());
        existingBudget.setBudgetLimit(updatedBudget.getBudgetLimit());
        existingBudget.setStartDate(updatedBudget.getStartDate());
        existingBudget.setEndDate(updatedBudget.getEndDate());

        //Update remaining amount based on new budget limit
        existingBudget.setRemainingAmount(updatedBudget.getBudgetLimit());

        return budgetRepository.save(existingBudget);
    }

    //method to get budget by category
    public Budget getBudgetByCategory(CategoryType category) {
        return budgetRepository.findByCategory(category)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found for category: " + category));
    }

}
