package com.project.ems_backend.service;

import com.project.ems_backend.model.Budget;
import com.project.ems_backend.model.CategoryType;
import com.project.ems_backend.model.Transaction;
import com.project.ems_backend.model.TransactionType;
import com.project.ems_backend.repository.BudgetRepository;
import com.project.ems_backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository, TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
    }

    // Fetch all budgets without pagination (used by TransactionService)
    public List<Budget> getAllBudgetsWithoutPagination() {
        return budgetRepository.findAll();
    }

    public Budget getBudgetById(long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with id: " + id));
    }

    public Page<Budget> getAllBudgets(int page, int size, String sortBy) {
        return budgetRepository.findAll(PageRequest.of(page, size, Sort.by(sortBy)));
    }

    public Budget saveBudget(Budget budget) {
        // Initialize remainingAmount to budgetLimit if it's a new budget
        if (budget.getId() == null) {
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

        // Update remaining amount based on the category's total expenses
        updateRemainingAmount(existingBudget);

        return budgetRepository.save(existingBudget);
    }

    public Budget getBudgetByCategory(CategoryType category) {
        return budgetRepository.findByCategory(category)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found for category: " + category));
    }

    // Update remaining amount based on total expenses for the budget category
    public void updateRemainingAmount(Budget budget) {
        BigDecimal totalExpenses = calculateTotalExpensesForCategory(budget.getCategory());
        budget.setRemainingAmount(budget.getBudgetLimit().subtract(totalExpenses));
        saveBudget(budget);
    }

    // Calculate total expenses for a specific category
    public BigDecimal calculateTotalExpensesForCategory(CategoryType category) {
        List<Transaction> expenses = transactionRepository.findByCategoryAndType(category, TransactionType.EXPENSE);
        return expenses.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Update all budgets remaining amounts (batch process)
    public void updateAllBudgetsRemainingAmounts() {
        List<Budget> allBudgets = getAllBudgetsWithoutPagination();
        allBudgets.forEach(this::updateRemainingAmount);
    }

    // Update budget when transaction is saved
    public void updateBudgetForTransaction(Transaction transaction) {
        if (transaction.getType() == TransactionType.EXPENSE) {
            Budget budget = getBudgetByCategory(transaction.getCategory());
            updateRemainingAmount(budget);
        }
    }

    // Reverse the budget update when a transaction is deleted
    public void reverseBudgetForTransaction(Transaction transaction) {
        if (transaction.getType() == TransactionType.EXPENSE) {
            Budget budget = getBudgetByCategory(transaction.getCategory());
            updateRemainingAmount(budget);
        }
    }
}
