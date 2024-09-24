package com.project.ems_backend.controller;

import com.project.ems_backend.model.CategoryType;
import com.project.ems_backend.model.Transaction;
import com.project.ems_backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public Page<Transaction> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) Long id, //for filtering transaction by id
            @RequestParam(required = false) String description,
            @RequestParam(required = false, defaultValue = "contains") String filterType
    ){
        return transactionService.getFilteredTransactions(page, size, sortBy, id, description, filterType);
    }


    @GetMapping("/{id}")
    public Transaction getTransactionById(@PathVariable("id") long id) {
        return transactionService.getTransactionById(id);
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction){
        return transactionService.saveTransaction(transaction);
    }

    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable Long id){
        transactionService.deleteTransaction(id);
    }

    @PutMapping("/{id}")
    public Transaction updateTransaction(@PathVariable Long id, @RequestBody Transaction transaction){
        return transactionService.updateTransaction(id, transaction);
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

