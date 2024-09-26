package com.project.ems_backend.repository;

import com.project.ems_backend.model.Budget;
import com.project.ems_backend.model.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    //If no budget is found for the given category, the method returns an Optional.empty() instead of null. This makes code safer and helps avoid NullPointerExceptions
    Optional<Budget> findByCategory(CategoryType category);
}
