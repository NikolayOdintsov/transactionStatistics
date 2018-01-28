package com.example.transaction.service;

import com.example.transaction.models.Transaction;
import com.example.transaction.models.TransactionStatistics;

import java.util.Map;

/**
 * Provides logic for Transaction actions:
 * - storing transactions.
 * - retrieving transaction statistics.
 * <p/>
 * Created by nikolay.odintsov on 28.01.18.
 */
public interface TransactionService<K, V> {

    boolean addTransaction(Transaction transaction);

    Map<K, V> getAllTransactions();

    TransactionStatistics getTransactionStatistics();
}
