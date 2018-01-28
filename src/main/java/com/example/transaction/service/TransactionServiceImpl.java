package com.example.transaction.service;

import com.example.transaction.models.Transaction;
import com.example.transaction.models.TransactionStatistics;
import com.example.transaction.service.storage.TransactionStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * Class provides logic for Transaction actions:
 * - storing transactions @see #addTransaction
 * - retrieving transaction statistics
 * <p/>
 * Method addTransaction stores transaction in TransactionStorage if transaction is not older than 60 seconds.
 * Method getAllTransactions returns all stored transactions.
 * Method getStatistics returns the statistic based on transactions of the last 60 seconds.
 * <p/>
 * Uses UUID value as key in storage. Values generated randomly.
 * Solves case when transactions could happen at the same time in concurrent environment.
 *
 * @see UUID
 * @see UUID#randomUUID
 * <p/>
 * Created by nikolay.odintsov on 28.01.18.
 */

@Service
public class TransactionServiceImpl implements TransactionService<UUID, Transaction> {
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
    /**
     * MAX_STORAGE_CAPACITY defines size of storage and represents precision of time in millis.
     */
    public static final int MAX_STORAGE_CAPACITY = 60 * 1000;

    private TransactionStorage transactions = new TransactionStorage<UUID, Transaction>(MAX_STORAGE_CAPACITY);

    @Override
    public boolean addTransaction(Transaction transaction) {
        if (transaction.isOutdated()) {
            logger.debug(transaction + " is outdated. Not stored.");

            return false;
        }

        this.transactions.put(UUID.randomUUID(), transaction);
        logger.debug(transaction + " stored.");

        return true;
    }

    @Override
    public Map<UUID, Transaction> getAllTransactions() {
        return this.transactions.getAll();
    }

    @Override
    public TransactionStatistics getTransactionStatistics() {
        TransactionStatistics statistics = this.getAllTransactions().values().stream()
                .filter(Transaction::isValid)
                .mapToDouble(Transaction::getAmount)
                .collect(TransactionStatistics::new,
                        TransactionStatistics::accept,
                        TransactionStatistics::combine);

        return statistics;
    }

}
