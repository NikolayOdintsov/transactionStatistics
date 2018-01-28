package com.example.transaction.models;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

/**
 * Class represents transaction.
 * Contains data:
 * - timestamp. Transaction time in epoch in millis in UTC time zone.
 * - amount. Transaction amount.
 * <p/>
 * Provides logic for check if transaction time is less than 60 sec.
 *
 * @see #isOutdated
 * <p/>
 * Created by nikolay.odintsov on 28.01.18.
 */
public class Transaction {
    /**
     * TTL is time to live for transaction in seconds.
     * Transaction older that this value considered as outdated.
     *
     * @see #isOutdated
     */
    public static final int TTL = 60;

    private double amount;
    /**
     * transaction time in epoch in millis in UTC time zone.
     *
     * @see #setTimestamp
     */
    private long timestamp;

    public Transaction() {
    }

    public Transaction(long timestamp, double amount) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;

        Transaction that = (Transaction) o;

        if (Double.compare(that.amount, amount) != 0) return false;
        return timestamp == that.timestamp;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(amount);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "amount=" + amount +
                ", timestamp=" + timestamp +
                '}';
    }

    /**
     * Transaction older that 60 seconds considered as outdated.
     *
     * @return boolean.
     * True - when transaction done more than 60 sec.
     * False - when transaction done less than 60 sec.
     */
    public boolean isOutdated() {
        Instant instantNow = Instant.now().atZone(ZoneOffset.UTC).toInstant();
        Instant transactionTime = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.UTC).toInstant();
        return Duration.between(transactionTime, instantNow).getSeconds() >= TTL;
    }

    public boolean isValid() {
        return !this.isOutdated();
    }

}
