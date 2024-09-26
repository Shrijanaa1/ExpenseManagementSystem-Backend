package com.project.ems_backend.controller;

import com.project.ems_backend.model.Budget;
import com.project.ems_backend.model.CategoryType;
import com.project.ems_backend.service.BudgetService;
import com.project.ems_backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/budgets")
public class BudgetController {
    private final BudgetService budgetService;
    private final TransactionService transactionService;

    @Autowired
    public BudgetController(BudgetService budgetService, TransactionService transactionService) {
        this.budgetService = budgetService;
        this.transactionService = transactionService;
    }

    @GetMapping("/{id}")
    public Budget getBudgetById(@PathVariable("id") long id) {
        return budgetService.getBudgetById(id);
    }

    @GetMapping
    public Page<Budget> getAllBudgets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ){
        return budgetService.getAllBudgets(page, size, sortBy);
    }


    @PostMapping
    public Budget createBudget(@RequestBody Budget budget){
        return budgetService.saveBudget(budget);
    }

    @DeleteMapping("/{id}")
    public void deleteBudget(@PathVariable Long id){
        budgetService.deleteBudget(id);
    }

    @PutMapping("/{id}")
    public Budget updateBudget(@PathVariable Long id, @RequestBody Budget budget){
        return budgetService.updateBudget(id, budget);
    }

    @PostMapping("/reload")
    public ResponseEntity<Void> reloadBudgets(){
        transactionService.updateAllBudgetsRemainingAmounts();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/categories/{type}")
    public ResponseEntity<List<String>> getCategories(@PathVariable("type") String type) {
        List<String> categories = Arrays.stream(CategoryType.values())         // converts array into a stream for performing operations like filtering and mapping on it
                .filter(cat -> cat.getType().name().equalsIgnoreCase(type))           //cat represents each CategoryType value (category) as it is being processed in the stream
                .map(Enum::name)     //maps each element in the filtered stream to its name of the enum value
                .toList();
        return ResponseEntity.ok(categories);
    }

}
