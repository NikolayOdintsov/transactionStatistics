package com.example.transaction;

import com.example.transaction.models.Transaction;
import com.example.transaction.models.TransactionStatistics;
import com.example.transaction.service.TransactionService;
import com.example.transaction.service.TransactionServiceImpl;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

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

        Map<UUID, Transaction> transactions;
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

        Map<UUID, Transaction> transactions;
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
    public void shouldReturnTransactionStatistics() throws JSONException {
        //given
        JSONObject expectedStatisticsJson = new JSONObject("{\"sum\": 55.00, " +
                "\"avg\": 5.50, \"max\": 10.00, \"min\": 1.00, \"count\": 10}");

        int amountOfTransactions = 10;
        double sum = 0.00;
        for (int i = 1; i <= amountOfTransactions; i++) {
            long timestamp = Instant.now().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
            Transaction tz = new Transaction(timestamp, i);
            this.transactionService.addTransaction(tz);
            sum += i;
        }

        double avg = sum / amountOfTransactions;

        //when
        transactionService.calculateTransactionStatistics();
        TransactionStatistics statistics = transactionService.getTransactionStatistics();

        //then
        assertNotNull(statistics);
        assertEquals(0, Double.valueOf(10.00).compareTo(statistics.getMax()));
        assertEquals(0, Double.valueOf(1.00).compareTo(statistics.getMin()));
        assertEquals(10, statistics.getCount());
        assertEquals(0, Double.valueOf(sum).compareTo(statistics.getSum()));
        assertEquals(0, Double.valueOf(avg).compareTo(statistics.getAverage()));
        assertNotNull(statistics.toString());

        //check JSON representation
        JSONObject statisticsJson = new JSONObject(statistics.toString());
        assertEquals(expectedStatisticsJson.toString(), statisticsJson.toString());
    }

    @Test
    public void shouldReturnTransactionStatisticsAndSkipOldTransaction() {
        //given
        int amountOfTransactions = 15;
        int amountOfValidTransactions = 10;
        double sum = 0.00;
        for (int i = 1; i <= amountOfTransactions; i++) {
            Transaction tz;

            if (i <= amountOfValidTransactions) {
                long timestamp = Instant.now().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
                tz = new Transaction(timestamp, i);
                sum += i;
            } else {
                //adding outdated transactions
                long timestamp = Instant.now().atZone(ZoneOffset.UTC).minusSeconds(60).toInstant().toEpochMilli();
                tz = new Transaction(timestamp, i);
            }

            this.transactionService.addTransaction(tz);
        }

        double avg = sum / amountOfValidTransactions;

        //when
        transactionService.calculateTransactionStatistics();
        TransactionStatistics statistics = transactionService.getTransactionStatistics();

        //then
        assertNotNull(statistics);
        assertEquals(0, Double.valueOf(10.00).compareTo(statistics.getMax()));
        assertEquals(0, Double.valueOf(1.00).compareTo(statistics.getMin()));
        assertEquals(10, statistics.getCount());
        assertEquals(0, Double.valueOf(sum).compareTo(statistics.getSum()));
        assertEquals(0, Double.valueOf(avg).compareTo(statistics.getAverage()));
    }

    @Test
    public void shouldReturnTransactionZeroStatisticsWhenNoTransactionInStorage() throws JSONException {
        //given
        JSONObject expectedZeroStatisticsJson = new JSONObject("{\"sum\": 0.00, " +
                "\"avg\": 0.00, \"max\": 0.00, \"min\": 0.00, \"count\": 0}");

        //when
        TransactionStatistics statistics = transactionService.getTransactionStatistics();

        //then
        assertNotNull(statistics);

        //check JSON representation
        JSONObject statisticsJson = new JSONObject(statistics.toString());
        assertEquals(expectedZeroStatisticsJson.toString(), statisticsJson.toString());
        assertEquals(0, Double.valueOf(0.00).compareTo(statistics.getSum()));
        assertEquals(0, Double.valueOf(0.00).compareTo(statistics.getAverage()));
        assertEquals(0, statistics.getCount());
    }
}
