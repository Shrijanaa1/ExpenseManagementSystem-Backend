package com.project.ems_backend.repository;

import com.project.ems_backend.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Handle "contains" filtering
    //LOWER converts description to lowercase to make search case-insensitive. LIKE for pattern matching for any description that contains search term
    @Query("SELECT t FROM Transaction  t WHERE LOWER(t.description) LIKE LOWER(CONCAT('%', :description, '%'))") //concatenates the wildcard characters (%) before and after, (to search term anywhere within the string)
    Page<Transaction> findByDescriptionContaining(@Param("description") String description, Pageable pageable);

    // Handle "startsWith" filtering
    @Query("SELECT t FROM Transaction t WHERE LOWER(t.description) LIKE LOWER(CONCAT(:description, '%'))")
    Page<Transaction> findByDescriptionStartingWith(@Param("description") String description, Pageable pageable);

    // Handle "endsWith" filtering
    @Query("SELECT t FROM Transaction t WHERE LOWER(t.description) LIKE LOWER(CONCAT('%', :description))")
    Page<Transaction> findByDescriptionEndingWith(@Param("description") String description, Pageable pageable);

    // Handle "equals" filtering
    @Query("SELECT t FROM Transaction t WHERE LOWER(t.description) = LOWER(:description)")
    Page<Transaction> findByDescriptionEquals(@Param("description") String description, Pageable pageable);

    @Query("SELECT t FROM Transaction  t WHERE LOWER(t.description) != LOWER(:description)")
    Page<Transaction> findByDescriptionNotEquals(String description, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE LOWER(t.description) NOT LIKE LOWER(CONCAT('%', :description, '%' ))")
    Page<Transaction> findByDescriptionNotContains(String description, Pageable pageable);

}
