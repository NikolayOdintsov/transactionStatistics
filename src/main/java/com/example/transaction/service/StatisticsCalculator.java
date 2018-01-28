package com.example.transaction.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Created by nikolay.odintsov on 28.01.18.
 */
@Component
public class StatisticsCalculator {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsCalculator.class);

    @Autowired
    private TransactionService transactionService;

    @Scheduled(fixedRateString = "${fixedRate.in.milliseconds}")
    public void scheduledStatisticsCalculatorTask() {
        this.transactionService.calculateTransactionStatistics();

        logger.debug("Statistics calculate task completed at " + Instant.now().toString() + ". Thread: " + Thread.currentThread().getName());
    }
}
