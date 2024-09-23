package com.project.ems_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.ems_backend.model.CategoryType;
import com.project.ems_backend.model.Transaction;
import com.project.ems_backend.model.TransactionType;
import com.project.ems_backend.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)  //to test only web layer(controller)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc; //Simulates HTTP requests and validate response without starting the server

    @MockBean
    private TransactionService transactionService; //Creates mock version of TransactionService and injects mock into transactionController

    @Autowired
    private ObjectMapper objectMapper; // used to convert Java objects into JSON objects and vice-versa

    private Transaction transaction;

    @BeforeEach
    public void setup() {  //Runs before each test
        // Initialize transaction object
        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(BigDecimal.valueOf(100.00));
        transaction.setType(TransactionType.EXPENSE);
        transaction.setCategory(CategoryType.FOOD);
        transaction.setDescription("Groceries");
    }


    @Test
    void testCreateTransaction() throws Exception {
        //Mock the transactionService to return the transaction when saved
        when(transactionService.saveTransaction(any(Transaction.class))).thenReturn(transaction); //whenever the saveTransaction method is called with any Transaction object, it should return the transaction object defined in the test.

        mockMvc.perform(post("/api/transactions") //simulates POST request
                        .contentType(MediaType.APPLICATION_JSON) // tells the server that you're sending JSON data
                        .content(objectMapper.writeValueAsString(transaction))) // adds the body of the request (the content)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(transaction.getAmount())) // checks that the JSON response from the server contains the amount field, and its value is equal to transaction.getAmount()
                .andExpect(jsonPath("$.type").value(transaction.getType().toString())) //$ refers to the root of the JSON response
                .andExpect(jsonPath("$.category").value(transaction.getCategory().toString()))
                .andExpect(jsonPath("$.description").value(transaction.getDescription()));
    }

    @Test
    void testDeleteTransaction() throws Exception {
        mockMvc.perform(delete("/api/transactions/{id}", transaction.getId()))
                .andExpect(status().isOk());
    }


    @Test
    void testUpdateTransaction() throws Exception {
        transaction.setAmount(BigDecimal.valueOf(200.00));  // updating amount

        // Mock the service to return the updated transaction
        when(transactionService.updateTransaction(anyLong(), any(Transaction.class))).thenReturn(transaction);

        mockMvc.perform(put("/api/transactions/{id}", transaction.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(transaction.getAmount())); //checks that the JSON response from the server includes an amount field, and its value matches the updated amount of the transaction
    }





















    @Test
    void testGetCategories() throws Exception {
        mockMvc.perform(get("/api/transactions/categories/{type}", "expense")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(CategoryType.FOOD.toString()));
    }

}
