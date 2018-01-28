package com.example.transaction.models;

import org.junit.Test;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by nikolay.odintsov on 28.01.18.
 */
public class TransactionTest {

    @Test
    public void shouldDefineIsTransactionOutdated() throws Exception {
        //given
        long timestamp = Instant.now().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
        double amount = ThreadLocalRandom.current().nextDouble(1.0, 100.0);

        Transaction validTransaction = new Transaction(timestamp, amount);
        Transaction invalidTransaction = new Transaction(timestamp - (Transaction.TTL * 1000), amount);
        //1 sec. before been invalid
        Transaction validTransaction2 = new Transaction(timestamp - ((Transaction.TTL - 1) * 1000), amount);

        //when
        boolean validResult = validTransaction.isOutdated();
        boolean validResult2 = validTransaction2.isOutdated();
        boolean invalidResult = invalidTransaction.isOutdated();

        //then
        assertNotNull(validResult);
        assertFalse(validResult);

        assertNotNull(validTransaction2);
        assertFalse(validResult2);

        assertNotNull(invalidResult);
        assertTrue(invalidResult);
    }
}