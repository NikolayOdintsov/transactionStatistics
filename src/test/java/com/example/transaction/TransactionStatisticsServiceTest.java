package com.example.transaction;

import com.example.transaction.models.Transaction;
import com.example.transaction.service.TransactionService;
import com.example.transaction.service.TransactionServiceImpl;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by nikolay.odintsov on 28.01.18.
 */
public class TransactionStatisticsServiceTest {

    private TransactionService transactionService = new TransactionServiceImpl();

    @Test
    public void shouldStoreTransactions() throws InterruptedException {
        //given
        int amountOfTransactions = 10;

        Map transactions;
        Transaction firstTZ = null;
        Transaction lastTZ = null;

        //when
        for (int i = 1; i <= amountOfTransactions; i++) {

            long timestamp = Instant.now().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
            Transaction tz = new Transaction(timestamp, i);

            if (i == 1) {
                firstTZ = tz;
            } else if (i == amountOfTransactions) {
                lastTZ = tz;
            }

            this.transactionService.addTransaction(tz);
        }

        transactions = transactionService.getAllTransactions();

        //then
        assertNotNull(transactions);
        assertEquals(amountOfTransactions, transactions.size());

        //should be passed when we assume that amount of transactions is less than storage capacity.
        assertTrue(transactions.containsValue(firstTZ));
        assertTrue(transactions.containsValue(lastTZ));

    }

    @Test
    public void shouldHaveConstantStorageComplexityForTransactionStorage() throws InterruptedException {
        //given
        int amountOfTransactions = 60001;

        Map transactions;
        Transaction firstTZ = null;
        Transaction lastTZ = null;

        //when
        for (int i = 1; i <= amountOfTransactions; i++) {
            long timestamp = Instant.now().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
            Transaction tz = new Transaction(timestamp, i);

            if (i == 1) {
                firstTZ = tz;
            } else if (i == amountOfTransactions) {
                lastTZ = tz;
            }

            this.transactionService.addTransaction(tz);
        }
        transactions = transactionService.getAllTransactions();

        //then
        assertNotNull(transactions);
        assertNotEquals(amountOfTransactions, transactions.size());
        assertEquals(TransactionServiceImpl.MAX_STORAGE_CAPACITY, transactions.size());

        // first transaction should be overwritten and lost
        assertFalse(transactions.containsValue(firstTZ));

        // last transaction should persist
        assertTrue(transactions.containsValue(lastTZ));

    }


    @Test
    public void shouldReturnTransactionStatistics() {
        //given

        //when

        //then
    }
}
