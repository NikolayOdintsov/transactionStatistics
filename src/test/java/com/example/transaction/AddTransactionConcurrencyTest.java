package com.example.transaction;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.anarsoft.vmlens.concurrent.junit.ThreadCount;
import com.example.transaction.models.Transaction;
import com.example.transaction.service.TransactionService;
import com.example.transaction.service.TransactionServiceImpl;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;

/**
 * Test provides easy way to test behaviour of TransactionService in concurrent environment.
 * <p/>
 * Created by nikolay.odintsov on 28.01.18.
 */

@RunWith(ConcurrentTestRunner.class)
public class AddTransactionConcurrencyTest {
    private static final Logger logger = LoggerFactory.getLogger(AddTransactionConcurrencyTest.class);
    /**
     * It is highly advised to play around with THREAD_COUNT param value and running test several times.
     */
    private final static int THREAD_COUNT = 100;
    private TransactionService transactionService = new TransactionServiceImpl();

    @Test
    @ThreadCount(THREAD_COUNT)
    public void addTransaction() throws InterruptedException {
        long timestamp = Instant.now().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
        double amount = ThreadLocalRandom.current().nextDouble(1.0, 100.0);

        Transaction tz = new Transaction(timestamp, amount);

        logger.debug("Adding new transaction. Thread: " + Thread.currentThread().getName() + " at " + timestamp);

        transactionService.addTransaction(tz);

    }

    @After
    public void getAllTransactions() {
        assertEquals("Capacity test", THREAD_COUNT, transactionService.getAllTransactions().size());
    }
}
