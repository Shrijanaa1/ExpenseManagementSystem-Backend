package com.project.ems_backend.repository;

import com.project.ems_backend.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    //LOWER converts description to lowercase to make search case-insensitive. LIKE for pattern matching for any description that contains search term
    @Query("SELECT t FROM Transaction  t WHERE LOWER(t.description) LIKE LOWER(CONCAT('%', :description, '%'))") //concatenates the wildcard characters (%) before and after, (to search term anywhere within the string)
    Page<Transaction> findByDescriptionContaining(@Param("description") String description, Pageable pageable);
}
