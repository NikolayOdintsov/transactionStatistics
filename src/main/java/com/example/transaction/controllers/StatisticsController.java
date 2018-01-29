package com.example.transaction.controllers;

import com.example.transaction.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

/**
 * Created by nikolay.odintsov on 27.01.18.
 */

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    private TransactionService transactionService;

    @GetMapping(produces = "application/json")
    public Callable<ResponseEntity<String>> getStatistics() {
        logger.debug("GET statistics called");

        return () -> ResponseEntity.ok(this.transactionService.getTransactionStatistics().toString());
    }
}
