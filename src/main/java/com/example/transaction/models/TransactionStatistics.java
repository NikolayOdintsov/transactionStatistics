package com.example.transaction.models;

import java.util.DoubleSummaryStatistics;

/**
 * Class represents transactions statistics.
 * Reuses {@link DoubleSummaryStatistics} class.
 * Overrides method toString to return string in JSON format.
 * Holds values:
 * - sum. Is a double specifying the total sum of transaction value.
 * - avg. Is a double specifying the average amount of transaction value.
 * - max. Is a double specifying single highest transaction value.
 * - min. Is a double specifying single lowest transaction value.
 * - count. Is a long specifying the total number of transactions.
 *
 * @see DoubleSummaryStatistics
 * <p/>
 * Created by nikolay.odintsov on 28.01.18.
 */
public class TransactionStatistics extends DoubleSummaryStatistics {
    @Override
    public String toString() {
        return String.format(
                "{\"sum\": %.2f, \"avg\": %.2f, \"max\": %.2f, \"min\": %.2f, \"count\": %d}",
                getSum(),
                getAverage(),
                getMax() == Double.NEGATIVE_INFINITY ? 0 : getMax(),
                getMin() == Double.POSITIVE_INFINITY ? 0 : getMin(),
                getCount());
    }
}