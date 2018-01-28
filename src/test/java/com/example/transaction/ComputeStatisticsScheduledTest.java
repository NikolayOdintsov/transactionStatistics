package com.example.transaction;

import com.example.transaction.models.Transaction;
import com.example.transaction.models.TransactionStatistics;
import com.example.transaction.service.TransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Created by nikolay.odintsov on 28.01.18.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class ComputeStatisticsScheduledTest {

    @Autowired
    private TransactionService transactionService;

    @Test
    public void getTransactionStatisticsTest() throws InterruptedException {
        //give scheduled task to start at least once
        Thread.sleep(1000);

        ExecutorService executor = Executors.newFixedThreadPool(30);
        for (int i = 0; i < 30; i++) {
            executor.execute(addTransaction());
        }
        executor.shutdown();
        executor.awaitTermination(50, TimeUnit.SECONDS);

        assertEquals("Statistics test", getExpected().toString(), transactionService.getTransactionStatistics().toString());
    }

    private Runnable addTransaction() {
        return () -> {
            long timestamp = Instant.now().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
            double amount = ThreadLocalRandom.current().nextDouble(1.0, 100.0);

            Transaction tz = new Transaction(timestamp, amount);

            transactionService.addTransaction(tz);
            try {
                TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(100, 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        };
    }

    private TransactionStatistics getExpected() {
        return this.transactionService.getAllTransactions().values().stream()
                .filter(o -> ((Transaction) o).isValid())
                .mapToDouble(o -> ((Transaction) o).getAmount())
                .collect(TransactionStatistics::new,
                        TransactionStatistics::accept,
                        TransactionStatistics::combine);
    }
}
