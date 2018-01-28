package com.example.transaction.controllers;

import com.example.transaction.models.Transaction;
import com.example.transaction.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by nikolay.odintsov on 27.01.18.
 */

@RestController
@RequestMapping("/transactions")
public class TransactionsController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionsController.class);

    @Autowired
    private TransactionService transactionService;

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> addTransactions(@RequestBody Transaction transaction) {
        logger.debug("POST transactions called. Transaction: " + transaction);

        boolean success = this.transactionService.addTransaction(transaction);

        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }
}
